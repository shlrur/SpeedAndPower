package shlrur.sap.app;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class LogoActivity extends Activity {

	private ImageView mLogo;
	private LinearLayout mThisView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo_layout);

		mThisView = (LinearLayout) findViewById(R.id.logo_layout);
		BitmapDrawable bg = (BitmapDrawable)getResources().getDrawable(R.drawable.back_img_1);
		bg.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
		
//		int sdk = android.os.Build.VERSION.SDK_INT;
//		if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN)
//			mThisView.setBackgroundDrawable((Drawable)bg);
//		else
			mThisView.setBackground(bg);
		
		
		new Thread(new Runnable() {

			public void run() {
				try {
					mLogo = (ImageView) findViewById(R.id.logo_image); // 인트로 이미지 지정
					Animation alphaAnim = AnimationUtils.loadAnimation(LogoActivity.this, R.anim.logo_anim); // 애니 설정 파일
					mLogo.startAnimation(alphaAnim);
					Thread.sleep(3000); // 3초간 로고를 보여준다.
					setResult(RESULT_OK);
					finish();
				} catch (Exception e) {
				}
			}
		}).start();
	}
}