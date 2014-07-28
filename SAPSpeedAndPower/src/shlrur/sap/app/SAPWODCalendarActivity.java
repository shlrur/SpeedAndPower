package shlrur.sap.app;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SAPWODCalendarActivity extends Activity {
	
	private String mDate;
	
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
	
	private LinearLayout[] cPick;
	private TextView[] cDate;
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
		
		setContentView(R.layout.sap_wod_calendar);

		// layout xml 의 id 가져오기
		cPick = new LinearLayout[42];
		cDate = new TextView[42];
		for(int i=0 ; i<42 ; i++){
			cPick[i] 	= (LinearLayout)findViewById(cPick_id[i]);
			cDate[i] 	= (TextView)findViewById(cDate_id[i]);
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
			
			cPick[i].setClickable(false);
			cPick[i].setOnClickListener(null);
		}
		
		for(int i=mWeekofFirstDay ; i<mWeekofFirstDay+mNumofDayofMonth ; i++) {
			String t_date;
			final String clicklintener_date;
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
			
			// Click Listener 등록
			cPick[i].setOnClickListener(new OnClickListener() {
				String date = clicklintener_date;

				@Override
				public void onClick(View v) {
					//Log.d("SAP", date);
					Intent intent = new Intent();
					intent.putExtra("date", date);
					setResult(RESULT_OK, intent);
					finish();
				}
			});
		}
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