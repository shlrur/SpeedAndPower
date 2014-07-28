package shlrur.sap.app;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

public class WODHtmlParse{
	private SAPDatabaseManagement mSAPDB;	
	private SQLiteDatabase mReadDB;
	
	private String[] wod;
	private String[] mStrength = 	{"스콰트", "데드리프트", "카프 레이즈", "벤치 프레스", "바벨 로우", "컬", "밀리터리 프레스", "무게 턱걸이 또는 풀다운"};
	private float[] mRM;
	
	private String url;
	
	private SharedPreferences mPrefs;
	
	public WODHtmlParse(Context context, SharedPreferences pref){
		wod = new String[5];
		mRM = new float[8];
		
		mPrefs = pref;
		
		mSAPDB = new SAPDatabaseManagement(context);
//		mWrightDB = mSAPDB.getWritableDatabase();
		mReadDB = mSAPDB.getReadableDatabase();
		
		Cursor cursor = mReadDB.rawQuery("SELECT * FROM db_strength", null);
		cursor.moveToLast();
//		Log.d("SAP", cursor.getString(0)+ " "+cursor.getString(1)+ " "+cursor.getString(2)+ " "+cursor.getString(3)+ " "+cursor.getString(4)+ " "
//			  +cursor.getString(5)+ " "+cursor.getString(6)+ " "+cursor.getString(7)+ " "+cursor.getString(8)+ " "+cursor.getString(9));
		for(int i=0 ; i<8 ; i++)
			mRM[i] = Float.parseFloat(cursor.getString(2+i));
		
		cursor.close();
		mSAPDB.close();
		mReadDB.close();
	}

	/**
	 * date에 해당하는 날짜로 매일운동이 세팅된다.
	 * @param date : 날짜. YYYYMMDD
	 */
	public void getHtmltoText(String date, Handler handler) {
		url = "http://speedandpower.co.kr/day_trainning/list.asp?regist_date=" + date;
		
		wod[0] = "";
		wod[1] = "";
		wod[2] = "";
		wod[3] = "";
		wod[4] = "";
		
		WODParseThread wod_thread = new WODParseThread(handler);
		
		wod_thread.start();
		
//		try {
//			wod_thread.join();
//		} catch (InterruptedException e) {
//			Log.d("SAP", "parse thread join FAIL!!");
//			e.printStackTrace();
//		}
	}
	
	class WODParseThread extends Thread{
		Handler mHandler;
		
		public WODParseThread(Handler handler) {
			mHandler = handler;
		}
		
