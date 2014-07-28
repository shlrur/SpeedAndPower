package shlrur.sap.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SetupStrengthActivity extends Activity {
	
	private EditText et_squat, et_deadlift, et_calfraise, et_benchpress;
	private EditText et_barbellrow, et_curl, et_militarypress, et_pullup;
	private TextView[] unit; 
	private Button btn_save;
	
	private SAPDatabaseManagement mSAPDB;
	private SQLiteDatabase mReadDB;
	private SQLiteDatabase mWrightDB;
	
	private SharedPreferences mPrefs;
	
	private float mCoefficient;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_strength_layout);
		
		mPrefs = getSharedPreferences("SAPpref", MODE_PRIVATE);
		
		unit = new TextView[8];
		
		if(mPrefs.getString("WeightUnit", "kg").equals("kg"))
			mCoefficient = 1f;
		else
			mCoefficient = 0.453592f;
		
		et_squat		= (EditText)findViewById(R.id.setup_strength_squat);
		et_deadlift		= (EditText)findViewById(R.id.setup_strength_deadlift);
		et_calfraise	= (EditText)findViewById(R.id.setup_strength_calfraise);
		et_benchpress	= (EditText)findViewById(R.id.setup_strength_benchpress);
		et_barbellrow	= (EditText)findViewById(R.id.setup_strength_barbellrow);
		et_curl			= (EditText)findViewById(R.id.setup_strength_curl);
		et_militarypress= (EditText)findViewById(R.id.setup_strength_militarypress);
		et_pullup		= (EditText)findViewById(R.id.setup_strength_pullup);
		btn_save		= (Button)findViewById(R.id.setup_strength_save);
		
		unit[0] = (TextView)findViewById(R.id.setup_strength_unit_1);
		unit[1] = (TextView)findViewById(R.id.setup_strength_unit_2);
		unit[2] = (TextView)findViewById(R.id.setup_strength_unit_3);
		unit[3] = (TextView)findViewById(R.id.setup_strength_unit_4);
		unit[4] = (TextView)findViewById(R.id.setup_strength_unit_5);
		unit[5] = (TextView)findViewById(R.id.setup_strength_unit_6);
		unit[6] = (TextView)findViewById(R.id.setup_strength_unit_7);
		unit[7] = (TextView)findViewById(R.id.setup_strength_unit_8);
		
		for(int i=0 ; i<8 ; i++)
			unit[i].setText(mPrefs.getString("WeightUnit", "kg"));
		
		mSAPDB = new SAPDatabaseManagement(this);
