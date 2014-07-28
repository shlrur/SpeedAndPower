package shlrur.sap.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SetupActivity extends Activity {

	private Button btn_profile, btn_strength;
	private TextView[] tv_profile;
	private TextView[] tv_strength;
	
	private SAPDatabaseManagement mSAPDB;
	private SQLiteDatabase mReadDB;
	
	private ToggleButton tgl_kg, tgl_lbs;
	
	private SharedPreferences mPrefs;
	
	private Cursor cursor_strength, cursor_profile;
	
	private String mUnit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup_layout);
		
		mPrefs = getSharedPreferences("SAPpref", MODE_PRIVATE);
		final SharedPreferences.Editor editor = mPrefs.edit();

		btn_profile	= (Button) findViewById(R.id.setup_profile_setup);
		btn_strength= (Button) findViewById(R.id.setup_strength_setup);
		
		tv_profile = new TextView[5];
		tv_strength = new TextView[9];
		
		tv_profile[0] = (TextView)findViewById(R.id.setup_profile_date);
		tv_profile[1] = (TextView)findViewById(R.id.setup_profile_height);
		tv_profile[2] = (TextView)findViewById(R.id.setup_profile_weight);
		tv_profile[3] = (TextView)findViewById(R.id.setup_profile_muscle);
		tv_profile[4] = (TextView)findViewById(R.id.setup_profile_fat);
		
		tv_strength[0] = (TextView)findViewById(R.id.setup_strength_date);
		tv_strength[1] = (TextView)findViewById(R.id.setup_strength_squat);
		tv_strength[2] = (TextView)findViewById(R.id.setup_strength_deadlift);
		tv_strength[3] = (TextView)findViewById(R.id.setup_strength_carf);
		tv_strength[4] = (TextView)findViewById(R.id.setup_strength_bench);
		tv_strength[5] = (TextView)findViewById(R.id.setup_strength_row);
		tv_strength[6] = (TextView)findViewById(R.id.setup_strength_curl);
		tv_strength[7] = (TextView)findViewById(R.id.setup_strength_military);
		tv_strength[8] = (TextView)findViewById(R.id.setup_strength_pullup);

		tgl_kg 		= (ToggleButton)findViewById(R.id.setup_tgl_kg);
		tgl_lbs		= (ToggleButton)findViewById(R.id.setup_tgl_lbs);
		
		Log.d("SAP",mPrefs.getString("WeightUnit", ""));
		
		if(mPrefs.getString("WeightUnit", "kg").equals("kg")){
			tgl_kg.setChecked(true);
			tgl_lbs.setChecked(false);
			Log.d("SAP","get kg");
		}
		else if(mPrefs.getString("WeightUnit", "kg").equals("lbs")){
			tgl_kg.setChecked(false);
			tgl_lbs.setChecked(true);
			Log.d("SAP","get lbs");
		}
		
		btn_profile.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SetupActivity.this, SetupProfileActivity.class));				
			}
		});
		
		btn_strength.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(SetupActivity.this, SetupStrengthActivity.class));
			}
		});
		
		tgl_kg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					tgl_lbs.setChecked(false);
					editor.putString("WeightUnit", "kg");
					editor.commit();
					Log.d("SAP","set kg");
				}
				else{
					tgl_lbs.setChecked(true);
					editor.putString("WeightUnit", "lbs");
					editor.commit();
					Log.d("SAP","set lbs");
				}
				onResume();
			}
		});
		
		tgl_lbs.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					tgl_kg.setChecked(false);
					editor.putString("WeightUnit", "lbs");
					editor.commit();
					Log.d("SAP","set lbs");
				}
				else{
					tgl_kg.setChecked(true);
					editor.putString("WeightUnit", "kg");
					editor.commit();
					Log.d("SAP","set kg");
				}
				onResume();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		float fat_rate;
		
		mSAPDB = new SAPDatabaseManagement(this);
		mReadDB = mSAPDB.getReadableDatabase();
		
		cursor_strength 	= mReadDB.rawQuery("SELECT * FROM db_strength", null);
		cursor_profile 		= mReadDB.rawQuery("SELECT * FROM db_profile", null);
		cursor_profile.moveToLast();
		cursor_strength.moveToLast();
		
		mUnit = mPrefs.getString("WeightUnit", "kg");
		
//		for(int i=0 ; i<tv_profile.length ; i++){
//			if(i==0)
//				tv_profile[i].setText("Profile\n"+cursor_profile.getString(i+1));
//			else
//				tv_profile[i].setText(cursor_profile.getString(i+1));
//		}
		
		tv_profile[0].setText("Profile\n"+cursor_profile.getString(1));
		tv_profile[1].setText(cursor_profile.getString(2)+" cm");
		tv_profile[2].setText(cursor_profile.getString(3)+" kg");
		tv_profile[3].setText(cursor_profile.getString(4)+" kg");
		
		fat_rate = Float.parseFloat(cursor_profile.getString(5))/Float.parseFloat(cursor_profile.getString(3))*100;
		
		tv_profile[4].setText(cursor_profile.getString(5)+" kg("+String.format("%.1f", fat_rate)+"%)");

		for(int i=0 ; i<tv_strength.length ; i++){
			if(i==0)
				tv_strength[i].setText("Repetition Maximum\n"+cursor_strength.getString(i+1));
			else{
				float temp = Float.parseFloat(cursor_strength.getString(i+1));
				
				if(mUnit.equals("lbs"))
					temp = (float) (temp*2.204623);
				
				tv_strength[i].setText(String.format("%.1f", temp) + " " + mUnit);
			}
		}
		
		cursor_profile.close();
		cursor_strength.close();
		mReadDB.close();
		mSAPDB.close();
	}
}
