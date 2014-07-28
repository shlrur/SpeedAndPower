package shlrur.sap.app;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ExerciseRecordActivity extends Activity {

	private SAPDatabaseManagement mSAPDB;
	private SQLiteDatabase mReadDB;
	private SQLiteDatabase mWrightDB;
	
	private Cursor mCursor;
	
	private TextView mDateView;
	private TextView mDateYester, mDateTomorrow;
	private LinearLayout mDatePick;
	private EditText mRecord;
	private ViewTouchImage mThumbnail;
	private Button mImage;
	private Button mSend, mCopyString/*, mCopyImage*/;
	
	private int mYear, mMonth, mDay, mWeek;
	private String[] dayOfWeek={"일요일","월요일","화요일","수요일","목요일","금요일","토요일"};
	private String mDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_record_layout);
		
		mDatePick = (LinearLayout)findViewById(R.id.exercise_record_date_pick);
		mDateYester = (TextView)findViewById(R.id.exercise_record_date_yesterday);
		mDateTomorrow = (TextView)findViewById(R.id.exercise_record_date_tomorrow);
		mDateView = (TextView)findViewById(R.id.exercise_record_date);
		mRecord = (EditText)findViewById(R.id.exercise_record_record);
		mThumbnail = (ViewTouchImage)findViewById(R.id.exercise_record_thumbnail_img);
		mImage = (Button)findViewById(R.id.exercise_record_edit_image);
		mSend = (Button)findViewById(R.id.exercise_record_btn_send);
		mCopyString = (Button)findViewById(R.id.exercise_record_btn_copy_string);
//		mCopyImage = (Button)findViewById(R.id.exercise_record_btn_copy_image);
		
		mDateYester.setText("<");
		mDateTomorrow.setText(">");
		
		Date curDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.KOREA);
				
		mDate = sdf.format(curDate);
				
		Calendar cal = Calendar.getInstance();
		cal.setTime(curDate);
		mYear = cal.get(Calendar.YEAR);
		mMonth = cal.get(Calendar.MONTH) + 1;
		mDay = cal.get(Calendar.DATE);
		mWeek = cal.get(Calendar.DAY_OF_WEEK);
		mDateView.setText(mYear+"년 "+mMonth+"월 "+mDay+"일\n"+dayOfWeek[mWeek-1]);
		
		getExerciseRecordfromDB();
		
		mDateYester.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveExerciseStringRecord();
				
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
				
				getExerciseRecordfromDB();
			}
		});
		
		mDateTomorrow.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveExerciseStringRecord();
				
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
				
				getExerciseRecordfromDB();
				
			}
		});
		
		mDatePick.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
//				new DatePickerDialog(ExerciseRecordActivity.this, mDateSetListener ,mYear, mMonth-1, mDay)
//				.show();
				Intent intent = new Intent(ExerciseRecordActivity.this, SAPERCalendarActivity.class);
				intent.putExtra("date", mDate);
				startActivityForResult(intent, 1);
			}
		});
		
		mImage.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				saveExerciseStringRecord();
				
				Intent intent = new Intent(ExerciseRecordActivity.this, ExerciseRecordImageActivity.class);
				intent.putExtra("date", mDate);
				startActivityForResult(intent, 0);
			}
		});
		
		mSend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveExerciseStringRecord();
				
				String sendString = getExerciseStringRecordfromDB();
				if(sendString.trim().equals("")) {
					Toast.makeText(ExerciseRecordActivity.this, "운동일지가 없습니다.", Toast.LENGTH_SHORT).show();
				}
				else {
					Intent intentSend = new Intent(Intent.ACTION_SEND);
					intentSend.putExtra(Intent.EXTRA_TEXT, sendString);
					intentSend.setType("text/plain");
					startActivity(Intent.createChooser(intentSend, "운동일지 보내기"));
				}
			}
		});
		
		mCopyString.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
				ClipData clip = ClipData.newPlainText("label", getExerciseStringRecordfromDB());
				clipboard.setPrimaryClip(clip);
				Toast.makeText(ExerciseRecordActivity.this, "운동일지가 클립보드에 복사되었습니다!", Toast.LENGTH_SHORT).show();
			}
		});
		