		/* (non-Javadoc)
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			Source source = null;
			Log.d("SAP", "parse start..........");
			try {
				//Log.d("SAP", "source on!" + url);
				source = new Source(new URL(url));
				
				//Log.d("SAP", "source on");
				source.fullSequentialParse();
//				Log.d("SAP", source.getTextExtractor()+"");
				List<Element> div = source.getAllElementsByClass("table_1 mt_btn");

				for(int i=0 ; i<div.size() ; i++)
				{
					List<Element> cont = div.get(i).getAllElements(HTMLElementName.TD);
					for(int j=0 ; j<cont.size() ; j++)
					{
						StringTokenizer tokens = new StringTokenizer(cont.get(j).getContent().toString(), "<br>");
						if(i!=2)
							wod[i] += "\n";
						while(tokens.hasMoreTokens()){
							String temp = tokens.nextToken();
							if(i==0 || i==2)
								temp = temp.replace(",", "\n");
							temp = temp.replace("\n ", "\n");
							if(!temp.equals("\n"))
								wod[i] += temp.trim() + "\n";
							else
								wod[i] += temp;
						}
					}
//					Log.d("SAP", "--start--");
//					Log.d("SAP", wod[i]);
					wod[i] = wod[i].trim();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.d("SAP", "parse complete");
			
			updateStrength();
			updateWeightLifting();
			Log.d("SAP", "update complete");
			
			mHandler.sendEmptyMessage(0);
		}
	}

	/**
	 * Update Strength Weight. wod[0] is modified.
	 */
	synchronized private void updateStrength(){
		String tempString = wod[0];
		
		// "카프 레이즈" 가 아닌 "카프" 가 매일운동으로 올라왔을 때
		// "카프" 를 "카프 레이즈" 로 바꿈
		if(wod[0].indexOf("카프")!=-1 && wod[0].indexOf("카프 레이즈")==-1)
			wod[0] = wod[0].replace("카프", "카프 레이즈");
		
		// "무게 턱걸이(또는 풀다운)" 를 "무게 턱걸이 또는 풀다운" 으로 바꿈
		if(wod[0].indexOf("무게 턱걸이(또는 풀다운)")!=-1)
			wod[0] = wod[0].replace("무게 턱걸이(또는 풀다운)", "무게 턱걸이 또는 풀다운");
		
		//////// Be Pretty Format ////////
		// 1. 스트렝쓰 운동 이름만 한 줄에 넣기  
		for(int i=0 ; i<mRM.length ; i++) {
			if(wod[0].indexOf(mStrength[i]) != -1){
				wod[0] = wod[0].replace(mStrength[i], mStrength[i]+"\n");
			}
		}
		
		////////Be Pretty Format ////////
		// 2. 제일 앞 칸에 white space가 있으면 지움
		wod[0] = wod[0].replaceAll("\n ", "\n");
		
		int workoutFormat;
		
		if(wod[0].indexOf("마지막 세트") != -1){
			workoutFormat = 1;
		}
		else{
			workoutFormat = 2;
		}
		
		
		try{
			switch(workoutFormat){
			case 1	:
				wod[0] = modifyFormat1(wod[0]);
				break;
			case 2	:
				wod[0] = modifyFormat2(wod[0]);
				break;
			default	:
				break;
			}
		
			////////Be Pretty Format ////////
			// 3. 단위 기호 변경
			if(mPrefs.getString("WeightUnit", "kg").equals("lbs"))
				wod[0] = wod[0].replace("%)", "lbs");
			else
				wod[0] = wod[0].replace("%)", "kg");
			
			////////Be Pretty Format ////////
			// 4. 운동 종류 사이에 한칸을 띄운다.
			StringBuffer insertNewLine = null;
			String[] linebyline = wod[0].split("\n");
			for(int i=0 ; i<linebyline.length ; i++){
				insertNewLine = new StringBuffer(linebyline[i]);
				for(int j=0 ; j<mStrength.length ; j++){
					if(linebyline[i].indexOf(mStrength[j])!=-1)
						insertNewLine.insert(0, "\n");
				}
				linebyline[i] = insertNewLine.toString();
			}
			wod[0] = "";
			for(int i=0 ; i<linebyline.length ; i++)
				wod[0] += linebyline[i]+"\n";
			wod[0] = wod[0].trim();
			
			////////Be Pretty Format ////////
			// 5. "*" 대신 " X "로 변경
			wod[0] = wod[0].replace("*", " X ");
			
		}catch(Exception e){
			Log.e("SAP", "Parsing Error!! : "+e.toString());
			wod[0] = "매일운동 포맷에 이상이 있습니다.\n\n" + 
					tempString;
		}
		
	}
	
