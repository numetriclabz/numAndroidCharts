package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AxisFormatter {

    public List<Float> horizontal_width_list = new ArrayList<>();
    float ver_ratio, hor_ratio, horstart, graphheight, width,border, horizontal_width, graphwidth, height;
    int label_size, size;
    Canvas canvas;
    Paint paint, textPaint;
    List<ChartData> values;
    List<String> hori_labels;
    String description;
    private List<String> colorList = new ArrayList<>();
    private  int legendTop,legendLeft, legendRight, legendBottom;
    private  RectF legends;

    // Plot XY Lables
    public void PlotXYLabels(float graphheight,float width,
                             float graphwidth, float height,
                             List<String> hori_labels, float maxY_values, Canvas canvas,
                             float horstart, float border,  List<Float> horizontal_width_list,
                             float horizontal_width, Paint paint, List<ChartData> values,
                             float maxX_values, String description){

        initializeValues(graphheight, width, graphwidth, height, hori_labels, canvas, horstart, border,
                horizontal_width_list, horizontal_width, paint, values, description);

        paint.setTextAlign(Paint.Align.LEFT);
        size = values.size();

        if(hori_labels != null) {
            size = hori_labels.size();
        }

        label_size = size - 1;
        ver_ratio =  maxY_values/label_size;  // Vertical label ratio
        paint.setColor(Color.BLACK);

        for (int i = 0; i < size; i++) {
            paint.setTextSize(18);
            createY_axis(i);
        }

        if(hori_labels != null) {
            size = hori_labels.size();
        }

        label_size = size - 1;
        hor_ratio = maxX_values/label_size;

        for(int j =0; j< size ; j++){

            createX_axis(j);
        }

        if(description !=null){
            Description();
        }

        paint.setTextSize(18);

    }

    protected void createY_axis(int i){

        float ver_height = ((graphheight / label_size) * i) + border;
        canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line
        paint.setColor(Color.BLACK);
        int Y_labels =  (int) size - 1- i;

        String y_labels = String.format("%.1f", Y_labels*ver_ratio);
        canvas.drawText(y_labels, border, ver_height, paint);
    }

    protected void createX_axis(int i){

        horizontal_width = ((graphwidth / label_size) * i) + horstart;
        horizontal_width_list.add(horizontal_width);
        canvas.drawLine(horizontal_width, graphheight +border, horizontal_width, border, paint);
        paint.setTextAlign(Paint.Align.CENTER);

        if (i== size -1)
            paint.setTextAlign(Paint.Align.RIGHT);

        if (i==0)
            paint.setTextAlign(Paint.Align.LEFT);

        DrawHorizotalLabels(i);
    }

    protected void DrawHorizotalLabels(int i){

        String x_values = String.format("%.1f", i * hor_ratio);
        paint.setColor(Color.BLACK);

        if(hori_labels != null){

            canvas.drawText(hori_labels.get(i), horizontal_width, height - 38, paint);

        }else {
            canvas.drawText(x_values, horizontal_width, height - 38, paint);
        }
    }

    protected void Description(){

        paint.setTextSize(28);
        this.canvas.drawText(description, graphwidth - 30, height + 50, paint);
    }

    protected void initializeValues(float graphheight,float width,
                                 float graphwidth, float height,
                                 List<String> hori_labels, Canvas canvas,
                                 float horstart, float border,  List<Float> horizontal_width_list,
                                 float horizontal_width, Paint paint, List<ChartData> values,String description){

        this.graphheight = graphheight;
        this.width = width;
        this.graphwidth = graphwidth;
        this.height = height;
        this.hori_labels = hori_labels;
        this.canvas = canvas;
        this.horstart = horstart;
        this.border = border;
        this.horizontal_width_list = horizontal_width_list;
        this.horizontal_width = horizontal_width;
        this.paint = paint;
        this.values = values;
        this.description = description;
    }

    //  return the maximum y_value
    public float getMaxY_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getY_values() > largest)
                largest = values.get(i).getY_values();
        return largest;
    }
    //  return the maximum y_value
    public float getMaxX_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getX_values() > largest)
                largest = values.get(i).getX_values();
        return largest;
    }
    // return the minimum value
    public float getMinValues(List<ChartData> values) {

        float smallest = Integer.MAX_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getY_values() < smallest)
                smallest = values.get(i).getX_values();
        return smallest;
    }

    public List<String> getColorList(){

        colorList.add("#FF6600");
        colorList.add("#FF0000");
        colorList.add("#D2691E");
        colorList.add("#DC143C");
        colorList.add("#A52A2A");
       return colorList;
    }

    protected int getLargestSize(List<ChartData> values){
        int largest = Integer.MIN_VALUE;
        for (int j =0; j < values.size();j++) {
            if (values.get(j).getList().get(j).getY_values() > largest){
                largest = j;
            }
        }
        return largest;
    }

    protected int getSmallestSize(List<ChartData> values){
        int smallest = Integer.MAX_VALUE;
        for (int j =0; j < values.size();j++) {
            if (values.get(j).getList().size() < smallest){
                smallest = values.get(j).getList().size();
            }
        }
        return smallest;
    }

    //  return the maximum y_value
    protected float getMultiMaxX_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int j =0; j < values.size();j++) {

            for(int i =0; i< values.get(j).getList().size(); i++){

                if (values.get(j).getList().get(i).getX_values() > largest)
                    largest = values.get(j).getList().get(i).getX_values();
            }

        }
        return largest;
    }

    //  return the maximum y_value
    protected float getMultiMaxY_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int j =0; j < values.size();j++) {

            for(int i =0; i< values.get(j).getList().size(); i++){

                if (values.get(j).getList().get(i).getY_values() > largest)
                    largest = values.get(j).getList().get(i).getY_values();
            }

        }
        return largest;
    }

    public void setLegegendPoint(List<String> legends_list, List<Integer>color_code_list){

        legendTop = (int) height - 10;
        legendLeft = (int) (width * 0.1);
        legendRight = (int) graphwidth;
        legendBottom = (int) height;

        legends = new RectF(legendLeft, legendTop, legendRight, legendBottom);
        Legends(legends_list, color_code_list);

    }

    private void Legends(List<String> legends_list, List<Integer>color_code_list){
        textPaint = new Paint();
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

    private void addLegends(Canvas canvas, int color, int top, int left, int right, int bottom, String label){

        legends = new RectF(left, top, right, bottom);

        Rect r = new Rect(left, top, left + 30, top + 30);
        paint.setColor(Color.parseColor(getColorList().get(color)));
        canvas.drawRect(r, paint);
        canvas.drawText(label, left + 40, top + 20, textPaint);
    }



}
