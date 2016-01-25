package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class MultiSpline extends View {

    private Renderer renderer = new Renderer();

    private Paint paint;
    private List<ChartData>  values;
    private List<String> hori_labels;
    private List<Float> horizontal_width_list = new ArrayList<>();

    private float horizontal_width,  border = 30, horstart = border * 2,  circleSize = 8f;
    private int parentHeight ,parentWidth, color_no =0;
    private String description;
    private Boolean gesture = false;
    private float mScaleFactor = 1.f;
    private Canvas canvas;
    private List<ChartData> list_cordinate = new ArrayList<>();
    private List<ChartData> line_cordinate_list = new ArrayList<>();
    private float x_cordinate, y_cordinate, height ,width, maxY_values, maxX_values, graphheight, graphwidth;
    private  AxisFormatter axisFormatter = new AxisFormatter();
    private List<Integer> color_code_list = new ArrayList<>();
    private  List<String> legends_list;

    private boolean showPoints = false;
    private boolean area_spline = false;

    public MultiSpline(Context context, AttributeSet attrs){

        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AreaChart, 0 ,0);

        try {
            showPoints = array.getBoolean(R.styleable.AreaChart_show_datapoints, false);
        }
        finally {
            array.recycle();
        }

        Paint paint = new Paint();
        this.paint = paint;
    }

    public void setData(List<ChartData> values){

        if(values != null) {
            this.values = values;
        }
    }

    public void setLegends(List<String> legends){
        if (legends != null)
        this.legends_list = legends;
    }

    public void setLabels(List<String> hori_labels){

        if (hori_labels != null)
            this.hori_labels = hori_labels;
    }

    public void setCircleSize(Float circleSize){
        this.circleSize = circleSize;
    }

    // Get the Width and Height defined in the activity xml file
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas) {
        if(values != null) {

            intilaizeValue(canvas);

            int largestSize = axisFormatter.getLargestSize(values);

            axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                    horizontal_width_list, paint, values.get(largestSize).getList(), maxX_values, null, false, false);

            line_cordinate_list = StoredCordinate(graphheight);

            DrawLine();

            if(showPoints){
                DrawText();
            }

            DrawCircle();

            if(legends_list != null)
                axisFormatter.setLegegendPoint(legends_list, color_code_list);

            if (gesture == true) {
                canvas.restore();
            }
        }
    }

    private void DrawLine(){

        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);

        for(int i =0; i < line_cordinate_list.size();i++){

            if(color_no < i)
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));
            color_code_list.add(color_no);

            renderer.DrawCubicPath(canvas, line_cordinate_list.get(i).getList(), paint, graphheight, area_spline);

            color_no += 1;
        }
    }

    private void DrawText() {
        color_no=0;
        paint.setStrokeWidth(0);

        for(int i =0; i < line_cordinate_list.size();i++){
            if(color_no < i)
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));


            for(int j =0; j< line_cordinate_list.get(i).getList().size(); j++) {

                canvas.drawText(line_cordinate_list.get(i).getList().get(j).getCordinate(),
                        line_cordinate_list.get(i).getList().get(j).getX_values() - 30,
                        line_cordinate_list.get(i).getList().get(j).getY_values(), paint);

            }
            color_no += 1;
        }
    }

    private void DrawCircle(){
        color_no=0;

        for(int i = 0; i < line_cordinate_list.size();i++){
            if(color_no < i)
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));
            paint.setStyle(Paint.Style.FILL);

            for(int j =0; j< line_cordinate_list.get(i).getList().size(); j++) {

                canvas.drawCircle(line_cordinate_list.get(i).getList().get(j).getX_values(),
                        line_cordinate_list.get(i).getList().get(j).getY_values(), circleSize, paint);

            }
            color_no += 1;
        }
    }


    private  List<ChartData> StoredCordinate(Float graphheight){

        float colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

        for (int j =0; j < values.size();j++) {

            list_cordinate = new ArrayList<>();

            for(int i =0; i< values.get(j).getList().size(); i++){

                float x_ratio = (maxX_values / (axisFormatter.getSmallestSize(values)));

                x_cordinate = (colwidth / x_ratio) * values.get(j).getList().get(i).getX_values();
                float line_height = (graphheight / maxY_values) * values.get(j).getList().get(i).getY_values();
                y_cordinate = (border - line_height) + graphheight;
                list_cordinate.add(new ChartData(y_cordinate, x_cordinate + horstart,
                        "(" + values.get(j).getList().get(i).getX_values() + ", " + values.get(j).getList().get(i).getY_values() + ")"));
            }

            line_cordinate_list.add(new ChartData(list_cordinate));

        }

        return line_cordinate_list;
    }


    private void intilaizeValue(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;
        maxY_values = axisFormatter.getMultiMaxY_Values(values);
        maxX_values = axisFormatter.getMultiMaxX_Values(values);
       // min = axisFormatter.getMinValues(values);
        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;
    }


}