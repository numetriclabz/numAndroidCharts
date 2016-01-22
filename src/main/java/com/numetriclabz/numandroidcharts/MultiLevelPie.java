package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.List;
import java.util.Random;

public class MultiLevelPie extends View{

    List<ChartData> data;
    Paint piePaint;
    RectF mRectF  = new RectF();
    int width;
    int height;
    float midX, midY, radius, innerRadius;


    public MultiLevelPie(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        init();
    }


    public void init(){

        piePaint = new Paint();

        piePaint.setAntiAlias(true);
        piePaint.setDither(true);
        piePaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        width = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));
        height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        canvas.drawColor(Color.TRANSPARENT);
        piePaint.reset();
        piePaint.setAntiAlias(true);



        midX = getWidth() / 2;
        midY = getHeight() / 2;

        if (midX < midY) {
            radius = midX;
        } else {
            radius = midY;
        }

        innerRadius = radius / 2;

        for (int i = 0; i < this.data.size(); i++){

            Float[] val = this.data.get(i).getY_List();
            drawChart(val, canvas, radius/(i+1));
        }
    }

    private void drawChart(Float[] val, Canvas canvas, float radius){

        float innerRadius = radius/2;

        float currentAngle = 270;
        float currentSweep = 0;

        float totalValue = 0;

        for(int i = 0; i < val.length; i++) {
            totalValue += val[i];
        }

        for(int i = 0; i < val.length; i++) {

            Path p = new Path();
            p.reset();

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            piePaint.setColor(color);

            currentSweep = (val[i]/ totalValue) * (360);

            mRectF.set(midX - radius, midY - radius, midX + radius, midY + radius);
            createArc(p, mRectF, currentSweep, currentAngle, currentSweep);
            mRectF.set(midX - innerRadius, midY - innerRadius, midX + innerRadius, midY + innerRadius);
            createArc(p, mRectF, currentSweep, currentAngle + currentSweep, -currentSweep);

            p.close();

            canvas.drawPath(p, piePaint);
            currentAngle = currentAngle + currentSweep;
        }
    }


    private void createArc(Path p, RectF mRectF, float currentSweep, float startAngle, float sweepAngle) {
        if (currentSweep == 360) {
            p.addArc(mRectF, startAngle, sweepAngle);
        } else {
            p.arcTo(mRectF, startAngle, sweepAngle);
        }
    }

    public void setData(List<ChartData> data){

        this.data = data;
        invalidate();
    }
}
