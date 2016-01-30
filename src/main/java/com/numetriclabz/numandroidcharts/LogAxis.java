package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class LogAxis {

    protected float border = 30;
    protected float y_axis_ratio,
                    x_axis_ratio,
                    max_YValue,
                    max_XValue,
                    horizontal_width,
                    width,
                    height,
                    graphheight,
                    graphwidth;

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

    protected int base;

    public LogAxis(float width, float height, List<String> labels,
                   Canvas canvas, List<Float> horizontal_width_list,
                   Paint paint, List<ChartData> values, String description,
                   int base){


        this.width = width;
        this.height = height;
        this.labels = labels;
        this.canvas = canvas;
        this.horizontal_width_list = horizontal_width_list;
        this.paint = paint;
        this.values = values;
        this.description = description;
        this.base = base;

        this.max_XValue = getMaxXAxisValues(values);

        init();
    }


    /**
     * This method is used to initialize various parameters
     */
    private void init(){

        graphheight = Math.min(height, width) - (3 * border);
        graphwidth = Math.min(height, width) - (3 * border);

        int largestSize = getLargestSize(values);
        largest_value_set = values.get(largestSize).getList();

        size = largest_value_set.size();

        label_size = size - 1;
        calculateLogAxis();

        if(labels != null)
            size = labels.size();


        if(largest_value_set.get(0).getLabels() != null){

            for(int j = 0; j< size+1 ; j++)
                createX_axis(j);
        }
        else {

            for(int j = 0; j < size + 1 ; j++)
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

        Log.e("graph height", graphheight+"");

        horizontal_width = ((graphwidth / size) * i) + horstart;
        horizontal_width_list.add(horizontal_width);

        if(i == 0){
            canvas.drawLine(horizontal_width, graphheight + border, horizontal_width, border, paint);
        }
        else{
            canvas.drawLine(horizontal_width, graphheight + border, horizontal_width, graphheight + (2 * border), paint);
        }

        DrawLabels(i);
    }


    /**
     * This function plots the breakdown labels for x axis given the labels are not explicitly
     * defined using setLabels() method
     * @param i index of the breakdown
     */
    protected void DrawLabels(int i){

        paint.setColor(Color.BLACK);

        if(i > 1){

            float colwidth = horizontal_width_list.get(1) -  horizontal_width_list.get(0) - 10;
            canvas.drawText(largest_value_set.get(i-1).getXValue()+"", horizontal_width - (colwidth) , Math.min(height, width) - 38, paint);
        }
        else if(i != 0 && i == 1){
            canvas.drawText(largest_value_set.get(i-1).getXValue()+"", horizontal_width/2 , Math.min(height, width) - 38, paint);
        }
    }


    /**
     * This function is used to plot the y axis and its breakdown with value.
     * Uses drawLine() method to create the y axis and lines to show breakdown of y axis
     * and drawText() method to plot labels related with breakdowns
     * @param i index of the breakdown
     */
    protected void create_y_axis(int i, String y, int size){



        float ver_height = ((graphheight / size) * i) + border;

        Log.e("index vlaue", i + "   " + size+"   "+height+"  " +graphheight+"   "+ver_height);

        if(i == size )
            canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line
        else
            canvas.drawLine(horstart, ver_height , border, ver_height, paint); // Draw vertical line


        paint.setColor(Color.BLACK);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(y+"", horstart -15, ver_height -10, paint);
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
     *This function calculates the largest value for x axis from data set.
     * @param values data set provided by the user as List
     * @return largest value from data set (Type float)
     */
    protected float getMaxXAxisValues(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int j =0; j < values.size();j++) {

            for(int i =0; i< values.get(j).getList().size(); i++){

                if (values.get(j).getList().get(i).getXValue() > largest)
                    largest = values.get(j).getList().get(i).getXValue();
            }
        }
        return largest;
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


    /**
     * This method is used to create Logarithmic Y-axis according to the min and max
     * fetched from the data set provided by the user. It fetches the min and max value,
     * calculates the relative log power to the base value and initiates the process to
     * plot the Y-axis.
     */
    protected void calculateLogAxis(){

        int mLogMax = getMaxValue(values);
        int mLogMin = getMinValue(values);

        int size = mLogMax - mLogMin + 1;

        for (int index = 0; index < size; index++) {

            int num = (int) Math.pow(10, mLogMax);

            paint.setTextSize(18);
            create_y_axis(index, formatNum((double)num, 0), size - 1);
            mLogMax--;
        }
    }


    /**
     * Function to fetch the maximum value from the dataset provided by the user
     * @param lists dataset provided by the user as list
     * @return maxValue maximum value from the dataset
     */
    protected int getMaxValue(List<ChartData> lists){

        int val = 0;

        for(int listIndex = 0; listIndex < lists.size(); listIndex++){

            List<ChartData> list = lists.get(listIndex).getList();

            for(int index = 0; index < list.size(); index++){

                if(val < list.get(index).getYValue())
                    val = list.get(index).getYValue();
            }
        }
        return (int)Math.ceil(Math.log(val)/Math.log(base));
    }


    /**
     * Function to fetch the minimum value from the data set provided by the user
     * @param lists dataset provided by the user as list
     * @return minVal minimum value from the dataset
     */
    protected int getMinValue(List<ChartData> lists){

        int val = 0;

        for(int listIndex = 0; listIndex < lists.size(); listIndex++){

            List<ChartData> list = lists.get(listIndex).getList();

            for(int index = 0; index < list.size(); index++){

                if (index ==0)
                    val = list.get(index).getYValue();
                else if(val > list.get(index).getYValue())
                    val = list.get(index).getYValue();
            }
        }
        return (int)Math.floor(Math.log(val) / Math.log(base));
    }


    private static char[] c = new char[]{'K', 'M', 'B', 'T'};

    /**
     * Recursive implementation, invokes itself for each factor of a thousand, increasing the class on each invokation.
     * @param n the number to format
     * @param iteration in fact this is the class from the array c
     * @return a String representing the number n formatted in a cool looking way.
     */
    private static String formatNum(double n, int iteration) {

        double d = ((long) n / 100) / 10.0;
        boolean isRound = (d * 10) %10 == 0;//true if the decimal part is equal to 0 (then it's trimmed anyway)
        return (d < 1000? //this determines the class, i.e. 'k', 'm' etc
                ((d > 99.9 || isRound || (!isRound && d > 9.99)? //this decides whether to trim the decimals
                        (int) d * 10 / 10 : d + "" // (int) d * 10 / 10 drops the decimal
                ) + "" + c[iteration])
                : formatNum(d, iteration + 1));

    }
}
