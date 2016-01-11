package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class AxisFormatter {

    public List<Float> horizontal_width_list = new ArrayList<>();
    float ver_ratio, hor_ratio, horstart, graphheight, width,border, horizontal_width, graphwidth, height;
    int label_size, size;
    Canvas canvas;
    Paint paint;
    List<ChartData> values;
    List<String> hori_labels;
    String description;

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

        if(hori_labels != null)
            size = hori_labels.size();

        label_size = size - 1;
        ver_ratio =  maxY_values/label_size;  // Vertical label ratio
        hor_ratio = maxX_values/label_size;

        for (int i = 0; i < size; i++) {

            paint.setColor(Color.BLACK);
            paint.setTextSize(18);
            createY_axis(i);
            createX_axis(i);

        }

        if(description !=null){
            Description();
        }

        paint.setTextSize(18);

    }

    public void createY_axis(int i){

        float ver_height = ((graphheight / label_size) * i) + border;
        canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line
        paint.setColor(Color.BLACK);
        int Y_labels =  (int) values.size()-1- i;

        String y_labels = String.format("%.1f", Y_labels*ver_ratio);
        canvas.drawText(y_labels, border, ver_height, paint);
    }

    public void createX_axis(int i){

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

    public void DrawHorizotalLabels(int i){

        String x_values = String.format("%.1f", i * hor_ratio);
        paint.setColor(Color.BLACK);

        if(hori_labels != null){

            canvas.drawText(hori_labels.get(i), horizontal_width, height - 2, paint);

        }else {
            canvas.drawText(x_values, horizontal_width, height - 2, paint);
        }
    }

    public void Description(){

        paint.setTextSize(28);
        this.canvas.drawText(description, graphwidth -30, height + 50, paint);
    }

    public void initializeValues(float graphheight,float width,
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

}
