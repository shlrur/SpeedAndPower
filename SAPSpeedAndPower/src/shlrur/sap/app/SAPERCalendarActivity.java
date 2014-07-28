package shlrur.sap.app;

import java.util.Calendar;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SAPERCalendarActivity extends Activity {
	
	private SAPDatabaseManagement mSAPDB;
	private SQLiteDatabase mReadDB;
//	private SQLiteDatabase mWrightDB;
	
	private Cursor mCursor;
	
//	private int mYear, mMonth, mDay, mFirstWeekofMonth, mNumofDayofMonth;
	private String mDate;
//	private String[] dayOfWeek={"일요일","월요일","화요일","수요일","목요일","금요일","토요일"};// 1,2,3,4,5,6,7
	
	private int[] cPick_id = 	{R.id.calendar_0_0, R.id.calendar_0_1, R.id.calendar_0_2, R.id.calendar_0_3, R.id.calendar_0_4, R.id.calendar_0_5, R.id.calendar_0_6,
								 R.id.calendar_1_0, R.id.calendar_1_1, R.id.calendar_1_2, R.id.calendar_1_3, R.id.calendar_1_4, R.id.calendar_1_5, R.id.calendar_1_6,
								 R.id.calendar_2_0, R.id.calendar_2_1, R.id.calendar_2_2, R.id.calendar_2_3, R.id.calendar_2_4, R.id.calendar_2_5, R.id.calendar_2_6,
								 R.id.calendar_3_0, R.id.calendar_3_1, R.id.calendar_3_2, R.id.calendar_3_3, R.id.calendar_3_4, R.id.calendar_3_5, R.id.calendar_3_6,
								 R.id.calendar_4_0, R.id.calendar_4_1, R.id.calendar_4_2, R.id.calendar_4_3, R.id.calendar_4_4, R.id.calendar_4_5, R.id.calendar_4_6,
								 R.id.calendar_5_0, R.id.calendar_5_1, R.id.calendar_5_2, R.id.calendar_5_3, R.id.calendar_5_4, R.id.calendar_5_5, R.id.calendar_5_6};
	
	private int[] cDate_id = 	{R.id.calendar_0_0_date, R.id.calendar_0_1_date, R.id.calendar_0_2_date, R.id.calendar_0_3_date, R.id.calendar_0_4_date, R.id.calendar_0_5_date, R.id.calendar_0_6_date,
			 					 R.id.calendar_1_0_date, R.id.calendar_1_1_date, R.id.calendar_1_2_date, R.id.calendar_1_3_date, R.id.calendar_1_4_date, R.id.calendar_1_5_date, R.id.calendar_1_6_date,
			 					 R.id.calendar_2_0_date, R.id.calendar_2_1_date, R.id.calendar_2_2_date, R.id.calendar_2_3_date, R.id.calendar_2_4_date, R.id.calendar_2_5_date, R.id.calendar_2_6_date,
			 					 R.id.calendar_3_0_date, R.id.calendar_3_1_date, R.id.calendar_3_2_date, R.id.calendar_3_3_date, R.id.calendar_3_4_date, R.id.calendar_3_5_date, R.id.calendar_3_6_date,
			 					 R.id.calendar_4_0_date, R.id.calendar_4_1_date, R.id.calendar_4_2_date, R.id.calendar_4_3_date, R.id.calendar_4_4_date, R.id.calendar_4_5_date, R.id.calendar_4_6_date,
			 					 R.id.calendar_5_0_date, R.id.calendar_5_1_date, R.id.calendar_5_2_date, R.id.calendar_5_3_date, R.id.calendar_5_4_date, R.id.calendar_5_5_date, R.id.calendar_5_6_date};
	
	private int[] cImage_id = 	{R.id.calendar_0_0_image, R.id.calendar_0_1_image, R.id.calendar_0_2_image, R.id.calendar_0_3_image, R.id.calendar_0_4_image, R.id.calendar_0_5_image, R.id.calendar_0_6_image,
			 					 R.id.calendar_1_0_image, R.id.calendar_1_1_image, R.id.calendar_1_2_image, R.id.calendar_1_3_image, R.id.calendar_1_4_image, R.id.calendar_1_5_image, R.id.calendar_1_6_image,
			 					 R.id.calendar_2_0_image, R.id.calendar_2_1_image, R.id.calendar_2_2_image, R.id.calendar_2_3_image, R.id.calendar_2_4_image, R.id.calendar_2_5_image, R.id.calendar_2_6_image,
			 					 R.id.calendar_3_0_image, R.id.calendar_3_1_image, R.id.calendar_3_2_image, R.id.calendar_3_3_image, R.id.calendar_3_4_image, R.id.calendar_3_5_image, R.id.calendar_3_6_image,
			 					 R.id.calendar_4_0_image, R.id.calendar_4_1_image, R.id.calendar_4_2_image, R.id.calendar_4_3_image, R.id.calendar_4_4_image, R.id.calendar_4_5_image, R.id.calendar_4_6_image,
			 					 R.id.calendar_5_0_image, R.id.calendar_5_1_image, R.id.calendar_5_2_image, R.id.calendar_5_3_image, R.id.calendar_5_4_image, R.id.calendar_5_5_image, R.id.calendar_5_6_image};
	
	private int[] cRecord_id = 	{R.id.calendar_0_0_record, R.id.calendar_0_1_record, R.id.calendar_0_2_record, R.id.calendar_0_3_record, R.id.calendar_0_4_record, R.id.calendar_0_5_record, R.id.calendar_0_6_record,
			 					 R.id.calendar_1_0_record, R.id.calendar_1_1_record, R.id.calendar_1_2_record, R.id.calendar_1_3_record, R.id.calendar_1_4_record, R.id.calendar_1_5_record, R.id.calendar_1_6_record,
			 					 R.id.calendar_2_0_record, R.id.calendar_2_1_record, R.id.calendar_2_2_record, R.id.calendar_2_3_record, R.id.calendar_2_4_record, R.id.calendar_2_5_record, R.id.calendar_2_6_record,
			 					 R.id.calendar_3_0_record, R.id.calendar_3_1_record, R.id.calendar_3_2_record, R.id.calendar_3_3_record, R.id.calendar_3_4_record, R.id.calendar_3_5_record, R.id.calendar_3_6_record,
			 					 R.id.calendar_4_0_record, R.id.calendar_4_1_record, R.id.calendar_4_2_record, R.id.calendar_4_3_record, R.id.calendar_4_4_record, R.id.calendar_4_5_record, R.id.calendar_4_6_record,
			 					 R.id.calendar_5_0_record, R.id.calendar_5_1_record, R.id.calendar_5_2_record, R.id.calendar_5_3_record, R.id.calendar_5_4_record, R.id.calendar_5_5_record, R.id.calendar_5_6_record};
	
	private LinearLayout[] cPick;
	private TextView[] cDate;
	private TextView[] cImage;
	private TextView[] cRecord;
	private TextView mMonthBefore, mMonthAfter, mMonthtv;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// 배경 흐리게 하는것
		WindowManager.LayoutParams window = new WindowManager.LayoutParams();
		window.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
		window.dimAmount = 0.5f;
		getWindow().setAttributes(window);
		
		setContentView(R.layout.sap_er_calendar);

		// layout xml 의 id 가져오기
		cPick = new LinearLayout[42];
		cDate = new TextView[42];
		cImage = new TextView[42];
		cRecord = new TextView[42];
		for(int i=0 ; i<42 ; i++){
			cPick[i] 	= (LinearLayout)findViewById(cPick_id[i]);
			cDate[i] 	= (TextView)findViewById(cDate_id[i]);
			cImage[i] 	= (TextView)findViewById(cImage_id[i]);
			cRecord[i] 	= (TextView)findViewById(cRecord_id[i]);
		}
		mMonthBefore = (TextView)findViewById(R.id.calendar_month_before);
		mMonthAfter = (TextView)findViewById(R.id.calendar_month_after);
		mMonthtv = (TextView)findViewById(R.id.calendar_month);
		
		mMonthBefore.setText("<");
		mMonthAfter.setText(">");
		
		// intent로 날짜 받아오기
		Intent get_intent = getIntent();
		mDate = get_intent.getStringExtra("date");		
//		mFirstWeekofMonth = getFirstdayWeekOfMonth(mDate);
//		mNumofDayofMonth = getNumofDayofMonth(mDate);
//		Log.d("SAP", mDate + "  " + mFirstWeekofMonth);
//		Log.d("SAP", mDate + "  " + mNumofDayofMonth);
		
		mMonthBefore.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int year, month, day;
				Calendar cal = Calendar.getInstance();
				
				year 	= Integer.parseInt(mDate.substring(0, 4));
				month 	= Integer.parseInt(mDate.substring(4, 6));
				day		= Integer.parseInt(mDate.substring(6, 8));
				
				cal.set(year, month-1, day);
				cal.add(Calendar.MONTH, -1);
				
				year	= cal.get(Calendar.YEAR);
				month	= cal.get(Calendar.MONTH)+1;
				day 	= cal.get(Calendar.DATE);
				//Log.d("SAP", year+" "+month+" "+day);
				
				mDate = Integer.toString(year);
				if(month<10)
					mDate+="0"+month;
				else
					mDate+=month;
				if(day<10)
					mDate+="0"+day;
				else
					mDate+=day;
				DrawCalendarofMonth(mDate);
			}
		});
		
		mMonthAfter.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				int year, month, day;
				Calendar cal = Calendar.getInstance();
				
				year 	= Integer.parseInt(mDate.substring(0, 4));
				month 	= Integer.parseInt(mDate.substring(4, 6));
				day		= Integer.parseInt(mDate.substring(6, 8));
				
				cal.set(year, month-1, day);
				cal.add(Calendar.MONTH, 1);
				
				year	= cal.get(Calendar.YEAR);
				month	= cal.get(Calendar.MONTH)+1;
				day 	= cal.get(Calendar.DATE);
				//Log.d("SAP", year+" "+month+" "+day);
				
				mDate = Integer.toString(year);
				if(month<10)
					mDate+="0"+month;
				else
					mDate+=month;
				if(day<10)
					mDate+="0"+day;
				else
					mDate+=day;
				DrawCalendarofMonth(mDate);
			}
		});
		
		DrawCalendarofMonth(mDate);
	}

	@Override
	protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
		super.onApplyThemeResource(theme, resid, first);

		// no background panel is shown
		theme.applyStyle(android.R.style.Theme_Panel, true);
	}
	
	private void DrawCalendarofMonth(String _date){
		int year, month/*, day*/;
		int mWeekofFirstDay;
		int mNumofDayofMonth;
		String date="";
		
		mSAPDB = new SAPDatabaseManagement(SAPERCalendarActivity.this);
		mReadDB = mSAPDB.getReadableDatabase();
		
		year = Integer.parseInt(_date.substring(0, 4));
		month = Integer.parseInt(_date.substring(4, 6));
//		day = Integer.parseInt(_date.substring(6, 8));
		
		mMonthtv.setText(year+"년 "+month+"월");
		
		date = Integer.toString(year);
		if(month<10)
			date += "0"+month;
		else
			date += month;
		
		mWeekofFirstDay = getFirstdayWeekOfMonth(_date)-1;
		mNumofDayofMonth = getNumofDayofMonth(_date);
		
		for(int i=0 ; i<42 ; i++) {
			// 배경을 일단 회색으로 다 채우기
			cPick[i].setBackgroundColor(Color.rgb(80, 80, 80));
			cDate[i].setText("");
			cRecord[i].setBackgroundColor(0);
			cImage[i].setBackgroundColor(0);
			
			cPick[i].setClickable(false);
			cPick[i].setOnClickListener(null);
		}
		
		for(int i=mWeekofFirstDay ; i<mWeekofFirstDay+mNumofDayofMonth ; i++) {
			String t_date;
			final String clicklintener_date;
//			final int _i = i;
			int t_day = i-mWeekofFirstDay+1;
			//배경 세팅
			cPick[i].setBackgroundResource(R.drawable.calendar_date_pick_selector);
			cDate[i].setText(Integer.toString(t_day));
			
			if(t_day<10)
				t_date = date+"0"+t_day;
			else
				t_date = date+t_day;
			
			t_date = t_date.substring(0, 4)+"-"+t_date.substring(4, 6)+"-"+t_date.substring(6, 8);
			clicklintener_date = t_date;
			//Log.d("SAP", t_date);
			
			mCursor = mReadDB.rawQuery("SELECT * FROM db_exerciserecord where date='"+t_date+"'", null);
			if(mCursor.getCount()!=0){
				mCursor.moveToLast();
				if(mCursor.getString(1).replaceAll("\\W", "")!=""){
					if(mCursor.getString(1).indexOf("SAPmESsAgE:TeMpDeLeTe") == -1){
						cRecord[i].setBackgroundColor(Color.rgb(0,0,255));
					}
					else {
						cRecord[i].setBackgroundColor(Color.rgb(134,134,134));
					}
				}
				if(mCursor.getBlob(2)!=null)
					cImage[i].setBackgroundColor(Color.rgb(255,0,0));
			}
			mCursor.close();
			
			// Click Listener 등록
			cPick[i].setOnClickListener(new OnClickListener() {
				String date = clicklintener_date;
//				String record;
//				byte[] img;
				Dialog dialog;
				TextView tvRecord;
				ImageView ivImage;
				Button btYes, btNo;
				
				SAPDatabaseManagement mmSAPDB;
				SQLiteDatabase mmReadDB;
				Cursor mmCursor;
				@Override
				public void onClick(View v) {
					//Log.d("SAP", date);
					dialog = new Dialog( SAPERCalendarActivity.this, R.style.Dialog );
					dialog.setContentView(R.layout.sap_er_calendar_diag);
					dialog.setTitle("Brief Record");
					tvRecord = (TextView)dialog.findViewById(R.id.calendar_diag_record);
					ivImage = (ImageView)dialog.findViewById(R.id.calendar_diag_image);
					btYes = (Button)dialog.findViewById(R.id.calendar_diag_yes);
					btNo = (Button)dialog.findViewById(R.id.calendar_diag_no);
					
					// Scrolling Enabled
					tvRecord.setMovementMethod(new ScrollingMovementMethod());
					
					mmSAPDB = new SAPDatabaseManagement(SAPERCalendarActivity.this);
					mmReadDB = mSAPDB.getReadableDatabase();
					
					mmCursor = mmReadDB.rawQuery("SELECT * FROM db_exerciserecord where date='"+date+"'", null);
					if(mmCursor.getCount()!=0){
						mmCursor.moveToLast();
						if(mmCursor.getString(1).replaceAll("\\W", "")!=""){
							if(mmCursor.getString(1).indexOf("SAPmESsAgE:TeMpDeLeTe") == -1)
								tvRecord.setText(mmCursor.getString(1));
							else {
								tvRecord.setText(mmCursor.getString(1).replace("SAPmESsAgE:TeMpDeLeTe\n", ""));
							}
						}
						else
							tvRecord.setText("NO RECORD");
						
						if(mmCursor.getBlob(2)!=null){
							byte[] img_byte = mmCursor.getBlob(2);
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 4;
							ivImage.setImageBitmap(BitmapFactory.decodeByteArray(img_byte, 0, img_byte.length, options));
						}
						else{
							ivImage.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
						}
					}
					else {
						tvRecord.setText("NO RECORD");
						ivImage.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
					}
					mmCursor.close();
					mmReadDB.close();
					mmSAPDB.close();
					
					btYes.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent();
							intent.putExtra("date", date);
							setResult(RESULT_OK, intent);
							dialog.dismiss();
							finish();
						}
					});
					btNo.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					dialog.show();
				}
			});
		}
		
		mReadDB.close();
		mSAPDB.close();
	}
	
	private int getFirstdayWeekOfMonth(String Date) {
		int year, month;
		
		year = Integer.parseInt(Date.substring(0, 4));
		month = Integer.parseInt(Date.substring(4, 6));
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, 1);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	
	private int getNumofDayofMonth(String Date) {
		int year, month;
		
		year = Integer.parseInt(Date.substring(0, 4));
		month = Integer.parseInt(Date.substring(4, 6));
		
		Calendar cal = Calendar.getInstance();
		cal.set(year, month-1, 1);
		return cal.getActualMaximum(Calendar.DATE);
	}
}