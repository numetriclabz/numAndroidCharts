package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AxisRenderer {

    protected float border = 30;
    protected float y_axis_ratio, x_axis_ratio, max_YValue, max_XValue, horizontal_width,
                    width, height, graphheight, graphwidth;

    protected float horstart = border * 2;

    Paint paint;

    private String description;

    public List<Float> horizontal_width_list = new ArrayList<>();
    List<String> colorList = new ArrayList<>();

    List<ChartData> values;
    List<ChartData> largest_value_set;
    List<String> labels;

    Canvas canvas;
    Paint textPaint = new Paint();

    int size, label_size;

    private boolean stacked = false;

    private  int legendTop,legendLeft, legendRight, legendBottom;
    private  RectF legends;


    public void PlotXYLabels(float width, float height, List<String> labels,
                             Canvas canvas,List<Float> horizontal_width_list,
                             Paint paint, List<ChartData> values, String description, boolean stacked){


        this.width = width;
        this.height = height;
        this.labels = labels;
        this.canvas = canvas;
        this.horizontal_width_list = horizontal_width_list;
        this.paint = paint;
        this.values = values;
        this.description = description;
        this.stacked = stacked;

        if(!stacked)
            this.max_YValue = getMaxYAxisValue(values);
        else
            this.max_YValue = getStackYValue(values);

        this.max_XValue = getMaxXAxisValues(values);

        init();
    }


    /**
     * This method is used to initialize various parameters
     */
    private void init(){

        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);

        int largestSize = getLargestSize(values);
        largest_value_set = values.get(largestSize).getList();

        size = largest_value_set.size();

        label_size = size - 1;

        y_axis_ratio =  max_YValue/label_size;

        for (int i = 0; i < size; i++) {
            paint.setTextSize(18);
            create_y_axis(i);
        }

        if(labels != null)
            size = labels.size();


        if(largest_value_set.get(0).getLabels() != null){

            for(int j =0; j< size+1 ; j++)
                createX_axis(j);
        }
        else {

            label_size = size - 1;
            x_axis_ratio = max_XValue/label_size;

            for(int j =0; j< size ; j++)
                createX_axis(j);
        }
    }


    /**
     * Function to create the axis and its vertical breakdowns.
     * This function also initiate the method which is responsible to plot the labels
     * related to vertical breakdowns
     * @param i index of the breakdown
     */
    protected void createX_axis(int i){

        if(largest_value_set.get(0).getLabels() != null)
            horizontal_width = ((graphwidth / size) * i) + horstart;
        else
            horizontal_width = ((graphwidth / label_size) * i) + horstart;


        horizontal_width_list.add(horizontal_width);

        if(i == 0){
            canvas.drawLine(horizontal_width, graphheight + border, horizontal_width, border, paint);
        }
        else{
            canvas.drawLine(horizontal_width, graphheight + border, horizontal_width, graphheight + (2 * border), paint);
        }


        if(largest_value_set.get(0).getLabels() != null)
            DrawLabels(i);
        else
            DrawExplicitLabels(i);
    }


    /**
     * This function plots the breakdown labels for x axis given the labels are not explicitly
     * defined using setLabels() method
     * @param i index of the breakdown
     */
    protected void DrawLabels(int i){

        paint.setColor(Color.BLACK);

        if(i >1){

            float colwidth = horizontal_width_list.get(1) -  horizontal_width_list.get(0);
            canvas.drawText(values.get(i-1).getLabels(), horizontal_width - (colwidth + 5) , height - 38, paint);
        }
        else if(i !=0 && i==1){
            canvas.drawText(values.get(i-1).getLabels(), horizontal_width/2 , height - 38, paint);
        }
    }


    /**
     * This function plots the breakdown labels for x axis given the labels are explicitly
     * defined using setLabels() method
     * @param i index of the breakdown
     */
    protected void DrawExplicitLabels(int i){

        paint.setTextAlign(Paint.Align.RIGHT);

        if (i==0)
            paint.setTextAlign(Paint.Align.LEFT);

        String x_values = String.format("%.1f", i * x_axis_ratio);
        paint.setColor(Color.BLACK);


        if(labels != null)
            canvas.drawText(labels.get(i), horizontal_width - 10, height - 38, paint);
        else
            canvas.drawText(x_values, horizontal_width -10, height - 38, paint);
    }


    /**
     * This function is used to plot the y axis and its breakdown with value.
     * Uses drawLine() method to create the y axis and lines to show breakdown of y axis
     * and drawText() method to plot labels related with breakdowns
     * @param i index of the breakdown
     */
    protected void create_y_axis(int i){

        Log.e("index vlaue", i + "");

        float ver_height = ((graphheight / label_size) * i) + border;

        if(i == largest_value_set.size() - 1)
            canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line
        else
            canvas.drawLine(horstart, ver_height , border, ver_height, paint); // Draw vertical line


        paint.setColor(Color.BLACK);
        int Y_labels =  (int) size - 1- i;

        String y_labels = String.format("%.1f", Y_labels * y_axis_ratio);
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(y_labels, horstart -15, ver_height -10, paint);
        paint.setTextAlign(Paint.Align.LEFT);
    }


    /**
     * This function returns the index of set having max number of the values from
     * the provided dataset
     * @param values dataset provided by the user as List
     * @return index of set (Type int)
     */
    protected int getLargestSize(List<ChartData> values){

        int largest = Integer.MIN_VALUE;
        int setSize = 0;

        for (int j = 0; j < values.size(); j++) {

            if (values.get(j).getList().size() > setSize) {
                setSize = values.get(j).getList().size();
                largest = j;
            }
        }
        return largest;
    }


    /**
     *This function calculates the largest value for y axis from the given data set.
     * @param values data set provided by the user as List
     * @return largest value from data set (Type float)
     */
    protected float getMaxYAxisValue(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;

        for (int j =0; j < values.size();j++) {

            for(int i = 0; i< values.get(j).getList().size(); i++){

                if (values.get(j).getList().get(i).getY_values() > largest)
                    largest = values.get(j).getList().get(i).getY_values();
            }

        }
        return largest;
    }


    /**
     *This function retrieves the largest value for y axis given the chart type is stacked.
     * Largest value is calculated by adding the max value from each set of the provided dataset.
     * @param values data set provided by the user as List
     * @return largest value from data set (Type float)
     */
    public float getStackYValue(List<ChartData> values) {

        float largest1 = 0;

        for (int i = 0; i < values.size(); i++) {

            float largest = Integer.MIN_VALUE;

            for (int j = 0; j < values.get(i).getList().size(); j++){

                if (values.get(i).getList().get(j).getY_values() > largest)
                    largest = values.get(i).getList().get(j).getY_values();
            }
            largest1 +=largest;
        }
        return largest1;
    }


    /**
     *This function calculates the largest value for x axis from data set.
     * @param values data set provided by the user as List
     * @return largest value from data set (Type float)
     */
    protected float getMaxXAxisValues(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int j =0; j < values.size();j++) {

            for(int i =0; i< values.get(j).getList().size(); i++){

                if (values.get(j).getList().get(i).getX_values() > largest)
                    largest = values.get(j).getList().get(i).getX_values();
            }
        }
        return largest;
    }


    /**
     * Initializes the process to create the legends
     * @param legends_list List having legend values
     * @param color_code_list List list having color codes for each legend
     */
    public void setLegendPoint(List<String> legends_list, List<Integer>color_code_list){

        legendTop = (int) height - 10;
        legendLeft = (int) (width * 0.1);
        legendRight = (int) graphwidth;
        legendBottom = (int) height;

        legends = new RectF(legendLeft, legendTop, legendRight, legendBottom);
        Legends(legends_list, color_code_list);

    }


    /**
     * This function calculates the position for each legend
     * @param legends_list List having legend values
     * @param color_code_list List list having color codes for each legend
     */
    private void Legends(List<String> legends_list, List<Integer>color_code_list){

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20f);

        int left = (int) (graphwidth * 0.1);
        for (int i = 0; i < legends_list.size(); i++){

            String label = legends_list.get(i);

            float text_width = textPaint.measureText(label, 0, label.length());

            int color = color_code_list.get(i);


            if (!((graphwidth - legendLeft) > (text_width + 60))) {

                legendTop -= 60;
                legendLeft = left;
            }

            addLegends(canvas, color, legendTop, legendLeft, legendRight, legendBottom, label);
            legendLeft += ((int)text_width + 60);
        }
    }


    /**
     * Function to draw legends having rectangle depicting set's color and set label
     * @param canvas Canvas object
     * @param color Integer color value
     * @param top Integer top most point of rectangle
     * @param left Integer left most point of rectangle
     * @param right Integer right most point of rectangle
     * @param bottom Integer bottom point of rectangle
     * @param label String label for the specific data set
     */
    private void addLegends(Canvas canvas, int color, int top, int left, int right, int bottom, String label){

        legends = new RectF(left, top, right, bottom);

        Rect r = new Rect(left, top, left + 30, top + 30);
        paint.setColor(Color.parseColor(getColorList().get(color)));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(r, paint);
        canvas.drawText(label, left + 40, top + 20, textPaint);
    }


    /**
     * This function adds color values to the list of colors
     * @return colorlist List having values for various colors
     */
    public List<String> getColorList(){

        colorList.add("#FF6600");
        colorList.add("#DC143C");
        colorList.add("#40E0D0");
        colorList.add("#A52A2A");
        colorList.add("#9932CC");
        colorList.add("#228B22");
        colorList.add("#FF0000");
        colorList.add("#DAA520");
        colorList.add("#FFA500");
        colorList.add("#A0522D");
        return colorList;
    }
}
