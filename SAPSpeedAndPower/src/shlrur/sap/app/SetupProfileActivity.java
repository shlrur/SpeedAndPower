package shlrur.sap.app;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SetupProfileActivity extends Activity {
	
	private EditText et_height, et_weight, et_muscle, et_fat; 
	private Button btn_save;
	
	private SAPDatabaseManagement mSAPDB;
	private SQLiteDatabase mReadDB;
	private SQLiteDatabase mWrightDB;
	
	private Cursor cursor_profile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_profile_layout);
		
		mSAPDB = new SAPDatabaseManagement(this);
//		mWrightDB = mSAPDB.getWritableDatabase();
		mReadDB = mSAPDB.getReadableDatabase();
		
		cursor_profile 	= mReadDB.rawQuery("SELECT * FROM db_profile", null);
		cursor_profile.moveToLast();
		
		et_height= (EditText)findViewById(R.id.setup_profile_height);
		et_weight= (EditText)findViewById(R.id.setup_profile_weight);
		et_muscle= (EditText)findViewById(R.id.setup_profile_muscle);
		et_fat	 = (EditText)findViewById(R.id.setup_profile_fat);
				
		btn_save = (Button)findViewById(R.id.setup_profile_save);

		et_height.setHint(cursor_profile.getString(2));
		et_weight.setHint(cursor_profile.getString(3));
		et_muscle.setHint(cursor_profile.getString(4));
		et_fat.setHint(cursor_profile.getString(5));
		
		Log.d("SAP", cursor_profile.getString(2) + " " + cursor_profile.getString(5));
		cursor_profile.close();
		mReadDB.close();
		mSAPDB.close();
		
		btn_save.setOnClickListener(new OnClickListener() {
			float height, weight, muscle, fat;
			String date;
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(SetupProfileActivity.this).setTitle("한번 더 확인해 주세요")
				.setMessage("키:"+et_height.getText()+"kg\n" +
							"뭄무게:"+et_weight.getText()+"kg\n" +
							"근육량:"+et_muscle.getText()+"kg\n" +
							"지방량:"+et_fat.getText()+"kg")
				.setPositiveButton("저장", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try{
							height 	= Float.valueOf(et_height.getText().toString());
							weight 	= Float.valueOf(et_weight.getText().toString());
							muscle 	= Float.valueOf(et_muscle.getText().toString());
							fat 	= Float.valueOf(et_fat.getText().toString());
							
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
							Date curDate = new Date();
							date = sdf.format(curDate);
							
							mSAPDB = new SAPDatabaseManagement(SetupProfileActivity.this);
							mWrightDB = mSAPDB.getWritableDatabase();
							
							mWrightDB.execSQL("INSERT INTO db_profile " +
														"(date, height, weight, muscle, fat) " +
														"VALUES('"+date+"', " +
														height+", " +
														weight+", " +
														muscle+", " +
														fat+");");
							Log.d("SAP", "profile record complete");
							mWrightDB.close();
							mSAPDB.close();
													
							finish();
						}catch(NumberFormatException e){
							Log.d("SAP", "ERROR: " + e.toString());
							new AlertDialog.Builder(SetupProfileActivity.this)
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
