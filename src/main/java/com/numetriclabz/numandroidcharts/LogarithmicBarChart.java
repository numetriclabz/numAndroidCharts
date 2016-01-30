package com.numetriclabz.numandroidcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class LogarithmicBarChart extends View {

    float colwidth, x_cordinate, y_cordinate,graphheight, graphwidth, left, right, top, bottom;
    private float border = 30, horstart = border * 2;

    int height, width, base;
    Paint paint;

    List<String> labels;
    List<ChartData> values;
    private List<Float> horizontal_width_list = new ArrayList<>();

    private List<ChartData> list_cordinate = new ArrayList<>();

    public LogarithmicBarChart(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        Paint paint = new Paint();
        this.paint = paint;

    }

    public void setData(List<ChartData> values){

        if(values != null)
            this.values = values;
    }


    public void setLabels(List<String> hori_labels){

        if (hori_labels != null)
            this.labels = hori_labels;
    }

    public void setBase(int base){
        this.base = base;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int minH = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();

        width = Math.max(minW, MeasureSpec.getSize(widthMeasureSpec));
        height = Math.max(minH, MeasureSpec.getSize(heightMeasureSpec));

        setMeasuredDimension(width, height);
    }


    private void init() {

        graphheight = Math.min(height, width) - (3 * border);
        graphwidth = Math.min(height, width) - (3 * border);
    }


    @Override
    protected void onDraw(Canvas canvas){

        init();

        LogAxis axisRenderer = new LogAxis(width, height, labels, canvas, horizontal_width_list,
                                           paint, values, null, base);

        colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

        list_cordinate = StoredCordinate(graphheight);

        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(2);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        ChartHelper chartHelper = new ChartHelper(list_cordinate, canvas, paint);
        chartHelper.createBar();
    }



    private List<ChartData> StoredCordinate(Float graphheight) {

        List<ChartData> value = values.get(0).getList();

        for(int i = 0;i<value.size(); i++){

            float barheight = (graphheight / 3 ) * ((float)(Math.log(value.get(i).getYValue())/Math.log(10)) - 3);

            left = (i * colwidth) + horstart +10;
            right = ((i * colwidth) + horstart) + (colwidth - 1)-10;
            bottom = graphheight + border;
            top = (border - barheight) + graphheight;

            list_cordinate.add(new ChartData(left, top, right, bottom));
        }

        return list_cordinate;
    }
}
