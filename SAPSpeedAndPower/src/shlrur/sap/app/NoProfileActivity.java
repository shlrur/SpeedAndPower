package shlrur.sap.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class NoProfileActivity extends Activity {
		
	private SAPDatabaseManagement mSAPDB;	
	
	private SQLiteDatabase mWrightDB;
	
	private EditText et_height, et_weight, et_muscle, et_fat;
	private EditText et_squat, et_deadlift, et_calfraise, et_benchpress;
	private EditText et_barbellrow, et_curl, et_militarypress, et_pullup;
	private Button btn_save, btn_import;
	private TextView[] tv_unit;
	
	private ToggleButton tgl_kg, tgl_lbs;
	
	private SharedPreferences mPrefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.no_profile_layout);
		
		mPrefs = getSharedPreferences("SAPpref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = mPrefs.edit();
		
		tv_unit = new TextView[8];
		
		et_height		= (EditText)findViewById(R.id.noprofile_height);
		et_weight		= (EditText)findViewById(R.id.noprofile_weight);
		et_muscle		= (EditText)findViewById(R.id.noprofile_muscle);
		et_fat			= (EditText)findViewById(R.id.noprofile_fat);
		et_squat		= (EditText)findViewById(R.id.noprofile_squat);
		et_deadlift		= (EditText)findViewById(R.id.noprofile_deadlift);
		et_calfraise	= (EditText)findViewById(R.id.noprofile_calfraise);
		et_benchpress	= (EditText)findViewById(R.id.noprofile_benchpress);
		et_barbellrow	= (EditText)findViewById(R.id.noprofile_barbellrow);
		et_curl			= (EditText)findViewById(R.id.noprofile_curl);
		et_militarypress= (EditText)findViewById(R.id.noprofile_militarypress);
		et_pullup		= (EditText)findViewById(R.id.noprofile_pullup);
		btn_save		= (Button)findViewById(R.id.noprofile_save);
		btn_import		= (Button)findViewById(R.id.noprofile_import);
		tv_unit[0]		= (TextView)findViewById(R.id.noprofile_unit_0);
		tv_unit[1]		= (TextView)findViewById(R.id.noprofile_unit_1);
		tv_unit[2]		= (TextView)findViewById(R.id.noprofile_unit_2);
		tv_unit[3]		= (TextView)findViewById(R.id.noprofile_unit_3);
		tv_unit[4]		= (TextView)findViewById(R.id.noprofile_unit_4);
		tv_unit[5]		= (TextView)findViewById(R.id.noprofile_unit_5);
		tv_unit[6]		= (TextView)findViewById(R.id.noprofile_unit_6);
		tv_unit[7]		= (TextView)findViewById(R.id.noprofile_unit_7);
		
		tgl_kg 			= (ToggleButton)findViewById(R.id.noprofile_tgl_kg);
		tgl_lbs			= (ToggleButton)findViewById(R.id.noprofile_tgl_lbs);
		
		tgl_kg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					tgl_lbs.setChecked(false);
					editor.putString("WeightUnit", "kg");
					editor.commit();
					for(int i=0 ; i<tv_unit.length ; i++)
						tv_unit[i].setText("kg");
				}
				else{
					tgl_lbs.setChecked(true);
					editor.putString("WeightUnit", "lbs");
					editor.commit();
					for(int i=0 ; i<tv_unit.length ; i++)
						tv_unit[i].setText("lbs");
				}
			}
		});
		
		tgl_lbs.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					tgl_kg.setChecked(false);
					editor.putString("WeightUnit", "lbs");
					editor.commit();
					for(int i=0 ; i<tv_unit.length ; i++)
						tv_unit[i].setText("lbs");
				}
				else{
					tgl_kg.setChecked(true);
					editor.putString("WeightUnit", "kg");
					editor.commit();
					for(int i=0 ; i<tv_unit.length ; i++)
						tv_unit[i].setText("kg");
				}
					
			}
		});
		
		btn_import.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					File sd = Environment.getExternalStorageDirectory();
					File data = Environment.getDataDirectory();
					
					if(sd.canWrite()){
						String currentDBPath = "/data/shlrur.sap.app/databases/SAP.db";
						String importDBPath = "/SAP_Application/SAP_backup/SAP.db";
						File currentDB = new File(data, currentDBPath);
			            File importDB = new File(sd, importDBPath);
			            Log.d("SAP", "db import start");

						File path = new File(data, "/data/shlrur.sap.app/databases");
						if (!path.isDirectory()) {
							path.mkdirs();
							Log.d("SAP", "import directory make");
						}

			            if (currentDB.exists()) {
			                FileChannel src = null;
			                FileChannel dst = null;
			                
			                try{
			                	src = new FileInputStream(importDB).getChannel();
				                dst = new FileOutputStream(currentDB).getChannel();
				                
				                dst.transferFrom(src, 0, src.size());
			                } finally{
			                	src.close();
				                dst.close();
			                }
			                Log.d("SAP", "db import complete");
			                
			                new AlertDialog.Builder(NoProfileActivity.this)
			                	.setTitle("Complete")
			                	.setMessage("백업 파일을 성공적으로 가져 왔습니다.")
			                	.setPositiveButton("확인", new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										finish();
									}
								})
								.show();
			                //finish();
			            }
					}
				}catch(Exception e){
					Log.d("SAP", "db backup fail");
					File sd = Environment.getExternalStorageDirectory();
					File importDB = new File(sd, "/SAP_Application/SAP_backup");
					Dialog dialog = new Dialog(NoProfileActivity.this, R.style.Dialog);
					TextView tv = new TextView(NoProfileActivity.this);
					tv.setText("백업 파일이 존재하지 않습니다.\n" +
            				importDB+"\n폴더에\nSAP.db\n파일이 존재하지 않습니다." +
            				"\nSAP.db\n파일을\n"+importDB+"\n폴더로 옮겨주십시오.");
	                tv.setTextSize(20);
	                dialog.setContentView(tv);
	                dialog.show();
				}
				
			}
		});
		
		btn_save.setOnClickListener(new OnClickListener() {
			float height, weight, muscle, fat;
			float squat, deadlift, calfraise, benchpress;
			float barbellrow, curl, militarypress, pullup;
			String date;
			float coefficient;
			
			@Override
			public void onClick(View v) {
				if(mPrefs.getString("WeightUnit", "kg").equals("kg"))
					coefficient = 1f;
				else if(mPrefs.getString("WeightUnit", "kg").equals("lbs"))
					coefficient = 0.453592f;
				
				new AlertDialog.Builder(NoProfileActivity.this).setTitle("한번 더 확인해 주세요")
											.setMessage("키: "+et_height.getText()+" cm\n" +
														"뭄무게: "+et_weight.getText()+" kg\n" +
														"근육량: "+et_muscle.getText()+" kg\n" +
														"지방량: "+et_fat.getText()+" kg\n\n" +
														"스쿼트1rm: "+et_squat.getText()+mPrefs.getString("WeightUnit", "kg")+"\n" +
														"데드리프트1rm: "+et_deadlift.getText()+mPrefs.getString("WeightUnit", "kg")+"\n" +
														"카프레이즈1rm: "+et_calfraise.getText()+mPrefs.getString("WeightUnit", "kg")+"\n" +
														"벤치프레스1rm: "+et_benchpress.getText()+mPrefs.getString("WeightUnit", "kg")+"\n" +
														"바벨로우1rm: "+et_barbellrow.getText()+mPrefs.getString("WeightUnit", "kg")+"\n" +
														"컬1rm: "+et_curl.getText()+mPrefs.getString("WeightUnit", "kg")+"\n" +
														"밀리터리프레스1rm: "+et_militarypress.getText()+mPrefs.getString("WeightUnit", "kg")+"\n" +
														"풀업1rm: "+et_pullup.getText()+mPrefs.getString("WeightUnit", "kg"))
											.setPositiveButton("저장", new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
													try{
														height 			= Float.valueOf(et_height.getText().toString());
														weight 			= Float.valueOf(et_weight.getText().toString());
														muscle 			= Float.valueOf(et_muscle.getText().toString());
														fat 			= Float.valueOf(et_fat.getText().toString());
														squat 			= Float.valueOf(et_squat.getText().toString()) * coefficient;
														deadlift		= Float.valueOf(et_deadlift.getText().toString()) * coefficient;
														calfraise		= Float.valueOf(et_calfraise.getText().toString()) * coefficient;
														benchpress		= Float.valueOf(et_benchpress.getText().toString()) * coefficient;
														barbellrow		= Float.valueOf(et_barbellrow.getText().toString()) * coefficient;
														curl			= Float.valueOf(et_curl.getText().toString()) * coefficient;
														militarypress	= Float.valueOf(et_militarypress.getText().toString()) * coefficient;
														pullup			= Float.valueOf(et_pullup.getText().toString()) * coefficient;
														
														SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
														Date curDate = new Date();
														date = sdf.format(curDate);
														
														mSAPDB = new SAPDatabaseManagement(NoProfileActivity.this);
														mWrightDB = mSAPDB.getWritableDatabase();
														
														mWrightDB.execSQL("INSERT INTO db_profile " +
																					"(date, height, weight, muscle, fat) " +
																					"VALUES('"+date+"', " +
																					height+", " +
																					weight+", " +
																					muscle+", " +
																					fat+");");
														Log.d("SAP", "profile record complete");
														
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
														
														mWrightDB.close();
														mSAPDB.close();
														
														finish();
													}catch(NumberFormatException e){
														Log.d("SAP", "ERROR: " + e.toString());
														new AlertDialog.Builder(NoProfileActivity.this)
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
		
		// 각 설정 값들의 default 값 미리 입력
		et_height.setText("167");
		et_weight.setText("65");
		et_muscle.setText("20");
		et_fat.setText("13");
		et_squat.setText("1");
		et_deadlift.setText("1");
		et_calfraise.setText("1");
		et_benchpress.setText("1");
		et_barbellrow.setText("1");
		et_curl.setText("1");
		et_militarypress.setText("1");
		et_pullup.setText("1");
		tgl_kg.setChecked(true);
		
		Toast.makeText(this, "임의의 설정값이 입력되어 있습니다. 알고있는 수치만 재입력 하시고 추후에 변경 가능합니다.", Toast.LENGTH_LONG).show();
	}
	
	@Override
	public void onBackPressed(){
		Toast.makeText(this, "개인 정보를 저장해 주십시오", Toast.LENGTH_SHORT).show();
	}
}
