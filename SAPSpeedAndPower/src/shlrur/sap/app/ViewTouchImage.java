package shlrur.sap.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

public class ViewTouchImage extends ImageView {

	private static final String TAG = "ImgViewChild";
	private static final boolean D = false;

	private Matrix matrix;
	private Matrix savedMatrix;
	private Matrix savedMatrix2;
	
	private Drawable d;

	private static final int NONE = 0;
	private static final int DRAG = 1;
	private static final int ZOOM = 2;
	private int mode = NONE;

	private PointF start = new PointF();
	private PointF mid = new PointF();
	private float oldDist = 1f;

	private static final int WIDTH = 0;
	private static final int HEIGHT = 1;

	private boolean isInit = false;
	
	/** Constants describing the state of this imageview */
//	private boolean isMoving;
//	private boolean isScaling;
//	private boolean isRestoring;

	public ViewTouchImage(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setScaleType(ScaleType.MATRIX);
		matrix = new Matrix();
		savedMatrix = new Matrix();
		savedMatrix2 = new Matrix();
	}


	public ViewTouchImage(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ViewTouchImage(Context context) {
		this(context, null);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		if (D) Log.i(TAG, "onLayout");
		d = this.getDrawable();
		super.onLayout(changed, left, top, right, bottom);
		if (isInit == false){
			init();
			isInit = true;
		}
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		if (D) Log.i(TAG, "setImageBitmap");
		super.setImageBitmap(bm);
		isInit = false;
		init();
	}

	@Override
	public void setImageResource(int resId) {
		if (D) Log.i(TAG, "setImageResource");
		super.setImageResource(resId);
		d = getDrawable();
		isInit = false;
		init();
	}

	protected void init() {
		d = this.getDrawable();
		initImgPos();
		setImageMatrix(matrix);
	}

	/**
	 * Sets the image in the imageview using the matrix
	 */
	public void initImgPos(){
		
		float[] value = new float[9];
		this.matrix.getValues(value);
		
		int width = this.getWidth();
		int height = this.getHeight();
		
		if (d == null)  return;
		int imageWidth = d.getIntrinsicWidth();
		int imageHeight = d.getIntrinsicHeight();
		int scaleWidth = (int) (imageWidth * value[0]);
		int scaleHeight = (int) (imageHeight * value[4]);

		if (imageWidth > width || imageHeight > height){
			
			float xratio = (float)width / (float)imageWidth;
			float yratio = (float)height / (float)imageHeight;
			
			// Math.min fits the image to the shorter axis. (with letterboxes around)
			// Math.max fits the image th the longer axis. (with the other axis cropped)
			value[0] = value[4] = Math.max(xratio, yratio);
		}
		
		scaleWidth = (int) (imageWidth * value[0]);
		scaleHeight = (int) (imageHeight * value[4]);
		
		// align the image to the top left corner
		value[2] = 0;
		value[5] = 0;
		
		// center the image. it will be aligned to the top left corner otherwise.
		value[2] = (float) width / 2 - (float)scaleWidth / 2;
		value[5] = (float) height / 2 - (float)scaleHeight / 2;

		matrix.setValues(value);
		setImageMatrix(matrix);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if(D) dumpEvent(event);

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			savedMatrix.set(matrix);
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			oldDist = spacing(event);
			if (oldDist > 10f) {
				savedMatrix.set(matrix);
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
		case MotionEvent.ACTION_UP:

		case MotionEvent.ACTION_POINTER_UP:
			mode = NONE;
			restore(matrix);
			break;
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				matrix.set(savedMatrix);
				matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);

			}
			else if (mode == ZOOM) {
				float newDist = spacing(event);
				if (newDist > 10f) {
					matrix.set(savedMatrix);
					float scale = newDist / oldDist;
					matrix.postScale(scale, scale, mid.x, mid.y);
				}
			}
			break;
		}

		// Matrix value modification
		// comment out below 2 lines to remove all restrictions on image transformation.
		
		// �� ����
		matrixTuning(matrix);
		setImageMatrix(savedMatrix2);

		// �� ������
		//setImageMatrix(matrix);
		
		return true;
	}

	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float)Math.sqrt(x * x + y * y);
	}

	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	private void matrixTuning(Matrix matrix){
		float[] value = new float[9];
		matrix.getValues(value);
		float[] savedValue = new float[9];
		savedMatrix2.getValues(savedValue);
		
		int width = getWidth();
		int height = getHeight();

		
		Drawable d = getDrawable();
		if (d == null)  return;
		int imageWidth = d.getIntrinsicWidth();
		int imageHeight = d.getIntrinsicHeight();
		int scaleWidth = (int) (imageWidth * value[0]);
		int scaleHeight = (int) (imageHeight * value[4]);

		// don't let the image go outside
		if (value[2] < width - scaleWidth)   value[2] = width - scaleWidth;
		if (value[5] < height - scaleHeight)   value[5] = height - scaleHeight;
		if (value[2] > 0)   value[2] = 0;
		if (value[5] > 0)   value[5] = 0;

		// maximum zoom ratio: 2x
		if (value[0] > 2 || value[4] > 2){
			value[0] = savedValue[0];
			value[4] = savedValue[4];
			value[2] = savedValue[2];
			value[5] = savedValue[5];
		}

		// don't let the image become smaller than the screen
		if (imageWidth > width || imageHeight > height){
			if (scaleWidth < width && scaleHeight < height){
				int target = WIDTH;
				if (imageWidth < imageHeight) target = HEIGHT;

				if (target == WIDTH) value[0] = value[4] = (float)width / imageWidth;
				if (target == HEIGHT) value[0] = value[4] = (float)height / imageHeight;

				scaleWidth = (int) (imageWidth * value[0]);
				scaleHeight = (int) (imageHeight * value[4]);

				if (scaleWidth > width) value[0] = value[4] = (float)width / imageWidth;
				if (scaleHeight > height) value[0] = value[4] = (float)height / imageHeight;
			}
		}

		// don't allow scale down under its size
		else{
			if (value[0] < 1)   value[0] = 1;
			if (value[4] < 1)   value[4] = 1;
		}

		// center the image
		scaleWidth = (int) (imageWidth * value[0]);
		scaleHeight = (int) (imageHeight * value[4]);
		if (scaleWidth < width){
			value[2] = (float) width / 2 - (float)scaleWidth / 2;
		}
		if (scaleHeight < height){
			value[5] = (float) height / 2 - (float)scaleHeight / 2;
		}

		matrix.setValues(value);
		savedMatrix2.set(matrix);
	}
	
	/** Gives animation effect after touchscreen event,
	 * puts the image back into the screen,
	 * limits max zoom at specific ratio. */
	private void restore(Matrix m) {
		
		setImageMatrix(matrix);
	}
	
	/** Show an event in the LogCat view, for debugging */
	private void dumpEvent(MotionEvent event) {
		String names[] = { "DOWN" , "UP" , "MOVE" , "CANCEL" , "OUTSIDE" ,
				"POINTER_DOWN" , "POINTER_UP" , "7?" , "8?" , "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_" ).append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN
				|| actionCode == MotionEvent.ACTION_POINTER_UP) {
			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
			sb.append(")" );
		}
		sb.append("[" );
		for (int i = 0; i < event.getPointerCount(); i++) {
			sb.append("#" ).append(i);
			sb.append("(pid " ).append(event.getPointerId(i));
			sb.append(")=" ).append((int) event.getX(i));
			sb.append("," ).append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";" );
		}
		sb.append("]" );
		Log.d(TAG, sb.toString());
	}

}
