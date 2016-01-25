package com.numetriclabz.numandroidcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class WaterFallChart extends View {

    private Paint paint;
    private List<ChartData> values;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private String description;
    private float border = 30, horstart = border * 2;
    private int parentHeight ,parentWidth;
    private Canvas canvas;
    private List<ChartData> list_cordinate = new ArrayList<>();
    private float height ,width, maxY_values, maxX_values, graphheight, graphwidth;
    private float left, right, top, bottom, barheight, colwidth;
    private  AxisFormatter axisFormatter = new AxisFormatter();
    private  float current_values = 0f, prebarheight = 0f, last_value;

    public WaterFallChart(Context context, AttributeSet attrs){
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
        axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, null, maxY_values, canvas,
                horizontal_width_list, paint, values, maxX_values, description, false, false);

        if (values != null) {

            colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);
            list_cordinate = StoredCordinate(graphheight);
            DrawText();
        }
    }

    private void intilaizeValue(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;
        AxisFormatter axisFormatter = new AxisFormatter();
        maxY_values = getMaxY_Values(values);
        if(values.get(0).getLabels() == null)
            maxX_values = axisFormatter.getMaxX_Values(values);

        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;

    }

    private void DrawText() {
        paint.setColor(Color.BLACK);
        for (int i = 0; i < values.size(); i++) {
            if(i== values.size()-1){

                canvas.drawText(current_values+"",
                        list_cordinate.get(i).getLeft() + border,
                        list_cordinate.get(i).getTop() - 10, paint);
            }
            else {

                canvas.drawText(values.get(i).getY_values()+"",
                        list_cordinate.get(i).getLeft() + border,
                        list_cordinate.get(i).getTop() - 10, paint);

            }
        }
    }

    private  List<ChartData> StoredCordinate(Float graphheight){

        for(int i = 0;i<values.size()-1; i++){

            barheight = (graphheight/maxY_values)*values.get(i).getY_values() ;

            if(values.get(0).getLabels() != null){

                left = (i * colwidth) + horstart;
                right = ((i * colwidth) + horstart) + (colwidth - 1);

                if(barheight <0){

                    paint.setColor(Color.parseColor(axisFormatter.getColorList().get(1)));
                    Float barheight1 = Math.abs(barheight);
                    bottom = top + border + barheight1;

                } else {
                    positiveBarHeight(i);
                }

            }

            prebarheight = barheight;
            last_value = values.get(i).getY_values();
            list_cordinate.add(new ChartData(left, top, right, bottom));
            createBar();

        }
        calculateLastHeight();
        createBar();
        return list_cordinate;
    }

    private void createBar(){

        canvas.drawRect(left, top,
                right, bottom, paint);
    }

    private void calculateLastHeight(){
        if(last_value >0){


            bottom =  graphheight + border;

        } else {

            top = bottom;
            bottom =  graphheight + border;
        }
        left = ((values.size() - 1) * colwidth) + horstart;
        right = (((values.size()-1) * colwidth) + horstart) + (colwidth - 1);
        paint.setColor(Color.parseColor(axisFormatter.getColorList().get(2)));
        list_cordinate.add(new ChartData(left, top, right, bottom));
    }

    private void positiveBarHeight(int i){

        paint.setColor(Color.parseColor(axisFormatter.getColorList().get(0)));
        if(i ==0){
            top = border + graphheight -  barheight;
            bottom = graphheight + border;

        }
        else{

            if(values.get(i-1).getY_values() >0){
                bottom = top;
                top = top - barheight;


            } else {

                if(bottom > barheight){
                    top = bottom- barheight -border;

                } else {
                    top = graphheight -(top+barheight+bottom) +3*border ;

                }

            }

        }
    }

    public float getMaxY_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;

        for (int i = 0; i < values.size()-1; i++) {

            current_values += values.get(i).getY_values();
            if(current_values > largest){
                largest = current_values;
            }
        }
        return largest;
    }

}
