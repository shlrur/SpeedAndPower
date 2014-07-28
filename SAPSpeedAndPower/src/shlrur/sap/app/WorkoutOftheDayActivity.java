package shlrur.sap.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class WorkoutOftheDayActivity extends Activity {

	private LinearLayout mDatePick;
	private TextView mDateView;
	private TextView mDateYester, mDateTomorrow;
	private ViewPager mPager;
	private Button[] mWOD;
	
	private int mPrevPosition;
	
	private int mYear, mMonth, mDay, mWeek;
	private String[] dayOfWeek={"일요일","월요일","화요일","수요일","목요일","금요일","토요일"};
	private String mDate;
	
	private WODHtmlParse parse;
	
	private PagerAdapterClass mPagerAdapt;
	
	private ProgressDialog mProgDiag;
	
	private SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.workout_of_the_day_activity);
		mPrefs = getSharedPreferences("SAPpref", MODE_PRIVATE);
		
		mProgDiag = new ProgressDialog(WorkoutOftheDayActivity.this);
		mProgDiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgDiag.setTitle("Loading...");
		mProgDiag.setMessage("매일운동을 가져오는 중입니다");
		
		parse = new WODHtmlParse(this, mPrefs);
		
		mWOD = new Button[5];
		
		mWOD[0] = (Button) findViewById(R.id.wod_btn_1);
		mWOD[1] = (Button) findViewById(R.id.wod_btn_2);
		mWOD[2] = (Button) findViewById(R.id.wod_btn_3);
		mWOD[3] = (Button) findViewById(R.id.wod_btn_4);
		mWOD[4] = (Button) findViewById(R.id.wod_btn_5);
		
		mPager = (ViewPager) findViewById(R.id.wod_pager);
		
		mDatePick = (LinearLayout) findViewById(R.id.wod_date_pick);
		mDateView = (TextView) findViewById(R.id.wod_date);
		mDateYester = (TextView)findViewById(R.id.wod_date_yesterday);
		mDateTomorrow = (TextView)findViewById(R.id.wod_date_tomorrow);
		
		mDateYester.setText("<");
		mDateTomorrow.setText(">");
		
		mPrevPosition=0;
		mWOD[0].setEnabled(false);
		
		Date curDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
				
		mDate = sdf.format(curDate);
		
		mProgDiag.show();
		parse.getHtmltoText(mDate, mHandler);
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH) + 1;
		mDay = cal.get(Calendar.DAY_OF_MONTH);
		mWeek = cal.get(Calendar.DAY_OF_WEEK);
		mDateView.setText(mYear+"년 "+mMonth+"월 "+mDay+"일\n"+dayOfWeek[mWeek-1]);
		
		mPagerAdapt = new PagerAdapterClass(getApplicationContext());
		mPager.setAdapter(mPagerAdapt);		
		
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				//Log.d("SAP", position+" page is on");
				mWOD[position].setEnabled(false);
				mWOD[mPrevPosition].setEnabled(true);
				mPrevPosition = position;
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {}
			@Override
			public void onPageScrollStateChanged(int arg0) {}
		});
		
		for(int i=0 ; i<5 ; i++){
			final int th = i;
			mWOD[i].setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mPager.setCurrentItem(th);
				}
			});
		}
		
		mDatePick.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				new DatePickerDialog(WorkoutOftheDayActivity.this, mDateSetListener ,mYear, mMonth-1, mDay)
//				.show();
				Intent intent = new Intent(WorkoutOftheDayActivity.this, SAPWODCalendarActivity.class);
				intent.putExtra("date", mDate);
				startActivityForResult(intent, 0);
			}
		});
		
		mDateYester.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance();
				
				mYear = Integer.parseInt(mDate.substring(0, 4));
				mMonth = Integer.parseInt(mDate.substring(4, 6)) - 1;
				mDay = Integer.parseInt(mDate.substring(6, 8));
				
				cal.set(mYear, mMonth, mDay);
				cal.add(Calendar.DATE, -1);
				
				mYear = cal.get(Calendar.YEAR);
				mMonth = cal.get(Calendar.MONTH) + 1;
				mDay = cal.get(Calendar.DATE);
				
				Log.d("SAP", cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DATE));
				
				String _month, _day;
				if (mMonth < 10)
					_month = "0" + mMonth;
				else
					_month = "" + mMonth;
				if (mDay < 10)
					_day = "0" + mDay;
				else
					_day = "" + mDay;
				
				mDate = mYear + "" + _month + "" + _day;
				
				// get day_of_week
				mWeek = cal.get(Calendar.DAY_OF_WEEK);

				mDateView.setText(mYear+"년 "+mMonth+"월 "+mDay+"일\n"+dayOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1]);
				
				mProgDiag.show();
		        parse.getHtmltoText(mDate, mHandler);
		        //Log.d("SAP", "parse complete??");
		        
		        mPagerAdapt.notifyDataSetChanged();
			}
		});
		
		mDateTomorrow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Calendar cal = Calendar.getInstance();
				
				mYear = Integer.parseInt(mDate.substring(0, 4));
				mMonth = Integer.parseInt(mDate.substring(4, 6)) - 1;
				mDay = Integer.parseInt(mDate.substring(6, 8));
				
				cal.set(mYear, mMonth, mDay);
				cal.add(Calendar.DATE, 1);
				
				mYear = cal.get(Calendar.YEAR);
				mMonth = cal.get(Calendar.MONTH) + 1;
				mDay = cal.get(Calendar.DATE);
				
				Log.d("SAP", cal.get(Calendar.YEAR) + "-" + cal.get(Calendar.MONTH) + "-" + cal.get(Calendar.DATE));
				
				String _month, _day;
				if (mMonth < 10)
					_month = "0" + mMonth;
				else
					_month = "" + mMonth;
				if (mDay < 10)
					_day = "0" + mDay;
				else
					_day = "" + mDay;
				
				mDate = mYear + "" + _month + "" + _day;
				
				// get day_of_week
				mWeek = cal.get(Calendar.DAY_OF_WEEK);

				mDateView.setText(mYear+"년 "+mMonth+"월 "+mDay+"일\n"+dayOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1]);
				
				mProgDiag.show();
		        parse.getHtmltoText(mDate, mHandler);
		        //Log.d("SAP", "parse complete??");
		        
		        mPagerAdapt.notifyDataSetChanged();
			}
		});
	}

