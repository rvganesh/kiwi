package com.sevenheaven.kiwi;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sevenheaven.kiwi.shapes.StarShape;

/**
 * Created by caifangmao on 15/3/2.
 */
public class TestView extends View {

    private Paint paint;

    private float xR;
    private float yR;
    private int screenWidth;
    private int screenHeight;

    public TestView(Context context){
        this(context, null);
    }

    public TestView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public TestView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(0xFF0099CC);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                xR = event.getX() / (float) screenWidth;
                yR = event.getY() / (float) screenHeight;

                xR = xR > 0.5F ? 0.5F : xR;
                xR = xR < 0 ? 0 : xR;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();

        return true;
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);

        canvas.save();
        canvas.translate(20, 20);

        paint.setColor(0xFF0099CC);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

        StarShape starShape = new StarShape(6, xR, true, yR);

        starShape.resize(400, 400);

        starShape.draw(canvas, paint);

        PointF[] controlPoint = starShape.getControlPoints();

        if(controlPoint != null){


            for(int i = 0; i < controlPoint.length; i++){
                if(controlPoint[i] != null){

                    paint.setColor(0x44000000);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(10);

                    if(i == controlPoint.length - 1){
                        canvas.drawLine(controlPoint[i].x, controlPoint[i].y, controlPoint[0].x, controlPoint[0].y, paint);
                    }else{
                        canvas.drawLine(controlPoint[i].x, controlPoint[i].y, controlPoint[i + 1].x, controlPoint[i + 1].y, paint);
                    }

                    paint.setColor(0xFFFF7733);
                    paint.setStyle(Paint.Style.FILL);

                    canvas.drawCircle(controlPoint[i].x, controlPoint[i].y, 4, paint);


                }




            }
        }

        canvas.restore();
    }
}
