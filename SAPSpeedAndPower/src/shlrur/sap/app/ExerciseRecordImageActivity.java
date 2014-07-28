package shlrur.sap.app;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ExerciseRecordImageActivity extends Activity {
	
	private SAPDatabaseManagement mSAPDB;
	private SQLiteDatabase mReadDB;
	private SQLiteDatabase mWrightDB;
	
	private Cursor mCursor;
	
	private TextView mTitle;
	private ViewTouchImage mImage;
	private Button mSave, mDelete, mLoad;
	
	private Bitmap mPhoto;
	
	private int mYear, mMonth, mDay, mWeek;
	private String[] dayOfWeek={"일요일","월요일","화요일","수요일","목요일","금요일","토요일"};
	private String mDate;
	
	private final int PICK_FROM_ALBUM = 0;
	
	private Uri mImageCaptureUri;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.exercise_record_image_layout);
		
		mTitle		= (TextView)findViewById(R.id.exercise_record_image_title);
		mImage 		= (ViewTouchImage)findViewById(R.id.exercise_record_image_img);
		mSave 		= (Button)findViewById(R.id.exercise_record_image_btn_save);
		mDelete 	= (Button)findViewById(R.id.exercise_record_image_btn_delete);
		mLoad		= (Button)findViewById(R.id.exercise_record_image_btn_load);
		
		mSave.setEnabled(false);
		
		Intent get_intent = getIntent();
		mDate = get_intent.getStringExtra("date");
		
		mYear = Integer.parseInt(mDate.substring(0, 4));
		mMonth = Integer.parseInt(mDate.substring(4, 6));
		mDay = Integer.parseInt(mDate.substring(6, 8));

		Calendar cal = Calendar.getInstance();
		cal.set(mYear, mMonth-1, mDay);
		mWeek = cal.get(Calendar.DAY_OF_WEEK);
		mTitle.setText(mYear+"년 "+mMonth+"월 "+mDay+"일 "+dayOfWeek[mWeek-1]+ "\n훈련 사진");
		
		getExerciseRecordImagefromDB();
				
		mSave.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				new AlertDialog.Builder(ExerciseRecordImageActivity.this).setTitle("저장 하시겠습니까?")
				.setMessage(mYear+"년 "+mMonth+"월 "+mDay+"일 "+dayOfWeek[mWeek-1]+ "\n훈련 사진을 변경하시겠습니까?")
				.setPositiveButton("저장", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						mPhoto.compress(Bitmap.CompressFormat.JPEG, 50, baos);
						byte[] imageByte = baos.toByteArray();
						
						setExerciseRecordImagetoDB(imageByte);
						
						Intent intent = new Intent();
						intent.putExtra("result", "save");
						setResult(RESULT_OK, intent);
						finish();
					}

				})
				.setNegativeButton("재확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
			}
		});
		
		mDelete.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new AlertDialog.Builder(ExerciseRecordImageActivity.this).setTitle("저장 하시겠습니까?")
				.setMessage(mYear+"년 "+mMonth+"월 "+mDay+"일 "+dayOfWeek[mWeek-1]+ "\n훈련 사진을 삭제하시겠습니까?")
				.setPositiveButton("삭제", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						byte[] imageByte = null;
						
						setExerciseRecordImagetoDB(imageByte);
						
						Intent intent = new Intent();
						intent.putExtra("result", "delete");
						setResult(RESULT_OK, intent);
						finish();
					}

				})
				.setNegativeButton("재확인", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {}
				}).show();
				
			}
		});
		
		mLoad.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {

				DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						doTakeAlbumAction();
					}
				};

				DialogInterface.OnClickListener cancelListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				};

				new AlertDialog.Builder(ExerciseRecordImageActivity.this)
						.setTitle("업로드할 이미지 선택")
						.setPositiveButton("앨범선택", albumListener)
						.setNegativeButton("취소", cancelListener).show();
			}
		});
	}

	private void setExerciseRecordImagetoDB(byte[] imageByte) {
		mSAPDB = new SAPDatabaseManagement(ExerciseRecordImageActivity.this);
		mWrightDB = mSAPDB.getWritableDatabase();
		
		String tDate;
		tDate = mDate.substring(0, 4)+"-"+mDate.substring(4, 6)+"-"+mDate.substring(6, 8);
		Log.d("SAP", tDate+" record image");

		mWrightDB.execSQL("UPDATE db_exerciserecord " +
							"SET body_image=?" +
							"WHERE date='"+tDate+"';"
							, new Object[]{imageByte});
		
		mWrightDB.close();
		mSAPDB.close();
	}
	
	private void getExerciseRecordImagefromDB(){
		mSAPDB = new SAPDatabaseManagement(ExerciseRecordImageActivity.this);
		mReadDB = mSAPDB.getReadableDatabase();
		
		String tDate;
		tDate = mDate.substring(0, 4)+"-"+mDate.substring(4, 6)+"-"+mDate.substring(6, 8);
		Log.d("SAP", tDate+" record image");
		
		mCursor = mReadDB.rawQuery("SELECT * FROM db_exerciserecord where date='"+tDate+"'", null);
		mCursor.moveToLast();
		
		byte[] img_byte = mCursor.getBlob(2);
		if(img_byte==null){
			Log.d("SAP", "NULL IMAGE");
			mImage.setImageDrawable(getResources().getDrawable(R.drawable.no_image));
			mDelete.setEnabled(false);
		}
		else{
			try{
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				mImage.setImageBitmap(BitmapFactory.decodeByteArray(img_byte, 0, img_byte.length, options));
			}catch(Exception e){
				Toast.makeText(ExerciseRecordImageActivity.this, "에러가 발생했습니다. 버그를 e-mail로 보내주세요.", Toast.LENGTH_SHORT).show();
			}
		}
		
		mCursor.close();
		mReadDB.close();
		mSAPDB.close();
	}
	
	/**
	 * 앨범에서 이미지 가져오기
	 */
	private void doTakeAlbumAction() {
		// 앨범 호출
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		startActivityForResult(intent, PICK_FROM_ALBUM);
	}

	public synchronized static Bitmap GetRotatedBitmap(Bitmap bitmap, int degrees) 
	{
	    if ( degrees != 0 && bitmap != null ) 
	    {
	        Matrix m = new Matrix();
	        m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2 );
	        try 
	        {
	            Bitmap b2 = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
	            if (bitmap != b2) 
	            {
	            	bitmap.recycle();
	            	bitmap = b2;
	            }
	        } 
	        catch (OutOfMemoryError ex) 
	        {
	            // We have no memory to rotate. Return the original bitmap.
	        }
	    }
	    
	    return bitmap;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			return;
		}

		switch (requestCode) {
		
		case PICK_FROM_ALBUM: {

			mImageCaptureUri = data.getData();
			
			mPhoto = null;

			String[] proj = { MediaStore.Images.Media.DATA,	MediaStore.Images.Media._ID, MediaStore.Images.ImageColumns.ORIENTATION };
			@SuppressWarnings("deprecation")
			Cursor cursor = this.managedQuery(mImageCaptureUri, proj, // Which columns to return
											null, // WHERE clause; which rows to return (all rows)
											null, // WHERE clause selection arguments (none)
											null); // Order-by clause (ascending by name)
			
			int orientation_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.ImageColumns.ORIENTATION);
			String orientation="0";
			if (cursor.moveToFirst()) {
				orientation = cursor.getString(orientation_ColumnIndex);
				Log.d("SAP", "Degree:"+orientation);
			}
			
			try {
				mPhoto = Images.Media.getBitmap(getContentResolver(), mImageCaptureUri );
				mPhoto = GetRotatedBitmap(mPhoto, Integer.parseInt(orientation));
				mImage.setImageBitmap(mPhoto);
				mSave.setEnabled(true);
				mDelete.setEnabled(true);
			} catch (FileNotFoundException e) {
				Toast.makeText(ExerciseRecordImageActivity.this, "이미지 가져오기 실패", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(ExerciseRecordImageActivity.this, "이미지 가져오기 실패", Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			
			break;
		}
		}
	}

	@Override
	public void onBackPressed(){
		setResult(RESULT_CANCELED);
		finish();
	}
}
