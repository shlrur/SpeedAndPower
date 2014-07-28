package shlrur.sap.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SAPDatabaseManagement extends SQLiteOpenHelper{

	public SAPDatabaseManagement(Context context) {
		super(context, "SAP.db", null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS db_profile ( " +
				"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"date TEXT NOT NULL, " +
				"height DOUBLE NOT NULL, " +
				"weight DOUBLE NOT NULL, " +
				"muscle DOUBLE NOT NULL, " +
				"fat DOUBLE NOT NULL);");
		Log.d("SAP test", "make profile table");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS db_strength ( " +
				"id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				"date TEXT NOT NULL, " +
				"squat DOUBLE NOT NULL, " +
				"deadlift DOUBLE NOT NULL, " +
				"calfraise DOUBLE NOT NULL, " +
				"benchpress DOUBLE NOT NULL, " +
				"barbellrow DOUBLE NOT NULL, " +
				"curl DOUBLE NOT NULL, " +
				"militarypress DOUBLE NOT NULL, " +
				"pullup DOUBLE NOT NULL);");
		Log.d("SAP test", "make strength RM table");
		
		db.execSQL("CREATE TABLE IF NOT EXISTS db_exerciserecord ( " +
				"date TEXT NOT NULL, " +
				"record TEXT NOT NULL, " +
				"body_image BLOB, " +
				"PRIMARY KEY(date));");
		Log.d("SAP test", "make exercise record table");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS db_profile");
		db.execSQL("DROP TABLE IF EXISTS db_strength");
		db.execSQL("DROP TABLE IF EXISTS db_exerciserecord");
		onCreate(db);
	}

}