//	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
//		@Override
//		public void onDateSet(DatePicker view, int year, int month,	int day) {
//			month++;
//			String _month, _day;
//			if(month<10)	_month = "0"+month;
//			else			_month = ""+month;
//			if(day<10)		_day = "0" + day;
//			else			_day = "" + day;
//			
//			mDate = year+""+_month+""+_day;
//			
//			mYear = year;
//			mMonth = month;
//			mDay = day;
//			Calendar cal=new GregorianCalendar();
//	        cal.set(Calendar.YEAR, mYear);
//	        cal.set(Calendar.MONTH, mMonth-1);
//	        cal.set(Calendar.DAY_OF_MONTH, mDay);
//	        
//	        mDateView.setText(mYear+"년 "+mMonth+"월 "+mDay+"일 "+dayOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1]);
//	        
//	        mProgDiag.show();
//	        parse.getHtmltoText(mDate, mHandler);
//	        //Log.d("SAP", "parse complete??");
//	        
//	        mPagerAdapt.notifyDataSetChanged();
//		}
//	};
	/**
	 * PagerAdapter
	 */
	private class PagerAdapterClass extends PagerAdapter {

		private LayoutInflater mInflater;

		public PagerAdapterClass(Context c) {
			super();
			mInflater = LayoutInflater.from(c);
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public Object instantiateItem(View pager, int position) {
			View v = null;
			
			if (position == 0) { // 스트렝스
				v = mInflater.inflate(R.layout.wod_inflate_1, null);
				TextView wod1_tv = (TextView)v.findViewById(R.id.wod_1_tv);
				wod1_tv.setText(parse.getWOD(0));
			} else if (position == 1) { // 완초삽
				v = mInflater.inflate(R.layout.wod_inflate_2, null);
				TextView wod2_tv = (TextView)v.findViewById(R.id.wod_2_tv);
				wod2_tv.setText(parse.getWOD(1));
			} else if(position == 2) { // 역도
				v = mInflater.inflate(R.layout.wod_inflate_3, null);
				TextView wod3_tv = (TextView)v.findViewById(R.id.wod_3_tv);
				wod3_tv.setText(parse.getWOD(2));
			} else if(position == 3) { // 특수부대
				v = mInflater.inflate(R.layout.wod_inflate_4, null);
				TextView wod4_tv = (TextView)v.findViewById(R.id.wod_4_tv);
				wod4_tv.setText(parse.getWOD(3));
			} else { // 보디빌딩
				v = mInflater.inflate(R.layout.wod_inflate_5, null);
				TextView wod5_tv = (TextView)v.findViewById(R.id.wod_5_tv);
				wod5_tv.setText(parse.getWOD(4));
			}

			((ViewPager) pager).addView(v, 0);
			//Log.d("SAP", "pager init");
			return v;
		}

		@Override
		public void destroyItem(View pager, int position, Object view) {
			((ViewPager) pager).removeView((View) view);
		}

		@Override
		public boolean isViewFromObject(View pager, Object obj) {
			return pager == obj;
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {}

		@Override
		public Parcelable saveState() {	return null;}

		@Override
		public void startUpdate(View arg0) {}

		@Override
		public void finishUpdate(View arg0) {}
		
		@Override
		public int getItemPosition(Object object){
		     return POSITION_NONE;
		}
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if(requestCode == 0){
				String date = data.getStringExtra("date");
				int year, month, day;
				Log.d("SAP", date);
				
				mDate = date.replaceAll("-", "");
				year = Integer.parseInt(mDate.substring(0, 4));
				month = Integer.parseInt(mDate.substring(4, 6));
				day = Integer.parseInt(mDate.substring(6, 8));
				// get day_of_week
				Calendar cal = Calendar.getInstance();
				cal.set(year, month, day);

				mDateView.setText(year+"년 "+month+"월 "+day+"일\n"+dayOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1]);
				
				mProgDiag.show();
		        parse.getHtmltoText(mDate, mHandler);
		        //Log.d("SAP", "parse complete??");
		        
		        mPagerAdapt.notifyDataSetChanged();
			}
		}
		else if(resultCode == RESULT_CANCELED)
		{
			if(requestCode == 0)
				Toast.makeText(WorkoutOftheDayActivity.this, "날짜 변경이 취소되었습니다.", Toast.LENGTH_SHORT).show();
		}
	}
	
	Handler mHandler = new Handler(new Handler.Callback(){
		@Override
		public boolean handleMessage(Message msg) {
			switch(msg.what) {
			case 0:
				Log.d("SAP", "message!!");
				//Toast.makeText(WorkoutOftheDayActivity.this, "Message come", Toast.LENGTH_SHORT).show();
				mPagerAdapt.notifyDataSetChanged();
				mProgDiag.dismiss();
				break;
			}
			return false;
		}
	});
}
