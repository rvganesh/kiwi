package com.sevenheaven.kiwi.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RectShape;

/**
 * Created by caifangmao on 15/3/2.
 */
public class StarShape extends RectShape {

    private int angles;
    private float corner;
    private boolean star;

    private float starRatio = 0.65F;
    private static final double startAngle = -(Math.PI / 2);

    private Path starPath;

    public StarShape(int angles, float corner, boolean star){
        this(angles, corner, star, -1);
    }

    public StarShape(int angles, float corner, boolean star, float starRatio){
        if(corner > 0.5F) throw new IllegalArgumentException("corners range is (0 - 0.5)");
        this.angles = angles;
        this.corner = corner;
        this.star = star;
        this.starRatio = starRatio == -1 ? this.starRatio : starRatio;

        starPath = new Path();
    }

    @Override
    public void draw(Canvas canvas, Paint paint){

        starPath.reset();

        RectF bound = rect();
        int radius = (int) ((bound.width() > bound.height() ? bound.height() : bound.width()) * 0.5F);
        int centerX = (int) (bound.width() * 0.5F);
        int centerY = (int) (bound.height() * 0.5F);

        double angle = startAngle;
        double angleStep = Math.PI * 2 / angles;
        double halfStep = angleStep * 0.5F;
        float[] positions;

        if(corner == 0){
            positions = centerRadiusPoint(centerX, centerY, angle, radius);
            starPath.moveTo(positions[0], positions[1]);
            if(star){
                positions = centerRadiusPoint(centerX, centerY, angle + halfStep, radius * starRatio);
                starPath.lineTo(positions[0], positions[1]);
            }
            for(int i = 0; i < angles - 1; i++){
                angle += angleStep;
                positions = centerRadiusPoint(centerX, centerY, angle, radius);
                starPath.lineTo(positions[0], positions[1]);

                if(star){
                    positions = centerRadiusPoint(centerX, centerY, angle + halfStep, radius * starRatio);
                    starPath.lineTo(positions[0], positions[1]);
                }
            }

            starPath.close();
        }else{
            float[] startP = centerRadiusPoint(centerX, centerY, angle - (star ? halfStep : angleStep), star ? radius * starRatio : radius);
            float[] centerP = centerRadiusPoint(centerX, centerY, angle, radius);
            float[] endP = centerRadiusPoint(centerX, centerY, angle + (star ? halfStep : angleStep), star ? radius * starRatio : radius);

            float[] bezierStart = segLine(centerP[0], centerP[1], startP[0], startP[1], this.corner);
            float[] bezierEnd = segLine(centerP[0], centerP[1], endP[0], endP[1], this.corner);

            float[] nextStart = segLine(endP[0], endP[1], centerP[0], centerP[1], this.corner);

            starPath.moveTo(bezierStart[0], bezierStart[1]);
            starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
            starPath.lineTo(nextStart[0], nextStart[1]);

            if(star){
                centerP = endP.clone();
                endP = centerRadiusPoint(centerX, centerY, angle + angleStep, radius);

                bezierEnd = segLine(centerP[0], centerP[1], endP[0], endP[1], this.corner);

                nextStart = segLine(endP[0], endP[1], centerP[0], centerP[1], this.corner);

                starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
                starPath.lineTo(nextStart[0], nextStart[1]);
            }

            for(int i = 0; i < angles - 1; i++){
                angle += angleStep;
                centerP = endP.clone();
                endP = centerRadiusPoint(centerX, centerY, angle + (star ? halfStep : angleStep), star ? radius * starRatio : radius);

                bezierEnd = segLine(centerP[0], centerP[1], endP[0], endP[1], this.corner);

                nextStart = segLine(endP[0], endP[1], centerP[0], centerP[1], this.corner);

                starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
                starPath.lineTo(nextStart[0], nextStart[1]);

                if(star){
                    centerP = endP;
                    endP = centerRadiusPoint(centerX, centerY, angle + angleStep, radius);

                    bezierEnd = segLine(centerP[0], centerP[1], endP[0], endP[1], this.corner);

                    nextStart = segLine(endP[0], endP[1], centerP[0], centerP[1], this.corner);

                    starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
                    starPath.lineTo(nextStart[0], nextStart[1]);
                }
            }

            starPath.close();
        }

        canvas.drawPath(starPath, paint);
    }

    private float[] segLine(float x0, float y0, float x1, float y1, float ratio){
        float dx = x1 - x0;
        float dy = y1 - y0;

        dx *= ratio;
        dy *= ratio;

        return new float[]{x0 + dx, y0 + dy};
    }

    private float[] centerRadiusPoint(int centerX, int centerY, double angle, double radius){
        float x = (float) (radius * Math.cos(angle) + centerX);
        float y = (float) (radius * Math.sin(angle) + centerY);

        return new float[]{x, y};
    }
}
