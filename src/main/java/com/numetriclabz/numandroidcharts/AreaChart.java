package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class AreaChart extends View {

    private Paint paint;
    private List<ChartData>  values;
    private List<String> hori_labels;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private String description;
    private float horizontal_width,  border = 30, horstart = border * 2,  circleSize = 8f;
    private int parentHeight ,parentWidth, color_no =0;
    private static final int INVALID_POINTER_ID = -1;
    private float mPosX;
    private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private Boolean gesture = false;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private Canvas canvas;
    private List<ChartData> list_cordinate = new ArrayList<>();
    private List<ChartData> line_cordinate_list = new ArrayList<>();
    private float x_cordinate, y_cordinate, height ,width, maxY_values, maxX_values, graphheight, graphwidth;
    private  AxisFormatter axisFormatter = new AxisFormatter();
    private List<Integer> color_code_list = new ArrayList<>();
    private  List<String> legends_list;

    private boolean showPoints = false;

    public AreaChart(Context context, AttributeSet attrs){

        super(context, attrs);

        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AreaChart, 0 ,0);

        try {
            showPoints = array.getBoolean(R.styleable.AreaChart_show_datapoints, false);
        }
        finally {
            array.recycle();
        }

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

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

    public void setGesture(Boolean gesture){
        this.gesture = gesture;
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
                DrawCircle();
            }

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
        paint.setStyle(Paint.Style.FILL_AND_STROKE);

        for(int i =0; i < line_cordinate_list.size();i++){

            if(color_no < i)
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));
            paint.setAlpha(100);
            color_code_list.add(color_no);

            Path path= new Path();
            path.reset();

            path.moveTo(
                    line_cordinate_list.get(i).getList().get(0).getX_values(),
                    graphheight + 30);


            int listSize = line_cordinate_list.get(i).getList().size();

            for(int j = 0; j < listSize; j++){

                path.lineTo(
                        line_cordinate_list.get(i).getList().get(j).getX_values(),
                        line_cordinate_list.get(i).getList().get(j).getY_values());
            }


            path.lineTo(
                    line_cordinate_list.get(i).getList().get(listSize-1).getX_values(),
                    graphheight + 30);


            color_no += 1;
            canvas.drawPath(path, paint);
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

                float x_ratio = (maxX_values / (axisFormatter.getSmallestSize(values) - 1));

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


    private void CanvasScaleFator(){

        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);
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

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(.1f, Math.min(mScaleFactor, 10.0f));

            invalidate();
            return true;
        }
    }

}
