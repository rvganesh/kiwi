package com.sevenheaven.kiwi.widget;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * Created by caifangmao on 15/4/23.
 */
public class GestureImageView extends ImageView {

    private PointF[] mStartPoints;
    private PointF[] mCurrentPoints;

    private float mCurrentScale;
    private PointF mImageAnchor;

    private Matrix matrix;

    private GestureDetector gestureDetector;

    private GestureDetector.SimpleOnGestureListener gestureListener = new GestureDetector.SimpleOnGestureListener(){

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
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        int action = event.getAction();

        if(event.getPointerCount() > 0) Log.d("x0:" + event.getX(), "y0:" + event.getY());
        if(event.getPointerCount() > 1) Log.d("x1:" + event.getX(event.getPointerId(1)), "y1:" + event.getY(event.getPointerId(1)));

        if(event.getPointerCount() <= 1){
            switch(action){
                case MotionEvent.ACTION_DOWN:
                    mStartPoints[0].set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurrentPoints[0].set(event.getX(), event.getY());
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mStartPoints[0].set(-1, -1);
                    mCurrentPoints[0].set(-1, -1);
                    break;
            }

            return true;
        }else{
            switch(action){
                case MotionEvent.ACTION_DOWN:
                    mStartPoints[0].set(event.getX(event.getPointerId(0)), event.getY(event.getPointerId(0)));
                    mStartPoints[1].set(event.getX(event.getPointerId(1)), event.getY(event.getPointerId(1)));
                    break;
                case MotionEvent.ACTION_MOVE:
                    mCurrentPoints[0].set(event.getX(event.getPointerId(0)), event.getY(event.getPointerId(0)));
                    mCurrentPoints[1].set(event.getX(event.getPointerId(1)), event.getY(event.getPointerId(1)));

                    int sDx = (int) (mStartPoints[1].x - mStartPoints[0].x);
                    int sDy = (int) (mStartPoints[1].y - mStartPoints[0].y);

                    int cDx = (int) (mCurrentPoints[1].x - mCurrentPoints[0].x);
                    int cDy = (int) (mCurrentPoints[1].y - mCurrentPoints[0].y);

                    int sDistance = (int) Math.sqrt(sDx * sDx + sDy * sDy);
                    int cDistance = (int) Math.sqrt(cDx * cDx + cDy * cDy);

                    matrix = getImageMatrix();

                    float scale = (float) cDistance / (float) sDistance;

                    Log.d("scale:" + scale, "scale");

                    matrix.setScale(scale, scale);

                    setImageMatrix(matrix);

                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    mStartPoints[0].set(-1, -1);
                    mStartPoints[1].set(-1, -1);
                    mCurrentPoints[0].set(-1, -1);
                    mCurrentPoints[1].set(-1, -1);
                    break;
            }
        }

        return true;
    }
}