//		mCopyImage.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				ClipboardManager mClipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//			    ContentValues values = new ContentValues(2);
//			    values.put(MediaStore.Images.Media.MIME_TYPE, "Image/jpg");
//			    values.put(MediaStore.Images.Media.DATA, filename.getAbsolutePath());
//			    ContentResolver theContent = getContentResolver();
//			    Uri imageUri = theContent.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//			    ClipData theClip = ClipData.newUri(getContentResolver(), "Image", imageUri);
//			    mClipboard.setPrimaryClip(theClip);
//			}
//		});
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		saveExerciseStringRecord();
	}
	
//	protected void onResume() {
//		super.onResume();
//		Log.d("SAP", "onResume");
//		getExerciseRecordfromDB();
//	}
	
	private void saveExerciseStringRecord() {
		if(mRecord.getText().toString().trim().equals("")){
			String temp = getExerciseStringRecordfromDB();
			if(!temp.trim().equals("")){
				Toast.makeText(ExerciseRecordActivity.this, "운동 일지가 임시로 지워졌습니다.", Toast.LENGTH_SHORT).show();
				setExerciseRecordtoDB("SAPmESsAgE:TeMpDeLeTe\n"+temp);
			}
		}
		else{
			setExerciseRecordtoDB(mRecord.getText().toString());
		}
	}
	
	private String getExerciseStringRecordfromDB(){
		mSAPDB = new SAPDatabaseManagement(ExerciseRecordActivity.this);
		mReadDB = mSAPDB.getReadableDatabase();
		String tDate;
		tDate = mDate.substring(0, 4)+"-"+mDate.substring(4, 6)+"-"+mDate.substring(6, 8);
		
		String returnStr;
		
		mCursor = mReadDB.rawQuery("SELECT * FROM db_exerciserecord where date='"+tDate+"'", null);
		if(mCursor.getCount()!=0){
			Log.d("SAP", "RECORD");
			mCursor.moveToLast();
			
			returnStr = mCursor.getString(1).toString();
			
		}
		else{ 
			// No record
			Log.d("SAP", "NO RECORD");
			returnStr = "";
		}
		
		mCursor.close();
		mReadDB.close();
		mSAPDB.close();
		return returnStr;
	}
	
	private void getExerciseRecordfromDB() {
		mSAPDB = new SAPDatabaseManagement(ExerciseRecordActivity.this);
		mReadDB = mSAPDB.getReadableDatabase();
		String tDate;
		tDate = mDate.substring(0, 4)+"-"+mDate.substring(4, 6)+"-"+mDate.substring(6, 8);
		Log.d("SAP", tDate+" read record");
		
		mCursor = mReadDB.rawQuery("SELECT * FROM db_exerciserecord where date='"+tDate+"'", null);
		if(mCursor.getCount()!=0){
			Log.d("SAP", "RECORD");
			mCursor.moveToLast();
			
			String tempExerStr = mCursor.getString(1);
			if(tempExerStr.indexOf("SAPmESsAgE:TeMpDeLeTe") == -1)
				mRecord.setText(tempExerStr);
			else{
				Log.d("SAP", "will remove record detected");
				tempExerStr = tempExerStr.replace("SAPmESsAgE:TeMpDeLeTe\n", "");
				mRecord.setText(tempExerStr);
				
				new AlertDialog.Builder(ExerciseRecordActivity.this).setTitle("한번 더 확인해 주세요")
				.setMessage(mYear+"년 "+mMonth+"월 "+mDay+"일 "+dayOfWeek[mWeek-1]+" 운동 일지가 삭제되었었습니다. 삭제하시겠습니까?")
				.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						setExerciseRecordtoDB("");
						mRecord.setText("");
					}
				})
				.setNegativeButton("재확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
			}
			
			if(mCursor.getBlob(2)==null) {
				Log.d("SAP", "NO THUMBNAIL");
				mThumbnail.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
			}
			else {
				byte[] img_byte = mCursor.getBlob(2);
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;
				mThumbnail.setImageBitmap(BitmapFactory.decodeByteArray(img_byte, 0, img_byte.length, options));
			}
			mImage.setEnabled(true);
		}
		else{ 
			// No record
			Log.d("SAP", "NO RECORD");
			mRecord.setText("");
			mThumbnail.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
			mImage.setEnabled(false);
		}
		
		mCursor.close();
		mReadDB.close();
		mSAPDB.close();
	}
	
	private void setExerciseRecordtoDB(String _record) {
		mSAPDB = new SAPDatabaseManagement(ExerciseRecordActivity.this);
		mReadDB = mSAPDB.getReadableDatabase();
		mWrightDB = mSAPDB.getWritableDatabase();
		String tDate;
		tDate = mDate.substring(0, 4)+"-"+mDate.substring(4, 6)+"-"+mDate.substring(6, 8);
		Log.d("SAP", tDate+" write record");
		
		mCursor = mReadDB.rawQuery("SELECT * FROM db_exerciserecord where date='"+tDate+"'", null);
		Log.d("SAP", mCursor.getCount()+"");
		if(mCursor.getCount()!=0){ // 이미 있는 운동일지를 수정
			Log.d("SAP", "RECORD MODIFY");
			mWrightDB.execSQL("UPDATE db_exerciserecord " +
							"SET record="+"'"+_record+"'" +
							"WHERE date="+"'"+tDate+"';");
//			Toast.makeText(ExerciseRecordActivity.this, "운동 일지 수정 완료", Toast.LENGTH_SHORT).show();
		}
		else{ // 새로운 운동일지를 기록
			Log.d("SAP", "NEW RECORD INSERT");
			mWrightDB.execSQL("INSERT INTO db_exerciserecord " +
					"(date, record) " +
					"VALUES('"+tDate+"', '"+_record+"');");
			Log.d("SAP", "exercise record recording complete");
//			Toast.makeText(ExerciseRecordActivity.this, "운동 일지 저장 완료", Toast.LENGTH_SHORT).show();
		}
		mCursor.close();
		mWrightDB.close();
		mReadDB.close();
		mSAPDB.close();
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if(requestCode == 0){
				String rs = data.getStringExtra("result");
				if(rs.equals("save"))
					Toast.makeText(ExerciseRecordActivity.this, mDate + " 사진이 변경되었습니다.", Toast.LENGTH_SHORT).show();
				else if(rs.equals("delete"))
					Toast.makeText(ExerciseRecordActivity.this, mDate + " 사진이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
				getExerciseRecordfromDB();
			}
			if(requestCode == 1){
				String date = data.getStringExtra("date");
				int year, month, day;
				Log.d("SAP", date);
				
				mDate = date.replaceAll("-", "");
				year = Integer.parseInt(mDate.substring(0, 4));
				month = Integer.parseInt(mDate.substring(4, 6));
				day = Integer.parseInt(mDate.substring(6, 8));
				// get day_of_week
				Calendar cal = Calendar.getInstance();
				cal.set(year, month-1, day);

				mDateView.setText(year+"년 "+month+"월 "+day+"일\n"+dayOfWeek[cal.get(Calendar.DAY_OF_WEEK)-1]);
				
				getExerciseRecordfromDB();
			}
		}
		else if(resultCode == RESULT_CANCELED)
		{
			if(requestCode == 0)
				Toast.makeText(ExerciseRecordActivity.this, "사진 변경이 취소되었습니다.", Toast.LENGTH_SHORT).show();
			if(requestCode == 1)
				Toast.makeText(ExerciseRecordActivity.this, "날짜 변경이 취소되었습니다.", Toast.LENGTH_SHORT).show();
		}
	}
}
