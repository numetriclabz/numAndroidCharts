package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;
import java.util.Random;

public class PieChartL extends View{


    List<ChartData> data;
    Paint piePaint, textPaint, legendPaint;
    RectF mBounds, legends;
    int width;
    int height;
    int top, left, endBottom, endRight;


    public PieChartL(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        init();
    }


    public void init(){


        piePaint = new Paint();

        piePaint.setAntiAlias(true);
        piePaint.setDither(true);
        piePaint.setStyle(Paint.Style.FILL);

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20f);
        legendPaint = new Paint();
    }


    @Override
    protected void onDraw(Canvas canvas){

        width = getWidth();
        height = getHeight();

        if (data != null){

            top = (int)(height * 0.1f);
            left = (int) (width * 0.1);
            endBottom = width -(int) (width * 0.1);
            endRight = endBottom;

            mBounds = new RectF(left, top, endRight, endBottom);

            float[] segment = pieSegment();

            float segStartPoint = 0;


            int legendTop = height -60;
            int legendLeft = left;
            int legendRight = width;
            int legendBottom = height;

            legends = new RectF(legendLeft, legendTop, legendRight, legendBottom);

            for (int i = 0; i < segment.length; i++){



                String label = this.data.get(i).getPieLabel();
                float text_width = textPaint.measureText(label, 0, label.length());

                Random rnd = new Random();
                int color = Color.argb(255, (int) segment[i], rnd.nextInt(256), rnd.nextInt(256));

                piePaint.setColor(color);
                canvas.drawArc(mBounds, segStartPoint, (segment[i] - 1), true, piePaint);

                segStartPoint += segment[i];

                if(!((width - legendLeft) > (text_width + 60))){

                    legendTop -= 60;
                    legendLeft = left;
                }

                addLegends(canvas, color,  legendTop, legendLeft, legendRight, legendBottom, label);
                legendLeft += ((int)text_width + 60);
            }
        }
        else {
            piePaint.setColor(Color.BLUE);
            canvas.drawOval(mBounds, piePaint);
        }
    }


    private void addLegends(Canvas canvas, int color, int top, int left, int right, int bottom, String label){

        legends = new RectF(left, top, right, bottom);

        Rect r = new Rect(left, top, left + 30, top + 30);
        piePaint.setColor(color);
        canvas.drawRect(r, piePaint);
        canvas.drawText(label, left + 40, top + 20, textPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event){

        if(event.getAction() == MotionEvent.ACTION_DOWN) {

            float relX = event.getX() - (mBounds.right - mBounds.left) * 0.5f;
            float relY = event.getY() - (mBounds.bottom - mBounds.top) * 0.5f;

            float angleInRad = (float)Math.atan2(relY, relX);
            int degree = (int)((angleInRad + Math.PI) * 180 / Math.PI);
        }

            return false;
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
