package shlrur.sap.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class HelpActivity extends Activity {

	private Button btn_home, btn_wod, btn_exercise_record, btn_rm_calculator;
	private Button btn_setup, btn_backup, btn_export, btn_suggestion;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_layout);
		
		btn_home			= (Button)findViewById(R.id.help_home);
		btn_wod				= (Button)findViewById(R.id.help_wod);
		btn_exercise_record	= (Button)findViewById(R.id.help_exercise_record); 
		btn_rm_calculator	= (Button)findViewById(R.id.help_rm_calculator);
		btn_setup			= (Button)findViewById(R.id.help_setup); 
		btn_backup			= (Button)findViewById(R.id.help_backup); 
		btn_export			= (Button)findViewById(R.id.help_export); 
		btn_suggestion		= (Button)findViewById(R.id.help_suggestion);

		btn_home.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelpActivity.this, HelpHomeActivity.class);
				startActivity(intent);
			}
		});
		btn_wod.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelpActivity.this, HelpWODActivity.class);
				startActivity(intent);
			}
		});
		btn_exercise_record.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelpActivity.this, HelpExerciseRecordActivity.class);
				startActivity(intent);
			}
		});
		btn_rm_calculator.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelpActivity.this, HelpRMUnitCalActivity.class);
				startActivity(intent);
			}
		});
		btn_setup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelpActivity.this, HelpSetupActivity.class);
				startActivity(intent);
			}
		});
		btn_backup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelpActivity.this, HelpBackupActivity.class);
				startActivity(intent);
			}
		});
		btn_export.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelpActivity.this, HelpExportActivity.class);
				startActivity(intent);
			}
		});
		btn_suggestion.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelpActivity.this, HelpSuggestionActivity.class);
				startActivity(intent);
			}
		});
	}
}