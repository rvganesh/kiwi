package com.sevenheaven.kiwi.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by caifangmao on 15/4/23.
 */
public class GestureImageView extends ImageView {

    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_DRAG = 1;
    private static final int TOUCH_ZOOM = 2;

    private int mTouchMode = TOUCH_NONE;

    private int mIntrinsicWidth;
    private int mIntrinsicHeight;

    private PointF[] mStartPoints;
    private PointF[] mCurrentPoints;

    PointF middlePoint;
    PointF sMiddlePoint;

    private float mCurrentScale;
    private PointF mImageAnchor;

    int sDx = 0, sDy = 0;
    int cDx = 0, cDy = 0;

    float sDistance = 0;
    float cDistance = 0;

    private Matrix mMatrix;
    private float mScale = 1;
    private float mRotate = 0;
    private PointF mMatrixPoint;

    private Rect imageBound;

    private GestureDetector gestureDetector;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onDown(MotionEvent event){
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent event){
            Log.d("double", "confirm");
            return true;
        }
    };

    public GestureImageView(Context context){
        this(context, null);
    }

    public GestureImageView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public GestureImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        mStartPoints = new PointF[2];
        mCurrentPoints = new PointF[2];

        mStartPoints[0] = new PointF(-1, -1);
        mStartPoints[1] = new PointF(-1, -1);
        mCurrentPoints[0] = new PointF(-1, -1);
        mCurrentPoints[1] = new PointF(-1, -1);

        mCurrentScale = 1;
        mImageAnchor = new PointF();

        gestureDetector = new GestureDetector(context, gestureListener);

        mMatrix = new Matrix();
        mMatrixPoint = new PointF();

        middlePoint = new PointF();
        sMiddlePoint = new PointF();

        imageBound = new Rect();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int result = -1;

        if(event.getPointerCount() > 0) Log.d("x0:" + event.getX(), "y0:" + event.getY());
        if(event.getPointerCount() > 1) Log.d("x1:" + event.getX(event.getPointerId(1)), "y1:" + event.getY(event.getPointerId(1)));

        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_DOWN:
                mTouchMode = TOUCH_DRAG;
                mStartPoints[0].set(event.getX(), event.getY());
                result = gestureDetector.onTouchEvent(event) ? 1 : 0;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mTouchMode = TOUCH_ZOOM;
                mStartPoints[0].set(event.getX(0), event.getY(0));
                mStartPoints[1].set(event.getX(1), event.getY(1));

                sDx = (int) (mStartPoints[1].x - mStartPoints[0].x);
                sDy = (int) (mStartPoints[1].y - mStartPoints[0].y);

                sMiddlePoint = new PointF(mStartPoints[0].x + sDx / 2, mStartPoints[0].y + sDy / 2);

                sDistance = (int) Math.sqrt(sDx * sDx + sDy * sDy);

                break;
            case MotionEvent.ACTION_MOVE:
                if(event.getPointerCount() <= 1){
                    mTouchMode = TOUCH_DRAG;
                }else{
                    mTouchMode = TOUCH_ZOOM;
                }
                Log.d("touchMode", mTouchMode == 1 ? "TOUCH_DRAG" : "TOUCH_ZOOM" );
                switch(mTouchMode){
                    case TOUCH_NONE:
                        break;
                    case TOUCH_DRAG:
                        mCurrentPoints[0].set(event.getX(), event.getY());
                        mMatrix.setScale(mScale, mScale);
                        mMatrix.preRotate((float) Math.toDegrees(mRotate));
                        mMatrix.postTranslate(mCurrentPoints[0].x + (mMatrixPoint.x - mStartPoints[0].x), mCurrentPoints[0].y + (mMatrixPoint.y - mStartPoints[0].y));

                        setImageMatrix(mMatrix);

                        result = gestureDetector.onTouchEvent(event) ? 1 : 0;
                        break;
                    case TOUCH_ZOOM:
                        mCurrentPoints[0].set(event.getX(0), event.getY(0));
                        mCurrentPoints[1].set(event.getX(1), event.getY(1));

                        cDx = (int) (mCurrentPoints[1].x - mCurrentPoints[0].x);
                        cDy = (int) (mCurrentPoints[1].y - mCurrentPoints[0].y);

                        cDistance = (int) Math.sqrt(cDx * cDx + cDy * cDy);

                        Log.d("cDistance:" + cDistance, "sDistance:" + sDistance);

                        float scale = cDistance / sDistance;

                        middlePoint = new PointF(mCurrentPoints[0].x + cDx / 2, mCurrentPoints[0].y + cDy / 2);
                        Log.d("scale:" + scale, "scale");

                        scale *= mScale;

                        PointF sRotatePoint = new PointF(mStartPoints[0].x - sMiddlePoint.x, mStartPoints[0].y - sMiddlePoint.y);
                        PointF cRotatePoint = new PointF(mCurrentPoints[0].x - middlePoint.x, mCurrentPoints[0].y - middlePoint.y);

                        mRotate = (float) (Math.atan2(cRotatePoint.y, cRotatePoint.x) - Math.atan2(sRotatePoint.y, sRotatePoint.y));


                        mMatrix.setScale(scale, scale);
                        mMatrix.preRotate((float) Math.toDegrees(mRotate));
                        mMatrix.postTranslate(middlePoint.x + (mMatrixPoint.x - sMiddlePoint.x), middlePoint.y + (mMatrixPoint.y - sMiddlePoint.y));

                        setImageMatrix(mMatrix);
                        break;
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if(event.getPointerCount() <= 1){
                    mTouchMode = TOUCH_DRAG;
                }

                float[] values = new float[9];
                mMatrix.getValues(values);

                mScale = values[0];

                mMatrixPoint.set(values[2], values[5]);

                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mTouchMode = TOUCH_NONE;

                values = new float[9];
                mMatrix.getValues(values);

                mMatrixPoint.set(values[2], values[5]);

                result = gestureDetector.onTouchEvent(event) ? 1 : 0;

                break;
        }

        Log.d("matrixPointX:" + mMatrixPoint.x, "matrixPointY:" + mMatrixPoint.y);

        switch(result){
            case -1:
                return true;
            case 0:
                return false;
            case 1:
                return true;
            default:
                return false;
        }
    }

    @Override
    public void setImageResource(int resId){
        super.setImageResource(resId);

        reconfigSize();
    }

    @Override
    public void setImageBitmap(Bitmap bitmap){
        super.setImageBitmap(bitmap);

        reconfigSize();
    }

    @Override
    public void setImageDrawable(Drawable drawable){
        super.setImageDrawable(drawable);

        reconfigSize();
    }

    @Override
    public void setImageURI(Uri uri){
        super.setImageURI(uri);

        reconfigSize();
    }

    private void reconfigSize(){
        if(getDrawable() != null){
            mIntrinsicWidth = getDrawable().getIntrinsicWidth();
            mIntrinsicHeight = getDrawable().getIntrinsicHeight();
        }
    }

    private void recomputeBound(float scale, float rotate, float pivotX, float pivotY){
        int scaledWidth = (int) (getDrawable().getIntrinsicWidth() * scale);
        int scaledHeight = (int) (getDrawable().getIntrinsicHeight() * scale);


    }

    @Override
    public void setImageMatrix(Matrix matrix){
        float[] currentValues = new float[9];
        matrix.getValues(currentValues);

        int scaledWidth = (int) (getDrawable().getIntrinsicWidth() * mScale);
        int scaledHeight = (int) (getDrawable().getIntrinsicHeight() * mScale);

        if(scaledWidth < getWidth()){
            if(currentValues[2] < 0) currentValues[2] = 0;
            if(currentValues[2] + scaledWidth > getWidth()) currentValues[2] = getWidth() - scaledWidth;
        }else{
            if(currentValues[2] > 0) currentValues[2] = 0;
            if(currentValues[2] + scaledWidth < getWidth()) currentValues[2] = getWidth() - scaledWidth;
        }

        if(scaledHeight < getHeight()){
            if(currentValues[5] < 0) currentValues[5] = 0;
            if(currentValues[5] + scaledHeight > getHeight()) currentValues[5] = getHeight() - scaledHeight;
        }else{
            if(currentValues[5] > 0) currentValues[5] = 0;
            if(currentValues[5] + scaledHeight < getHeight()) currentValues[5] = getHeight() - scaledHeight;
        }
        matrix.setValues(currentValues);


        super.setImageMatrix(matrix);
    }
}
