package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BarChart extends View {

    private Paint paint;
    private List<ChartData>  values;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private String description;
    private float  border = 30, horstart = border * 2;
    private int parentHeight ,parentWidth;
    private Canvas canvas;
    private List<ChartData> list_cordinate = new ArrayList<>();
    private float height ,width, maxY_values, maxX_values, graphheight, graphwidth;
    private float left, right, top, bottom, barheight, colwidth;
    private AxisFormatter axisFormatter = new AxisFormatter();
    private List<ChartData>  trendzones ;
    private List<ChartData> trendlines;
    private Boolean inverseAxis = false;
    public Bitmap  mBitmap;
    private Paint   mBitmapPaint;

    public BarChart(Context context, AttributeSet attrs){
        super(context, attrs);

        Paint paint = new Paint();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        this.paint = paint;
    }

    public void setData(List<ChartData> values){

        if(values != null) {
            this.values = values;
        }
    }

    public void setTrendZones(List<ChartData> trendzones){
        this.trendzones = trendzones;
    }

    public void setTrendlines(List<ChartData>  trendlines){
        this.trendlines = trendlines;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setInverseY_Axis(boolean inverseAxis){
        this.inverseAxis = inverseAxis;
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {

        init(canvas);

        axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, null, maxY_values, canvas,
                horizontal_width_list, paint, values, maxX_values, description, inverseAxis, false);

        if (values != null) {

            colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

            list_cordinate = StoredCordinate(graphheight);

            if(trendzones != null) {
                ChartHelper chartHelper1 = new ChartHelper(trendzones, canvas, paint);
                chartHelper1.DrawTrendzone(values.size(), colwidth, graphheight, maxY_values);
            }

            if(trendlines != null) {
                ChartHelper chartHelper2 = new ChartHelper(trendlines, canvas, paint);
                chartHelper2.DrawTrendlines(graphheight, maxY_values, graphwidth);
            }

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(0)));
            ChartHelper chartHelper = new ChartHelper(list_cordinate, canvas, paint);
            chartHelper.createBar();
            DrawText();
        }
    }

    private void init(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;
        maxY_values = axisFormatter.getMaxY_Values(values);

        if(values.get(0).getLabels() == null)
            maxX_values = axisFormatter.getMaxX_Values(values);

        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;

    }

    private void DrawText() {
        paint.setColor(Color.BLACK);
        for (int i = 0; i < values.size(); i++) {

            canvas.drawText(values.get(i).getY_values() + "",
                    list_cordinate.get(i).getLeft() + border,
                    list_cordinate.get(i).getTop() - 10, paint);
        }
    }

    private  List<ChartData> StoredCordinate(Float graphheight){


        for(int i = 0;i<values.size(); i++){

            float x_ratio = 0;
            float ver_ratio = maxY_values/values.size();
            barheight = (graphheight / (maxY_values + (int) ver_ratio)) * values.get(i).getY_values();

            if(values.get(0).getLabels() != null){

                 left = (i * colwidth) + horstart;
                 right = ((i * colwidth) + horstart) + (colwidth - 1);
                 bottom = graphheight + border;
                 if(inverseAxis == true){

                    top = barheight + border;

                 } else {
                    top = (border - barheight) + graphheight;
                 }
            }
            else{

                 x_ratio = (maxX_values/(values.size()-1));
                 left = ((colwidth/x_ratio) *values.get(i).getX_values()) +border;
                 right = left+border+20;
                 bottom =  graphheight + border;
                 if(inverseAxis == true){

                     top = barheight + border;

                 } else {
                     top = (border - barheight) + graphheight;
                 }
            }

            list_cordinate.add(new ChartData(left, top, right, bottom));
        }

        return list_cordinate;
    }

    public void saveChart(com.numetriclabz.numandroidcharts.BarChart barChart){

        axisFormatter.saveChart(barChart.getBitmap(), height+border, width);
    }

    public Bitmap getBitmap()
    {

        this.setDrawingCacheEnabled(true);
        this.buildDrawingCache();
        Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
        this.setDrawingCacheEnabled(false);


        return bmp;
    }


}