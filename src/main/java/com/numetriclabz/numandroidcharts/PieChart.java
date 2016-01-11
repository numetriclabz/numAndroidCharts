package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.Random;

public class PieChart extends View{


    List<ChartData> data;
    Paint piePaint;
    RectF mBounds;
    int width;
    int height;


    public PieChart(Context context,  AttributeSet attributeSet){
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
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        width = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = width  + getPaddingBottom() + getPaddingTop();
        height = Math.min(MeasureSpec.getSize(heightMeasureSpec), minh);
        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas){

        if (data != null){

            int top = 10;
            int left = 0;
            int endBottom = getHeight();
            int endRight = endBottom;


            mBounds = new RectF(left, top, endRight, endBottom);

            float[] segment = pieSegment();

            float segStartPoint = 0;

            for (int i = 0; i < segment.length; i++){

                Random rnd = new Random();
                int color = Color.argb(255, (int) segment[i], rnd.nextInt(256), rnd.nextInt(256));

                piePaint.setColor(color);
                canvas.drawArc(mBounds, segStartPoint, segment[i], true, piePaint);
                segStartPoint += segment[i];
            }
        }
        else {
            piePaint.setColor(Color.BLUE);
            canvas.drawOval(mBounds, piePaint);
        }
    }


    private float[] pieSegment(){

        float[] segValues = new float[this.data.size()];
        float Total = getTotal();

        for (int i = 0; i < this.data.size(); i++){

            segValues[i] = (this.data.get(i).getValue()/Total) * 360;
        }

        return segValues;
    }


    private float getTotal(){

        Log.e("data string", this.data.size()+"");

        float total = 0;

        for(int i = 0; i < this.data.size(); i++){

            total += this.data.get(i).getValue();
        }
        return total;
    }


    public void setData(List<ChartData> data){

        this.data = data;
        invalidate();
    }

}
