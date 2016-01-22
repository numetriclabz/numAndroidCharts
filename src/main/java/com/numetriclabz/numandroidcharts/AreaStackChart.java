package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ScaleGestureDetector;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AreaStackChart extends View {

    private Paint paint;
    private List<ChartData> values;
    private List<String> labels;
    private List<Float> horizontal_width_list = new ArrayList<>();
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
    List<ChartData> line_cordinate_list = new ArrayList<>();
    private float x_cordinate, y_cordinate, height ,width, maxY_values, maxX_values, graphheight, graphwidth;
    private AxisRenderer axisRenderer = new AxisRenderer();
    private List<Integer> color_code_list = new ArrayList<>();
    private  List<String> legends_list;
    JSONObject jsonObject;

    private boolean stacked = false;

    private  AxisFormatter axisFormatter = new AxisFormatter();

    public AreaStackChart(Context context, AttributeSet attrs){

        super(context, attrs);

        Paint paint = new Paint();
        this.paint = paint;
    }

    public void setData(List<ChartData> values){

        if(values != null)
            this.values = values;
    }

    public void setLegends(List<String> legends){

        if (legends != null)
            this.legends_list = legends;
    }


    public void setLabels(List<String> hori_labels){

        if (hori_labels != null)
            this.labels = hori_labels;
    }


    public void setCircleSize(Float circleSize){
        this.circleSize = circleSize;
    }

    public void setStackedArea(boolean stacked){
        this.stacked = stacked;
    }


    // Get the Width and Height defined in the activity xml file
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }


    protected void onDraw(Canvas canvas){
        if(values != null) {

            intilaizeValue(canvas);

            axisRenderer.PlotXYLabels(width, height, labels, canvas, horizontal_width_list,
                    paint, values, null, stacked);

            getBarheight();
            line_cordinate_list = StoredCordinate(graphheight);

            DrawLine();
            if(legends_list != null)
                axisRenderer.setLegendPoint(legends_list, color_code_list);
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

    protected List<ChartData> StoredCordinate(Float graphheight){
         float colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

            for (int j = 0; j < values.size(); j++) {

                list_cordinate = new ArrayList<>();

                for (int i = 0; i < values.get(j).getList().size(); i++) {

                    float x_ratio = (maxX_values / (axisFormatter.getSmallestSize(values) - 1));

                    x_cordinate = (colwidth / x_ratio) * values.get(j).getList().get(i).getX_values();

                    String str = jsonObject.optString(i + "");
                    str = str.replace("[","").replace("]", "");

                    List<String> items = Arrays.asList(str.split(","));
                    float line_height = (graphheight / maxY_values) * Float.parseFloat(items.get(j));

                    y_cordinate = (border - line_height) + graphheight;
                    list_cordinate.add(new ChartData(y_cordinate, x_cordinate + horstart,
                            values.get(j).getList().get(i).getY_values() + ""));
                }

                line_cordinate_list.add(new ChartData(list_cordinate));
            }

            return line_cordinate_list;
    }

    private void intilaizeValue(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;
        maxY_values = axisRenderer.getStackYValue(values);
        maxX_values = axisRenderer.getMaxXAxisValues(values);

        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;
    }

    private void getBarheight(){

        try {

            int size = values.get(0).getList().size();
            jsonObject = new JSONObject();

            for (int j = 0; j < size; j++) {

                List<Float> barheight_list1 = new ArrayList<>();

                barheight_list1 = stacked_height(barheight_list1, j);

                jsonObject.put(j + "", barheight_list1.toString());

            }
            Log.e("json", jsonObject.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<Float> stacked_height(List<Float> barHeightList, int num){

        for (int i = 0; i < values.size(); i++) {

            float barheight1 = 0 ;

            if(i != 0)
                barheight1 = barHeightList.get(i - 1) + values.get(i).getList().get(num).getY_values();
            else
                barheight1 = values.get(i).getList().get(num).getY_values();

            barHeightList.add(barheight1);
        }
        return barHeightList;
    }

}