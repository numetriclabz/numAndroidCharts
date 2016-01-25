package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class CombinedChart extends View {
    private Paint paint;
    private List<ChartData> values;
    private List<String> hori_labels;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private float horizontal_width,  border = 30, horstart = border * 2,  circleSize = 5f, colwidth;
    private int parentHeight ,parentWidth, color_no =0;
    private Canvas canvas;
    private List<ChartData> list_cordinate = new ArrayList<>();
    private float x_cordinate, y_cordinate, height ,width, maxY_values, maxX_values, graphheight, graphwidth;
    private  AxisFormatter axisFormatter = new AxisFormatter();
    private List<Integer> color_code_list = new ArrayList<>();
    private  List<String> legends_list;
    private boolean showPoints = false;
    private Renderer renderer =  new Renderer();

    public CombinedChart(Context context, AttributeSet attrs){
        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, com.numetriclabz.numandroidcharts.R.styleable.AreaChart, 0 ,0);

        try {
            showPoints = array.getBoolean(com.numetriclabz.numandroidcharts.R.styleable.AreaChart_show_datapoints, false);
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

    protected void onDraw(Canvas canvas) {
        if (values != null) {

            intilaizeValue(canvas);
            int largestSize = axisFormatter.getLargestSize(values);

            axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                    horizontal_width_list, paint,  values.get(largestSize).getList(), maxX_values, null, false, false);

            colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

            DrawDataPoint();

            if (legends_list != null)
                axisFormatter.setLegegendPoint(legends_list, color_code_list);
        }
    }

    private void DrawDataPoint(){

        for(int i =0; i <values.size(); i++){

                if(ChartData.LineChart.equals(values.get(i).getChartName())){
                    list_cordinate = new ArrayList<>();
                    list_cordinate = StoredCordinate(i);
                    DrawLine();


                } else if(ChartData.BarChart.equals(values.get(i).getChartName())){

                    list_cordinate = new ArrayList<>();
                    list_cordinate =StoredCordinate_rectangle(i);
                    DrawRectangle();
                    paint.setAlpha(1000);
                    DrawBarText(i);

                } else if(ChartData.AreaChart.equals(values.get(i).getChartName())){

                    list_cordinate = new ArrayList<>();
                    list_cordinate = StoredCordinate(i);
                    DrawArea();
                } else if(ChartData.SplineChart.equals(values.get(i).getChartName())){

                    list_cordinate = new ArrayList<>();
                    list_cordinate = StoredCordinate(i);
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(2);
                    renderer.DrawCubicPath(canvas, list_cordinate, paint, graphheight, false);
                    color_code_list.add(10);
                   // renderer.DrawCircle(canvas, list_cordinate, paint, circleSize);
                }
            }
    }

    private void DrawArea(){

        paint.setAntiAlias(true);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

            Path path= new Path();
            path.reset();

            path.moveTo(
                    list_cordinate.get(0).getX_values(),
                    graphheight + 30);


            int listSize = list_cordinate.size();

            for(int j = 0; j < listSize; j++){
                if(color_no > axisFormatter.getColorList().size())
                    color_no = 0;

                paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));
                paint.setAlpha(100);


                path.lineTo(
                        list_cordinate.get(j).getX_values(),
                        list_cordinate.get(j).getY_values());
            }


        path.lineTo(
                list_cordinate.get(listSize - 1).getX_values(),
                    graphheight + 30);

            color_code_list.add(color_no);

            color_no += 1;
            canvas.drawPath(path, paint);
    }

    private void DrawRectangle(){

        for(int i=0;i<list_cordinate.size();i++){
            if(color_no > axisFormatter.getColorList().size())
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));
            paint.setAlpha(180);
            canvas.drawRect(list_cordinate.get(i).getLeft(), list_cordinate.get(i).getTop(),
                    list_cordinate.get(i).getRight(), list_cordinate.get(i).getBottom(), paint);
        }
        color_code_list.add(color_no);

        color_no += 1;

    }


    private  List<ChartData> StoredCordinate(int j){

        list_cordinate = new ArrayList<>();

        for(int i =0; i< values.get(j).getList().size(); i++){

            float x_ratio = (maxX_values / (axisFormatter.getSmallestSize(values) - 1));

            x_cordinate = (colwidth / x_ratio) * values.get(j).getList().get(i).getX_values();
            float line_height = (graphheight / maxY_values) * values.get(j).getList().get(i).getY_values();
            y_cordinate = (border - line_height) + graphheight;
            list_cordinate.add(new ChartData(y_cordinate, x_cordinate + horstart,
                   values.get(j).getList().get(i).getY_values() + ""));
        }

        return list_cordinate;
    }

    private  List<ChartData> StoredCordinate_rectangle(int i){

        list_cordinate = new ArrayList<>();

        for(int j =0; j< values.get(i).getList().size();j++) {

            float x_ratio = (maxX_values / (axisFormatter.getSmallestSize(values) - 1));
            float barheight = (graphheight / maxY_values) * values.get(i).getList().get(j).getY_values();
            float left = ((colwidth / x_ratio) * values.get(i).getList().get(j).getX_values()) + border;
            float top = (border - barheight) + graphheight;
            float right = ((colwidth / x_ratio) * values.get(i).getList().get(j).getX_values()) + horstart;
            float bottom = graphheight + border;

            list_cordinate.add(new ChartData(left, top, right, bottom));
        }
        return  list_cordinate;
    }


    private void DrawLine(){

        paint.setAntiAlias(true);
//        paint.setDither(true);
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeJoin(Paint.Join.ROUND);
//        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(3);

        for(int i =0; i < list_cordinate.size()-1;i++){
            if(color_no > axisFormatter.getColorList().size())
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));

            canvas.drawLine(list_cordinate.get(i).getX_values(),
                    list_cordinate.get(i).getY_values(),
                    list_cordinate.get(i + 1).getX_values(),
                    list_cordinate.get(i + 1).getY_values(), paint);


        }
        DrawCircle();
        DrawLineText();
        color_code_list.add(color_no);
        color_no += 1;
    }

    private void DrawLineText(){
        paint.setStrokeWidth(0);
        for(int i =0; i < list_cordinate.size();i++) {
            canvas.drawText(list_cordinate.get(i).getCordinate(),
                    list_cordinate.get(i).getX_values() - border,
                    list_cordinate.get(i).getY_values(), paint);
        }
    }

    private void DrawCircle(){

        for(int i=0; i< list_cordinate.size(); i++) {

            canvas.drawCircle(list_cordinate.get(i).getX_values(), list_cordinate.get(i).getY_values(), circleSize, paint);
        }
    }

    private void DrawBarText(int j) {
          paint.setStrokeWidth(0);
            for(int i =0; i< list_cordinate.size(); i++) {

                if((list_cordinate.get(i).getTop() -border) > 0) {

                    canvas.drawText(Float.toString(values.get(j).getList().get(i).getY_values()),
                            list_cordinate.get(i).getLeft() +border,
                            list_cordinate.get(i).getTop()-border, paint);
                } else {

                    canvas.drawText(Float.toString(values.get(j).getList().get(i).getY_values()),
                            list_cordinate.get(i).getLeft() +border -40,
                            list_cordinate.get(i).getTop()+border, paint);
                }
            }

            color_no +=1;
    }

    // Get the Width and Height defined in the activity xml file
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