//		mWrightDB = mSAPDB.getWritableDatabase();
		mReadDB = mSAPDB.getReadableDatabase();
		Cursor cursor_strength = mReadDB.rawQuery("SELECT * FROM db_strength", null);
		cursor_strength.moveToLast();
		
		et_squat.setHint(String.format("%.1f", Float.parseFloat(cursor_strength.getString(2))/mCoefficient));
		et_deadlift.setHint(String.format("%.1f", Float.parseFloat(cursor_strength.getString(3))/mCoefficient));
		et_calfraise.setHint(String.format("%.1f", Float.parseFloat(cursor_strength.getString(4))/mCoefficient));
		et_benchpress.setHint(String.format("%.1f", Float.parseFloat(cursor_strength.getString(5))/mCoefficient));
		et_barbellrow.setHint(String.format("%.1f", Float.parseFloat(cursor_strength.getString(6))/mCoefficient));
		et_curl.setHint(String.format("%.1f", Float.parseFloat(cursor_strength.getString(7))/mCoefficient));
		et_militarypress.setHint(String.format("%.1f", Float.parseFloat(cursor_strength.getString(8))/mCoefficient));
		et_pullup.setHint(String.format("%.1f", Float.parseFloat(cursor_strength.getString(9))/mCoefficient));
		
		Log.d("SAP", cursor_strength.getString(2) + " " + cursor_strength.getString(9));
		cursor_strength.close();
		mReadDB.close();
		mSAPDB.close();
		
		btn_save.setOnClickListener(new OnClickListener() {
			float squat, deadlift, calfraise, benchpress;
			float barbellrow, curl, militarypress, pullup;
			
			String date;
			
			@Override
			public void onClick(View v) {
				String diag_message="";
				
				if(!et_squat.getText().toString().equals(""))
					diag_message += "스쿼트1rm: "+et_squat.getText()+mPrefs.getString("WeightUnit", "kg")+"\n";
				if(!et_deadlift.getText().toString().equals(""))
					diag_message += "데드리프트1rm: "+et_deadlift.getText()+mPrefs.getString("WeightUnit", "kg")+"\n";
				if(!et_calfraise.getText().toString().equals(""))
					diag_message += "카프레이즈1rm: "+et_calfraise.getText()+mPrefs.getString("WeightUnit", "kg")+"\n";
				if(!et_benchpress.getText().toString().equals(""))
					diag_message += "벤치프레스1rm: "+et_benchpress.getText()+mPrefs.getString("WeightUnit", "kg")+"\n";
				if(!et_barbellrow.getText().toString().equals(""))
					diag_message += "바벨로우1rm: "+et_barbellrow.getText()+mPrefs.getString("WeightUnit", "kg")+"\n";
				if(!et_curl.getText().toString().equals(""))
					diag_message += "컬1rm: "+et_curl.getText()+mPrefs.getString("WeightUnit", "kg")+"\n";
				if(!et_militarypress.getText().toString().equals(""))
					diag_message += "밀리터리프레스1rm: "+et_militarypress.getText()+mPrefs.getString("WeightUnit", "kg")+"\n";
				if(!et_pullup.getText().toString().equals(""))
					diag_message += "풀업1rm: "+et_pullup.getText()+mPrefs.getString("WeightUnit", "kg");
				
				if(diag_message.equals(""))
					diag_message = "기록의 변경이 없습니다.\n그래도 저장하시겠습니까?";
				
				new AlertDialog.Builder(SetupStrengthActivity.this).setTitle("한번 더 확인해 주세요")
											.setMessage(diag_message)
											.setPositiveButton("저장", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													try{
														mSAPDB = new SAPDatabaseManagement(SetupStrengthActivity.this);
														mWrightDB = mSAPDB.getWritableDatabase();
														mReadDB = mSAPDB.getReadableDatabase();
														Cursor cursor_strength = mReadDB.rawQuery("SELECT * FROM db_strength", null);
														cursor_strength.moveToLast();
														
														if(et_squat.getText().toString().equals(""))
															squat 			= Float.parseFloat(cursor_strength.getString(2));
														else
															squat 			= mCoefficient*Float.valueOf(et_squat.getText().toString());
														
														if(et_deadlift.getText().toString().equals(""))
															deadlift		= Float.parseFloat(cursor_strength.getString(3));
														else
															deadlift		= mCoefficient*Float.valueOf(et_deadlift.getText().toString());
														
														if(et_calfraise.getText().toString().equals(""))
															calfraise		= Float.parseFloat(cursor_strength.getString(4));
														else
															calfraise		= mCoefficient*Float.valueOf(et_calfraise.getText().toString());
														
														if(et_benchpress.getText().toString().equals(""))
															benchpress		= Float.parseFloat(cursor_strength.getString(5));
														else
															benchpress		= mCoefficient*Float.valueOf(et_benchpress.getText().toString());
														
														if(et_barbellrow.getText().toString().equals(""))
															barbellrow		= Float.parseFloat(cursor_strength.getString(6));
														else
															barbellrow		= mCoefficient*Float.valueOf(et_barbellrow.getText().toString());
														
														if(et_curl.getText().toString().equals(""))
															curl 			= Float.parseFloat(cursor_strength.getString(7));
														else
															curl			= mCoefficient*Float.valueOf(et_curl.getText().toString());
														
														if(et_militarypress.getText().toString().equals(""))
															militarypress	= Float.parseFloat(cursor_strength.getString(8));
														else
															militarypress	= mCoefficient*Float.valueOf(et_militarypress.getText().toString());
														
														if(et_pullup.getText().toString().equals(""))
															pullup 			= Float.parseFloat(cursor_strength.getString(9));
														else
															pullup			= mCoefficient*Float.valueOf(et_pullup.getText().toString());
														
														SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
														Date curDate = new Date();
														date = sdf.format(curDate);
																											
														mWrightDB.execSQL("INSERT INTO db_strength " +
																"(date, squat, deadlift, calfraise, benchpress, barbellrow, curl, militarypress, pullup) " +
																"VALUES('"+date+"', " +
																squat+", " +
																deadlift+", " +
																calfraise+", " +
																benchpress+", " +
																barbellrow+", " +
																curl+", " +
																militarypress+", " +
																pullup+");");
														Log.d("SAP", "strength record complete");
														
														cursor_strength.close();
														mReadDB.close();
														mWrightDB.close();
														mSAPDB.close();
														
														finish();
													}catch(NumberFormatException e){
														Log.d("SAP", "ERROR: " + e.toString());
														new AlertDialog.Builder(SetupStrengthActivity.this)
									                	.setTitle("ERROR")
									                	.setMessage("모든 값을 정확히 입력해주십시오")
									                	.setPositiveButton("확인", new DialogInterface.OnClickListener() {
															@Override
															public void onClick(DialogInterface dialog, int which) {
																dialog.dismiss();
															}
														})
														.show();
													}
												}
											})
											.setNegativeButton("재입력", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													
												}
											}).show();
			}
		});	
	}
}
