package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class SplineChart extends View {

    private Renderer renderer =  new Renderer();

    private Paint paint;
    private List<ChartData>  values;
    private List<String> hori_labels;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private String description;
    private float horizontal_width,border = 30, horstart = border * 2,  circleSize = 5f;
    private int parentHeight ,parentWidth;
    private Boolean gesture = false;
    private Canvas canvas;
    private List<ChartData> list_cordinate = new ArrayList<>();
    private float y_cordinate, height ,width, maxY_values, maxX_values, min, graphheight, graphwidth;
    private boolean area_spline = false;

    public SplineChart(Context context, AttributeSet attrs){
        super(context, attrs);

        Paint paint = new Paint();
        this.paint = paint;
    }

    public void setData(List<ChartData> values){

        if(values != null)
            this.values = values;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setLabels(List<String> hori_labels){

        if (hori_labels != null)
            this.hori_labels = hori_labels;
    }

    public void setCircleSize(Float circleSize){
        this.circleSize = circleSize;
    }

    public void setSplineArea(boolean splineArea){
        this.area_spline = splineArea;
    }

    // Get the Width and Height defined in the activity xml file
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {

        intilaizeValue(canvas);

        AxisFormatter axisFormatter = new AxisFormatter();
        axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                horizontal_width_list, paint, values, maxX_values, description, false, false);

        if (maxY_values != min && values != null) {

            paint.setColor(Color.BLUE);
            paint.setStyle(Paint.Style.STROKE);

            list_cordinate = StoredCordinate(graphheight);

            renderer.DrawCubicPath(canvas, list_cordinate, paint, graphheight+border,area_spline);
            renderer.DrawCircle(canvas, list_cordinate, paint, circleSize);

            DrawText();

            if(gesture == true) {
                canvas.restore();
            }
        }
    }


    private void intilaizeValue(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;
        AxisFormatter axisFormatter = new AxisFormatter();
        maxY_values = axisFormatter.getMaxY_Values(values);
        maxX_values = axisFormatter.getMaxX_Values(values);
        min = axisFormatter.getMinValues(values);
        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;
    }


    private void DrawText() {
        for (int i = 0; i < values.size(); i++) {
            canvas.drawText("(" + values.get(i).getX_values() + ", " + values.get(i).getY_values() + ")",
                    list_cordinate.get(i).getX_values() - 30,
                    list_cordinate.get(i).getY_values(), paint);
        }
    }

    private  List<ChartData> StoredCordinate(Float graphheight){

        float colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

        for(int i = 0;i<values.size(); i++){

            float x_ratio = (maxX_values/ (values.size()-1));
            float x_cordinate = (colwidth/x_ratio) *values.get(i).getX_values();
            float line_height = (graphheight / maxY_values) * values.get(i).getY_values();
            y_cordinate = (border - line_height) + graphheight ;
            list_cordinate.add(new ChartData(y_cordinate,x_cordinate + horstart));
        }

        return list_cordinate;
    }

}