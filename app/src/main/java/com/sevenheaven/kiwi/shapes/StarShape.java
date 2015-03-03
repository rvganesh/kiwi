package com.sevenheaven.kiwi.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RectShape;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by caifangmao on 15/3/2.
 */
public class StarShape extends RectShape {

    private int angles;
    private float corner;
    private boolean star;

    private List<PointF> controlPoints;

    private float starRatio = 0.65F;
    private static final double startAngle = -(Math.PI / 2);

    private Path starPath;

    public StarShape(int angles, float corner, boolean star){
        this(angles, corner, star, -1);
    }

    public StarShape(int angles, float corner, boolean star, float starRatio){
        if(corner > 0.5F || corner < 0) throw new IllegalArgumentException("corners range is (0 - 0.5)");
        this.angles = angles;
        this.corner = corner;
        this.star = star;
        this.starRatio = starRatio == -1 ? this.starRatio : starRatio;

        starPath = new Path();

        controlPoints = new ArrayList<PointF>();
    }

    @Override
    public void draw(Canvas canvas, Paint paint){

        starPath.reset();
        controlPoints.clear();

        RectF bound = rect();
        int radius = (int) ((bound.width() > bound.height() ? bound.height() : bound.width()) * 0.5F);
        int centerX = (int) (bound.width() * 0.5F);
        int centerY = (int) (bound.height() * 0.5F);

        double angle = startAngle;
        double angleStep = Math.PI * 2 / angles;
        double halfStep = angleStep * 0.5F;
        float[] positions;

        //区分corner，corner为0时区分出来以避免不必要的曲线计算
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

            //
            //
            //    /\  <----    )  绘制圆角的多边形， 每个角包含一条曲线和一条直线，曲线用来实现圆角 直线用来连接下一个角，曲线的控制点为原角的顶点，
            //    \/          /    起点和终点分别是以corner为百分比取到得两边线段上的点（绘制星型多边形时 对 凹角做类似处理）
            //


            float[] startP = centerRadiusPoint(centerX, centerY, angle - (star ? halfStep : angleStep), star ? radius * starRatio : radius);
            float[] centerP = centerRadiusPoint(centerX, centerY, angle, radius);
            float[] endP = centerRadiusPoint(centerX, centerY, angle + (star ? halfStep : angleStep), star ? radius * starRatio : radius);

            float[] bezierStart = segLine(centerP[0], centerP[1], startP[0], startP[1], this.corner);
            float[] bezierEnd = segLine(centerP[0], centerP[1], endP[0], endP[1], this.corner);

            float[] nextStart = segLine(endP[0], endP[1], centerP[0], centerP[1], this.corner);

            starPath.moveTo(bezierStart[0], bezierStart[1]);
            starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
            starPath.lineTo(nextStart[0], nextStart[1]);

            int cpIndex = 0;

            controlPoints.add(new PointF(bezierStart[0], bezierStart[1]));
            controlPoints.add(new PointF(centerP[0], centerP[1]));
            controlPoints.add(new PointF(bezierEnd[0], bezierEnd[1]));

            if(star){
                cpIndex += 3;
                centerP = endP.clone();
                endP = centerRadiusPoint(centerX, centerY, angle + angleStep, radius);

                bezierEnd = segLine(centerP[0], centerP[1], endP[0], endP[1], this.corner);

                controlPoints.add(new PointF(nextStart[0], nextStart[1]));

                nextStart = segLine(endP[0], endP[1], centerP[0], centerP[1], this.corner);

                starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
                starPath.lineTo(nextStart[0], nextStart[1]);

                controlPoints.add(new PointF(centerP[0], centerP[1]));
                controlPoints.add(new PointF(bezierEnd[0], bezierEnd[1]));
            }

            for(int i = 0; i < angles - 1; i++){
                cpIndex += 3;

                angle += angleStep;
                centerP = endP.clone();
                endP = centerRadiusPoint(centerX, centerY, angle + (star ? halfStep : angleStep), star ? radius * starRatio : radius);

                bezierEnd = segLine(centerP[0], centerP[1], endP[0], endP[1], this.corner);

                controlPoints.add(new PointF(nextStart[0], nextStart[1]));

                nextStart = segLine(endP[0], endP[1], centerP[0], centerP[1], this.corner);

                starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
                starPath.lineTo(nextStart[0], nextStart[1]);


                controlPoints.add(new PointF(centerP[0], centerP[1]));
                controlPoints.add(new PointF(bezierEnd[0], bezierEnd[1]));

                if(star){
                    cpIndex += 3;
                    centerP = endP;
                    endP = centerRadiusPoint(centerX, centerY, angle + angleStep, radius);

                    bezierEnd = segLine(centerP[0], centerP[1], endP[0], endP[1], this.corner);

                    controlPoints.add(new PointF(nextStart[0], nextStart[1]));

                    nextStart = segLine(endP[0], endP[1], centerP[0], centerP[1], this.corner);

                    starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
                    starPath.lineTo(nextStart[0], nextStart[1]);

                    controlPoints.add(new PointF(centerP[0], centerP[1]));
                    controlPoints.add(new PointF(bezierEnd[0], bezierEnd[1]));
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

    public PointF[] getControlPoints(){
        return controlPoints.toArray(new PointF[controlPoints.size()]);
    }
}
