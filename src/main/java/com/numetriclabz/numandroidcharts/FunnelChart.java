package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;
import java.util.Random;

public class FunnelChart extends View{

    List<ChartData> data;

    public FunnelChart(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }


    @Override
    protected void onDraw(Canvas canvas){

        int startTop = 50;
        int startLeft = 50;
        int endBottom = getHeight() - 50;
        int endRight = getWidth() - 50;

        RectF rectf = new RectF(startLeft, startTop, endRight, endBottom);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);

        if(this.data != null) {

            for(int i = 0; i < this.data.size(); i++){

                Random rnd = new Random();
                int color = Color.argb(255, (int) rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
                paint.setColor(color);
                int total = getTotal();
                Log.e("segment total", "" + total);
                Log.e("segment", ""+this.data.get(i).getPyramid_value());
                float segment = ((this.data.get(i).getPyramid_value()*100)/total);

                Log.e("segment", ""+segment);

                int bottom = startTop + (int)((endBottom*(int)segment)/100);
                Log.e("start and bottom", startTop + " " + bottom);

                Rect rect = new Rect(startLeft, startTop, endRight, bottom);

                canvas.drawRect(rect, paint);

                startTop = bottom;
            }
        }
        Path path = new Path();
        path.reset();


        path.moveTo(50, 50);
        path.lineTo((endRight / 4)+25, (endBottom / 2) + 50);
        path.lineTo((endRight/4)+25, endBottom+50);
        //path.lineTo(endRight / 4, (endBottom/3)+50);
        path.lineTo(50, endBottom+50);
        path.moveTo(endRight, 50);
        path.lineTo(25+(endRight - (endRight / 4)), (endBottom / 2) + 50);
        path.lineTo(25+(endRight - (endRight / 4)), endBottom+50);
        //path.lineTo(endRight, endBottom+50);
        path.lineTo(endRight, endBottom+50);
        path.close();

        paint.setColor(Color.WHITE);
        // Clipping canvas to path and drawing small area
        canvas.save();
        canvas.clipPath(path);
        canvas.drawPath(path, paint);
        canvas.restore();
    }


    private int getTotal(){

        Log.e("data string", this.data.size()+"");
        int total = 0;
        for(int i = 0; i < this.data.size(); i++){

            total += this.data.get(i).getPyramid_value();
        }
        Log.e("data string", total+"");
        return total;
    }

    public void setData(List<ChartData> data){

        this.data = data;
        invalidate();
    }
}