	synchronized private String modifyFormat1(String workoutString) {
		int result_start = 0;
		int result_end = 0;
		DecimalFormat dFormat = new DecimalFormat("####.#");
		StringBuffer target = new StringBuffer(workoutString);

		ArrayList<SAPStrengthRMClass> mMain = new ArrayList<SAPStrengthRMClass>();
		ArrayList<SAPStrengthRMClass> mSub = new ArrayList<SAPStrengthRMClass>();
		
		// sub 추출
		while(result_start != -1){
			result_start = target.indexOf("(마지막 세트 무게의", result_start+1);
			if(result_start != -1){
				result_end = target.indexOf("%)", result_start+1);
				mSub.add(new SAPStrengthRMClass(result_start, result_end+1, Float.parseFloat(target.substring(result_start+12, result_end)), -1));
			}
		}
		
		// main 추출
		result_start = 0;
		result_end = 0;
		while(result_start != -1){
			result_start = target.indexOf("(1RM", result_start+1);
			if(result_start != -1){
				result_end = target.indexOf("%)", result_start+1);
				mMain.add(new SAPStrengthRMClass(result_start, result_end+1, Float.parseFloat(target.substring(result_start+5, result_end)), -1));
			}
		}
		
		int result=-1;
		for(int i=0 ; i<mMain.size() ; i++) {
			for(int j=0 ; j<mStrength.length ; j++) {
				if(target.indexOf((int)(i+1)+".	"+mStrength[j], result+1) != -1){
					result = target.indexOf((int)(i+1)+". "+mStrength[j], result);
					
					mMain.get(i).kind = j;
				}
			}
		}
		
		int mMainCnt	= mMain.size()-1;
		int mSubCnt 	= mSub.size()-1;
		Log.d("SAP", mMainCnt+"");
		Log.d("SAP", mSubCnt+"");
		int mainStart;
		float aRM = 0;
		for(int i=0 ; i<mMain.size()+mSub.size() ; i++) {
			if(mMainCnt != -1)
				mainStart = mMain.get(mMainCnt).start;
			else
				mainStart = 0;
			
			if( mainStart > mSub.get(mSubCnt).start ) {
			// Main 이 선택될 때
				aRM = mMain.get(mMainCnt).value*mRM[mMain.get(mMainCnt).kind]/100;
				if(mPrefs.getString("WeightUnit", "kg").equals("lbs")){
					aRM = (float) (aRM*2.204623);
				}
				target.replace(mMain.get(mMainCnt).start+6, mMain.get(mMainCnt).end-1, dFormat.format((float)aRM));
				mMainCnt--;
			}
			else {
			// Sub 가 선택될 때
				target.replace(mSub.get(mSubCnt).start+12, mSub.get(mSubCnt).end-1, dFormat.format((float)(mSub.get(mSubCnt).value*aRM/(float)100)));
				mSubCnt--;
			}
		}
		
		// *( 아닌데에 * 넣기
		Pattern p = Pattern.compile( "[^*][(]" );
		Matcher m = p.matcher(target.toString());
		
		StringBuffer sb = new StringBuffer();
		
		while(m.find())
			m.appendReplacement(sb, m.group().substring(0, m.group().length()-2)+"회*(");
		
		m.appendTail(sb);
		target = sb;
		
		// "(마지막 세트 무게의 " & "(1RM의 " 삭제
		workoutString = target.toString();
		workoutString = workoutString.replace("(마지막 세트 무게의 ", "");
		workoutString = workoutString.replace("(1RM의 ", "");
				
		return workoutString;
	}
	
	synchronized private String modifyFormat2(String workoutString) {
		DecimalFormat dFormat = new DecimalFormat("####.#");
		/*
		 * parser start
		 */
		String[] linebyline = workoutString.split("\n");
		float aRM = 0.0f;
		Log.d("SAP", "line num is" + linebyline.length);
		// read line by line
		for(int i=0 ; i<linebyline.length ; i++){
			StringBuffer target = new StringBuffer(linebyline[i]);
			
			// find the kind of strength workout
			for(int j=0 ; j<mStrength.length ; j++){
				// find!!
				if(target.indexOf(mStrength[j])!=-1){
					aRM = mRM[j]; // kg 단위의 1RM
//					target.insert(0, "\n");
				}
			}
			
			// find the "(1RM의 " and "%)"
			if(target.indexOf("(1RM의 ") != -1 && target.indexOf("%)") != -1){
				float weight = Float.parseFloat(target.substring(target.indexOf("(1RM의 ")+6, target.indexOf("%)")))*aRM/100;
				// lbs
				if(mPrefs.getString("WeightUnit", "kg").equals("lbs"))
					weight = (float) (weight*2.204623);
				
				target.replace(target.indexOf("(1RM의 "), target.indexOf("%)"), dFormat.format((float)weight));
			}
			linebyline[i] = target.toString();
		}
		

		// integrate
		workoutString="";
		for(int i=0 ; i<linebyline.length ; i++)
			workoutString += linebyline[i]+"\n";
		
		workoutString = workoutString.trim();		
		
		return workoutString;
	}
	
	class SAPStrengthRMClass{
		int 	start, end;
		float 	value;
		int		kind;
		public SAPStrengthRMClass(int _start, int _end, float _value, int _kind) {
			start 	= _start;	//"("
			end 	= _end;		//")"
			value 	= _value;
			kind 	= _kind;
		}
	}

