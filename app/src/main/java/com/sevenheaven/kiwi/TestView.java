package com.sevenheaven.kiwi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import android.graphics.Matrix;

import com.sevenheaven.kiwi.shapes.StarShape;
import com.sevenheaven.kiwi.R;

/**
 * Created by caifangmao on 15/3/2.
 */
public class TestView extends View {

    private Paint paint;

    private float xR;
    private float yR;
    private int screenWidth;
    private int screenHeight;

    PorterDuffXfermode xfermode;

    private Bitmap bitmap;

    private Bitmap src;
    private Bitmap dst;

    private Drawable drawable;

    private PathEffect pathEffect;

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
        paint.setStrokeWidth(50);

        screenWidth = context.getResources().getDisplayMetrics().widthPixels;
        screenHeight = context.getResources().getDisplayMetrics().heightPixels;


        bitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

        src = makeSrc(200, 200);
        dst = makeDst(200, 200);

        pathEffect = new PathDashPathEffect(makeSemiCircle(), 20, 0.0f, PathDashPathEffect.Style.TRANSLATE);
        //paint.setPathEffect(pathEffect);
    }

    private Path makeSemiCircle(){
        Path p = new Path();
        p.moveTo(0, 0);
        p.addCircle(10, 0, 10, Path.Direction.CW);
        return p;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                xR = event.getX() / (float) screenWidth;
                yR = event.getY() / (float) screenHeight;

                xR = xR > 1F ? 1F : xR;
                xR = xR < 0 ? 0 : xR;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.values()[(int) (xR * PorterDuff.Mode.values().length)]);
        invalidate();

        return true;
    }

    static Bitmap makeDst(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFF0099CC);
        c.drawOval(new RectF(0, 0, w*3/4, h*3/4), p);
        return bm;
    }

    // create a bitmap with a rect, used for the "src" image
    static Bitmap makeSrc(int w, int h) {
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

        p.setColor(0xFFFF7733);
        c.drawRect(w/3, h/3, w*19/20, h*19/20, p);
        return bm;
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


        canvas.drawBitmap(dst, 0, 0, paint);

        paint.setXfermode(xfermode);



        paint.setColor(0xFFFF7733);
        canvas.drawBitmap(src, 0, 0, paint);
        paint.setXfermode(null);


        paint.setTextSize(50);
        canvas.drawText(PorterDuff.Mode.values()[(int) (xR * PorterDuff.Mode.values().length)].name(), 0, 400, paint);

    }
}
