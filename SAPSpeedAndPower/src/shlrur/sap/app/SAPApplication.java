package shlrur.sap.app;

import android.app.Application;
import android.util.Log;

public class SAPApplication extends Application {

	boolean isLogo;
	
	@Override
	public void onCreate() {
		Log.d("SAP", "Application onCreate");
		
		isLogo = true;
	}
}