	/**
	 * Update Weight Lifting Weight
	 */
	synchronized private void updateWeightLifting(){
		String[] mWeightLifting = 	{"클린", "클린 앤 저크", "클린 풀", "클린 풀(%of 클린)", "클린 풀(% of 클린)", 
									"파워 클린", "파워클린 앤 파워저크", "파워 클린 앤 저크", "파워 저크", "저크(랙에서)", 
									"스내치", "스내치(무릎에서)", "스내치 풀", "스내치 풀 (% of 스내치)",
									"스내치 밸런스", "파워 스내치", "루마니안 풀", "백 스콰트", "프론트 스콰트", 
									"굿모닝", "비하인드 넥 프레스", "벤치 프레스", "푸샵", "행잉레그레이즈", "백레이즈"};
				
		/*
		 * 역도 운동 종류 
		 */
		for(int i=0 ; i<mWeightLifting.length ; i++) {
			if(wod[2].indexOf(mWeightLifting[i]) != -1 && i!=0 && i!=10 && i!=2 && i!=12 && i!=5 && i!=1){
				wod[2] = wod[2].replace(mWeightLifting[i], "\n"+mWeightLifting[i]+"\n ");
			}
		}
		
		// 클린
		if(wod[2].indexOf(mWeightLifting[1]) == -1 && wod[2].indexOf(mWeightLifting[2]) == -1 && wod[2].indexOf(mWeightLifting[3]) == -1 &&
		   wod[2].indexOf(mWeightLifting[4]) == -1 && wod[2].indexOf(mWeightLifting[5]) == -1 && wod[2].indexOf(mWeightLifting[6]) == -1 &&
		   wod[2].indexOf(mWeightLifting[7]) == -1 && wod[2].indexOf(mWeightLifting[0]) != -1)
		{
			wod[2] = wod[2].replace(mWeightLifting[0], "\n"+mWeightLifting[0]+"\n ");
		}
		
		// 스내치
		if(wod[2].indexOf(mWeightLifting[11]) == -1 && wod[2].indexOf(mWeightLifting[12]) == -1 && wod[2].indexOf(mWeightLifting[13]) == -1 && 
		   wod[2].indexOf(mWeightLifting[14]) == -1 && wod[2].indexOf(mWeightLifting[15]) == -1 && wod[2].indexOf(mWeightLifting[10]) != -1)
		{
			wod[2] = wod[2].replace(mWeightLifting[10], "\n"+mWeightLifting[10]+"\n ");
		}
		
		// 클린 풀
		if(wod[2].indexOf(mWeightLifting[3]) == -1 && wod[2].indexOf(mWeightLifting[4]) == -1 && wod[2].indexOf(mWeightLifting[2]) != -1)
		{
			wod[2] = wod[2].replace(mWeightLifting[2], "\n"+mWeightLifting[2]+"\n ");
		}
		
		// 스내치 풀
		if(wod[2].indexOf(mWeightLifting[13]) == -1 && wod[2].indexOf(mWeightLifting[12]) != -1)
		{
			wod[2] = wod[2].replace(mWeightLifting[12], "\n"+mWeightLifting[12]+"\n ");
		}
		
		// 파워 클린
		if(wod[2].indexOf(mWeightLifting[7]) == -1 && wod[2].indexOf(mWeightLifting[5]) != -1)
		{
			wod[2] = wod[2].replace(mWeightLifting[5], "\n"+mWeightLifting[5]+"\n ");
		}
		
		// 클린 앤 저크
		if(wod[2].indexOf(mWeightLifting[7]) == -1 && wod[2].indexOf(mWeightLifting[1]) != -1)
		{
			wod[2] = wod[2].replace(mWeightLifting[1], "\n"+mWeightLifting[1]+"\n ");
		}
		
		wod[2] = wod[2].replace("  ", " ");
		wod[2] = wod[2].replace("\n ", "\n");
		
		wod[2] = wod[2].trim();
	}
	
	/**
	 * 매일운동 5종목 중에서 하나의 종목을 가져온다.
	 * @param i : 스트렝스, 완초삽, 역도, 특수부대, 보디빌딩
	 * @return wod[i] : 5의 wod 중에서 i번째 wod를 return한다.
	 */
	public String getWOD(int i) {
		return wod[i];
	}
}
