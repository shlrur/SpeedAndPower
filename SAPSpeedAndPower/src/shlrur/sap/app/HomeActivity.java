package shlrur.sap.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class HomeActivity extends Activity {

	private SAPApplication mMyApplication;
	
	private SAPDatabaseManagement mSAPDB;
	private SQLiteDatabase mReadDB;
//	private SQLiteDatabase mWrightDB;
	
	private Cursor mCursor;
	
	private Button btn_wod, btn_setup, btn_backup, btn_exercise_record, btn_tabata;
	private Button btn_help, btn_suggestion, btn_rmcalculation, btn_export;
	
	private TextView mTv_CustomText;
	
	private Dialog mDialog, mDialog_nest;
	
	private SharedPreferences mPrefs;
	
	private ProgressDialog mProgDiag;
	
	private final float[] RMCoefficient = {0, 
			1.000f, 1.047f, 1.091f, 1.130f, 1.167f, 
			1.202f, 1.236f, 1.269f, 1.300f, 1.330f,
			1.359f, 1.387f, 1.416f, 1.445f, 1.475f,
			1.504f, 1.531f, 1.560f, 1.587f, 1.616f};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_layout);
		
		mMyApplication = (SAPApplication) getApplication();
		
		/*
		 * 로고 중복 표시 방지 코드
		 */
		if (mMyApplication.isLogo) {
			Log.d("SAP", "LOGO");
			startActivityForResult(new Intent(this, LogoActivity.class), 1);
			mMyApplication.isLogo = false;
		}
		else Log.d("SAP", "no LOGO");
		
		mProgDiag = new ProgressDialog(HomeActivity.this);
		mProgDiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgDiag.setTitle("Processing...");
		mProgDiag.setMessage("작업 중입니다");
		
		btn_wod 			= (Button)findViewById(R.id.home_btn_wod);
		btn_setup 			= (Button)findViewById(R.id.home_btn_setup);
		btn_tabata			= (Button)findViewById(R.id.home_btn_tabata);
		btn_backup 			= (Button)findViewById(R.id.home_btn_backup);
		btn_exercise_record	= (Button)findViewById(R.id.home_btn_exerciserecord);
		btn_help 			= (Button)findViewById(R.id.home_btn_help);
		btn_suggestion 		= (Button)findViewById(R.id.home_btn_suggestion);
		btn_rmcalculation 	= (Button)findViewById(R.id.home_btn_rmcalculator);
		btn_export			= (Button)findViewById(R.id.home_btn_export);
				
		mTv_CustomText = (TextView)findViewById(R.id.home_custom_text);
		
		mPrefs = getSharedPreferences("SAPpref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = mPrefs.edit();
		
		mPrefs = getSharedPreferences("SAPpref", MODE_PRIVATE);
		mTv_CustomText.setText(mPrefs.getString("HomeText", "길게 눌러보세요."));
		mTv_CustomText.setTextSize(Integer.parseInt(mPrefs.getString("HomeTextSize", "15")));
		
		mTv_CustomText.setOnLongClickListener(new OnLongClickListener() {
			String text, textSize;
			Button save, cancle;
			EditText et_text, et_size;
			@Override
			public boolean onLongClick(View v) {
				Log.d("SAP", "home text long click");
				mDialog = new Dialog( HomeActivity.this, R.style.Dialog );
				mDialog.setContentView(R.layout.home_text_diag);
				save	= (Button)mDialog.findViewById(R.id.home_text_save);
				cancle	= (Button)mDialog.findViewById(R.id.home_text_cancle);
				et_text	= (EditText)mDialog.findViewById(R.id.home_text_text);
				et_size	= (EditText)mDialog.findViewById(R.id.home_text_size);
				save.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						text = et_text.getText().toString();
						textSize = et_size.getText().toString();
						if(!text.equals(""))
							editor.putString("HomeText", text);
						if(!textSize.equals(""))
							editor.putString("HomeTextSize", textSize);
						if(text.equals("")&&textSize.equals(""))
							Toast.makeText(HomeActivity.this, "취소 되셨습니다.", Toast.LENGTH_SHORT).show();
						editor.commit();
						mTv_CustomText.setText(mPrefs.getString("HomeText", "길게 눌러보세요."));
						mTv_CustomText.setTextSize(Integer.parseInt(mPrefs.getString("HomeTextSize", "15")));
						Log.d("SAP", "Home Text save");
						mDialog.dismiss();
					}
				});
				cancle.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.d("SAP", "Home Text cancle");
						Toast.makeText(HomeActivity.this, "취소 되셨습니다.", Toast.LENGTH_SHORT).show();
						mDialog.dismiss();
					}
				});
				mDialog.show();
				return false;
			}
		});
		btn_wod.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, WorkoutOftheDayActivity.class);
				startActivity(intent);
			}
		});
		btn_setup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, SetupActivity.class);
				startActivity(intent);
			}
		});
		btn_tabata.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, TabataTimerActivity.class);
				startActivity(intent);
			}
		});
		btn_exercise_record.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, ExerciseRecordActivity.class);
				startActivity(intent);
			}
		});
		btn_help.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeActivity.this, HelpActivity.class);
				startActivity(intent);
			}
		});
		btn_suggestion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Uri uri = Uri.parse("mailto:shlrur123@gmail.com");
				Intent it = new Intent(Intent.ACTION_SENDTO, uri);
				
				SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy.MM.dd HH:mm:ss", Locale.KOREA );
		        Date currentTime = new Date ( );
		        String today = formatter.format ( currentTime );
				it.putExtra(Intent.EXTRA_SUBJECT, "[SAP app] "+today);
				startActivity(it);
			}
		});
		btn_backup.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgDiag.show();
				try{
					File sd = Environment.getExternalStorageDirectory();
					File data = Environment.getDataDirectory();
					
					if(sd.canWrite()){
						String currentDBPath = "/data/shlrur.sap.app/databases/SAP.db";
						String backupDBPath = "/SAP_Application/SAP_backup/SAP.db";
						File currentDB = new File(data, currentDBPath);
			            File backupDB = new File(sd, backupDBPath);
			            Log.d("SAP", "db backup start");

						File path = new File(sd, "/SAP_Application/SAP_backup");
						if (!path.isDirectory()) {
							path.mkdirs();
							Log.d("SAP", "backup directory make");
						}

			            if (currentDB.exists()) {
			                FileChannel src = null;
			                FileChannel dst = null;
			                
			                try{
			                	src = new FileInputStream(currentDB).getChannel();
				                dst = new FileOutputStream(backupDB).getChannel();
				                
				                dst.transferFrom(src, 0, src.size());
			                } finally{
			                	src.close();
				                dst.close();
			                }
			                Log.d("SAP", "db backup complete: "+path.toString());
//			                Toast.makeText(HomeActivity.this, path.toString()+" 의 위치에 백업 되었습니다.", Toast.LENGTH_SHORT).show();
			                mDialog = new Dialog( HomeActivity.this, R.style.Dialog );
			                TextView tv = new TextView(HomeActivity.this);
			                tv.setPadding(30, 30, 30, 30);
//			                tv.setGravity(Gravity.);
			                tv.setText(path.toString()+"\n의 위치에\nSAP.db\n파일이 백업 되었습니다.\n" +
			                		"이후, 앱을 다시 설치 하셨을 때\n"+ path.toString() + 
			                		"\n의 위치에\nSAP.db\n파일이 있어야 복원이 가능합니다.");
			                tv.setTextSize(20);
			                mDialog.setContentView(tv);
			                mDialog.show();
			            }
					}
					mProgDiag.dismiss();
				}catch(Exception e){
					mProgDiag.dismiss();
					Log.d("SAP", "db backup fail");
					Toast.makeText(HomeActivity.this, "backup 실패 ㅠㅠ\n"+e.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		btn_export.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				try{
					mProgDiag.show();
					File sd = Environment.getExternalStorageDirectory();
					if(sd.canWrite()){
						mSAPDB = new SAPDatabaseManagement(HomeActivity.this);
						mReadDB = mSAPDB.getReadableDatabase();
						
						SimpleDateFormat formatter = new SimpleDateFormat ( "yyyy_MM_dd_HH_mm_ss", Locale.KOREA );
				        Date currentTime = new Date ( );
				        String today = formatter.format ( currentTime );
				        
				        // Profile record
				        // One-RM record
				        // Exercise record text
						// Exercise record image
				        
				        String mProfileRecord="";
				        String mOneRMRecord="";
				        String mExerciseRecord="";
				        
				        String mProfileRecordPath 		= "/SAP_Application/SAP_Export/"+today+"/Profile_Record.txt";
				        String mOneRMRecordPath 		= "/SAP_Application/SAP_Export/"+today+"/One_RM_Record.txt";
				        String mExerciseRecordTextPath 	= "/SAP_Application/SAP_Export/"+today+"/Exercise_Record.txt";
				        String mExerciseRecordImagePath = "/SAP_Application/SAP_Export/"+today+"/Exercise_Record_Image";
				        
				        File mProfileRecordText = new File(sd, mProfileRecordPath);
				        File mOneRMRecordText = new File(sd, mOneRMRecordPath);
				        File mExerciseRecordText = new File(sd, mExerciseRecordTextPath);
				        File mExerciseRecordImage;
				        
//				        String ExportPath = "/SAP_Application/SAP_Export/"+today+"/Exercise_Report.txt";
//						File backupDB = new File(sd, ExportPath);
						
						// make save directory
						File path_txt = new File(sd, "/SAP_Application/SAP_Export/"+today);
						File path_img = new File(sd, "/SAP_Application/SAP_Export/"+today+"/Exercise_Record_Image");
						if (!path_txt.isDirectory()) {
							path_txt.mkdirs();
							Log.d("SAP", "export txt directory make");
						}
						else
							Log.d("SAP", "into the export txt directory");
						if (!path_img.isDirectory()) {
							path_img.mkdirs();
							Log.d("SAP", "export img directory make");
						}
						else
							Log.d("SAP", "into the export img directory");
						
						FileOutputStream mProfileRecordFos = new FileOutputStream(mProfileRecordText);
						FileOutputStream mOneRMRecordFos = new FileOutputStream(mOneRMRecordText);
						FileOutputStream mExerciseRecordFos = new FileOutputStream(mExerciseRecordText);
						FileOutputStream mExerciseRecordImageFos;
						
						/*
						 * Profile Record Export
						 */
						mCursor = mReadDB.rawQuery("SELECT * FROM db_profile", null);
						if(mCursor.getCount() == 0){ 
							Log.d("SAP test", "no profile data");
						}
						mCursor.moveToFirst();
						for(int i=0 ; i<mCursor.getCount() ; i++){
							float fatRate;
							fatRate = Float.parseFloat(mCursor.getString(5))/Float.parseFloat(mCursor.getString(3))*100;
							mProfileRecord += "날짜: " + mCursor.getString(1) + "\r\n";
							mProfileRecord += "- 키: " + mCursor.getString(2) + " cm\r\n";
							mProfileRecord += "- 몸무게: " + mCursor.getString(3) + " kg\r\n";
							mProfileRecord += "- 근육량: " + mCursor.getString(4) + " kg\r\n";
							mProfileRecord += "- 지방량: " + mCursor.getString(5) + " kg("+ String.format("%.1f", fatRate) +")\r\n\r\n\r\n";
							mCursor.moveToNext();
						}
						mProfileRecordFos.write(mProfileRecord.getBytes());
						mProfileRecordFos.close();
						mCursor.close();
						
						/*
						 * One-RM Record Export
						 */
						// Coefficient
						float mCoefficient;
						SharedPreferences mPrefs;
						mPrefs = getSharedPreferences("SAPpref", MODE_PRIVATE);
						String unit = mPrefs.getString("WeightUnit", "kg"); 
						
						if(unit.equals("kg"))
							mCoefficient = 1f;
						else
							mCoefficient = 2.204623f;
						
						mCursor = mReadDB.rawQuery("SELECT * FROM db_strength", null);
						if(mCursor.getCount() == 0){
							Log.d("SAP test", "no strength data");
						}
						mCursor.moveToFirst();
						for(int i=0 ; i<mCursor.getCount() ; i++){
							
							mOneRMRecord += "날짜: " + mCursor.getString(1) + "\r\n";
							mOneRMRecord += "- 스쿼트: " + String.format("%.1f", Float.parseFloat(mCursor.getString(2))*mCoefficient) + " " +unit +"\r\n";
							mOneRMRecord += "- 데드리프트: " + String.format("%.1f", Float.parseFloat(mCursor.getString(3))*mCoefficient) + " " +unit +"\r\n";
							mOneRMRecord += "- 카프레이즈: " + String.format("%.1f", Float.parseFloat(mCursor.getString(4))*mCoefficient) + " " +unit +"\r\n";
							mOneRMRecord += "- 벤치프레스: " + String.format("%.1f", Float.parseFloat(mCursor.getString(4))*mCoefficient) + " " +unit +"\r\n";
							mOneRMRecord += "- 바벨로우: " + String.format("%.1f", Float.parseFloat(mCursor.getString(4))*mCoefficient) + " " +unit +"\r\n";
							mOneRMRecord += "- 컬: " + String.format("%.1f", Float.parseFloat(mCursor.getString(4))*mCoefficient) + " " +unit +"\r\n";
							mOneRMRecord += "- 밀리터리프레스: " + String.format("%.1f", Float.parseFloat(mCursor.getString(4))*mCoefficient) + " " +unit +"\r\n";
							mOneRMRecord += "- 풀업: " + String.format("%.1f", Float.parseFloat(mCursor.getString(4))*mCoefficient) + " " +unit +"\r\n\r\n\r\n";
							
							mCursor.moveToNext();
						}
						mOneRMRecordFos.write(mOneRMRecord.getBytes());
						mOneRMRecordFos.close();
						mCursor.close();
						
						/*
						 * Exercise text, image Record
						 */
						mCursor = mReadDB.rawQuery("SELECT * FROM db_exerciserecord", null);
						if(mCursor.getCount() == 0){ 
							Log.d("SAP test", "no exercise record data");
						}
						mCursor.moveToFirst();
						for(int i=0 ; i<mCursor.getCount() ; i++){
							// text
							mExerciseRecord += "날짜: " + mCursor.getString(0) + "\r\n";
							mExerciseRecord += "운동 일지\r\n--" + mCursor.getString(1).replace("\n", "\r\n") + "\r\n\r\n\r\n";
							
							// image
							if(mCursor.getBlob(2)!=null) {
								mExerciseRecordImage = new File(sd, mExerciseRecordImagePath+"/"+mCursor.getString(0)+".jpg");
								mExerciseRecordImageFos = new FileOutputStream(mExerciseRecordImage);
								mExerciseRecordImageFos.write(mCursor.getBlob(2));
								mExerciseRecordImageFos.close();
							}
							mCursor.moveToNext();
						}
						mExerciseRecordFos.write(mExerciseRecord.getBytes());
						mExerciseRecordFos.close();
						mCursor.close();
						
						mReadDB.close();
						mSAPDB.close();
						
						mDialog = new Dialog( HomeActivity.this, R.style.Dialog );
		                TextView tv = new TextView(HomeActivity.this);
		                tv.setPadding(30, 30, 30, 30);
		                tv.setText(path_txt.toString()+"\n의 위치에 운동 일지가 내보내 졌습니다.");
		                tv.setTextSize(20);
		                mDialog.setContentView(tv);
		                mDialog.show();
					}
					mProgDiag.dismiss();
				}catch(Exception e){
					mProgDiag.dismiss();
					Log.d("SAP", "Export fail: "+e.toString());
					Toast.makeText(HomeActivity.this, "export 실패 ㅠㅠ\n"+e.toString(), Toast.LENGTH_SHORT).show();
				}
			}
		});
		btn_rmcalculation.setOnClickListener(new OnClickListener() {
			Button cal1, cal2, cal3, cal4;
			EditText weight, counts, onerm, kg, lbs;
			Button result;
			
			@Override
			public void onClick(View v) {
				mDialog = new Dialog( HomeActivity.this, R.style.Dialog );
				mDialog.setContentView(R.layout.repetition_maximum_calculator_diag);
				mDialog.setTitle("RM Calculator");
				cal1 = (Button)mDialog.findViewById(R.id.rm_calculator_diag_select_1);
				cal2 = (Button)mDialog.findViewById(R.id.rm_calculator_diag_select_2);
				cal3 = (Button)mDialog.findViewById(R.id.rm_calculator_diag_select_3);
				cal4 = (Button)mDialog.findViewById(R.id.rm_calculator_diag_select_4);
				cal1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mDialog.dismiss();
						mDialog_nest = new Dialog(HomeActivity.this, R.style.Dialog);
						mDialog_nest.setContentView(R.layout.repetition_maximum_calculator_nest_1_diag);
						weight = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_1_weight);
						counts = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_1_counts);
						onerm  = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_1_one_rm);
						result = (Button)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_1_result);
						result.setOnClickListener(new OnClickListener() {
							float v_weight;
							int v_counts;
							float v_onerm = 0f;
							@Override
							public void onClick(View v) {
								try{
									v_weight = Float.parseFloat(weight.getText().toString());
									v_counts = Integer.parseInt(counts.getText().toString());
								} catch(NumberFormatException e){
									Toast.makeText(HomeActivity.this, "올바른 값을 입력하십시오", Toast.LENGTH_SHORT).show();
									onerm.setText("");
								}
								if(v_counts<1 || v_counts>20){
									Toast.makeText(HomeActivity.this, "1~20의 횟수를 입력하십시오", Toast.LENGTH_SHORT).show();
									onerm.setText("");
								}
								else if(v_weight == 0){
									Toast.makeText(HomeActivity.this, "0 이상의 값을 입력하십시오", Toast.LENGTH_SHORT).show();
									onerm.setText("");
								}
								else{
									v_onerm = v_weight*RMCoefficient[v_counts];
									onerm.setText(String.format("%.1f", v_onerm));
									Toast.makeText(HomeActivity.this, "계산 완료", Toast.LENGTH_SHORT).show();
								}
							}
						});
						mDialog_nest.show();
					}
				});
				cal2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mDialog.dismiss();
						mDialog_nest = new Dialog(HomeActivity.this, R.style.Dialog);
						mDialog_nest.setContentView(R.layout.repetition_maximum_calculator_nest_2_diag);
						weight = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_2_weight);
						counts = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_2_counts);
						onerm  = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_2_one_rm);
						result = (Button)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_2_result);
						result.setOnClickListener(new OnClickListener() {
							float v_weight = 0f;
							int v_counts;
							float v_onerm;
							@Override
							public void onClick(View v) {
								try{
									v_counts = Integer.parseInt(counts.getText().toString());
									v_onerm= Float.parseFloat(onerm.getText().toString());
								} catch(NumberFormatException e){
									Toast.makeText(HomeActivity.this, "올바른 값을 입력하십시오", Toast.LENGTH_SHORT).show();
									weight.setText("");
								}
								if(v_counts<1 || v_counts>20){
									Toast.makeText(HomeActivity.this, "1~20의 횟수를 입력하십시오", Toast.LENGTH_SHORT).show();
									weight.setText("");
								}
								else if(v_onerm == 0){
									Toast.makeText(HomeActivity.this, "0 이상의 값을 입력하십시오", Toast.LENGTH_SHORT).show();
									weight.setText("");
								}
								else{
									v_weight = v_onerm/RMCoefficient[v_counts];
									weight.setText(String.format("%.1f", v_weight));
									Toast.makeText(HomeActivity.this, "계산 완료", Toast.LENGTH_SHORT).show();
								}
							}
						});
						mDialog_nest.show();
					}
				});
				cal3.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mDialog.dismiss();
						mDialog_nest = new Dialog(HomeActivity.this, R.style.Dialog);
						mDialog_nest.setContentView(R.layout.repetition_maximum_calculator_nest_3_diag);
						weight = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_3_weight);
						counts = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_3_counts);
						onerm  = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_3_one_rm);
						result = (Button)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_3_result);
						result.setOnClickListener(new OnClickListener() {
							float v_weight;
							int v_counts;
							float v_onerm;
							@Override
							public void onClick(View v) {
								try{
									v_weight = Float.parseFloat(weight.getText().toString());
									v_onerm= Float.parseFloat(onerm.getText().toString());
								} catch(NumberFormatException e){
									Toast.makeText(HomeActivity.this, "올바른 값을 입력하십시오", Toast.LENGTH_SHORT).show();
									counts.setText("");
								}
								if(v_onerm == 0 || v_weight == 0){
									counts.setText("");
									Toast.makeText(HomeActivity.this, "0 이상의 값을 입력하십시오", Toast.LENGTH_SHORT).show();
								}
								else if(v_onerm < v_weight){
									counts.setText("");
									Toast.makeText(HomeActivity.this, "1-RM 값이 더 커야합니다", Toast.LENGTH_SHORT).show();
								}
								else{
									v_counts = MostClosetCounts(v_onerm/v_weight);
									if(v_counts==0)
										counts.setText("20+");
									else
										counts.setText(v_counts+"");
									Toast.makeText(HomeActivity.this, "계산 완료", Toast.LENGTH_SHORT).show();
								}
							}
						});
						mDialog_nest.show();
					}
				});
				cal4.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						mDialog.dismiss();
						mDialog_nest = new Dialog(HomeActivity.this, R.style.Dialog);
						mDialog_nest.setContentView(R.layout.repetition_maximum_calculator_nest_4_diag);
						kg = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_4_kg);
						lbs = (EditText)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_4_lbs);
						result = (Button)mDialog_nest.findViewById(R.id.rm_calculator_nest_diag_4_result);
						result.setOnClickListener(new OnClickListener() {
							float v_lbs, v_kg;
							@Override
							public void onClick(View v) {
								if(kg.getText().toString().equals("") && lbs.getText().toString().equals("")){
									// 둘 다 입력값이 없을 때
									Toast.makeText(HomeActivity.this, "kg, lbs 둘 중 하나를 입력하세요.", Toast.LENGTH_SHORT ).show();
								}
								else if(!kg.getText().toString().equals("") && lbs.getText().toString().equals("")){
									// kg만 입력값이 있을 때
									try{
										v_kg = Float.parseFloat(kg.getText().toString());
										v_lbs = v_kg*2.204623f;
										lbs.setText(String.format("%.1f", v_lbs));
										Toast.makeText(HomeActivity.this, "계산 완료", Toast.LENGTH_SHORT ).show();
									} catch(NumberFormatException e){
										Toast.makeText(HomeActivity.this, "올바른 값을 입력하십시오", Toast.LENGTH_SHORT).show();
									}
								}
								else if(kg.getText().toString().equals("") && !lbs.getText().toString().equals("")){
									// lbs만 입력값이 있을 때
									try{
										v_lbs = Float.parseFloat(lbs.getText().toString());
										v_kg = v_lbs*0.453592f;
										kg.setText(String.format("%.1f", v_kg));
										Toast.makeText(HomeActivity.this, "계산 완료", Toast.LENGTH_SHORT ).show();
									} catch(NumberFormatException e){
										Toast.makeText(HomeActivity.this, "올바른 값을 입력하십시오", Toast.LENGTH_SHORT).show();
									}
								}
								else if(!kg.getText().toString().equals("") && !lbs.getText().toString().equals("")){
									// 둘 다 입력값이 있을 때
									Toast.makeText(HomeActivity.this, "kg, lbs 둘 중 하나만 입력하세요.", Toast.LENGTH_SHORT ).show();
								}
							}
						});
						mDialog_nest.show();
					}
				});
				mDialog.show();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		/*
		 * 프로필 미 입력시 입력
		 */
		mSAPDB = new SAPDatabaseManagement(this);
//		mWrightDB = mSAPDB.getWritableDatabase();
		mReadDB = mSAPDB.getReadableDatabase();
		mCursor = mReadDB.rawQuery("SELECT id FROM db_profile", null);
		if(mCursor.getCount() == 0){ 
			Log.d("SAP test", "no prifile data");
			startActivity(new Intent(this, NoProfileActivity.class));
		}
		mCursor.close();
		mReadDB.close();
		mSAPDB.close();
	}
	
	private int MostClosetCounts(float input){
		int res=0;
		
		for(int i=1 ; i<21 ; i++)
		{
			if(input-RMCoefficient[i]<0){
				res = i-1;
				break;
			}
		}
		
		return res;
	}
}
