package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class HorizontalAxisFormatter {


    public List<Float> vertical_height_list = new ArrayList<>();
    float ver_ratio, hor_ratio,  border = 30, horstart = border * 2, graphheight, width, horizontal_width, graphwidth, height;
    float colwidth, ver_height, maxY_values, maxX_values;
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
                             List<Float> vertical_height_list,
                             Paint paint, List<ChartData> values,
                             float maxX_values, String description){

        this.graphheight = graphheight;
        this.width = width;
        this.graphwidth = graphwidth;
        this.height = height;
        this.hori_labels = hori_labels;
        this.canvas = canvas;
        this.horstart = horstart;
        this.border = border;
        this.vertical_height_list = vertical_height_list;
        this.paint = paint;
        this.values = values;
        this.description = description;
        this.maxX_values= maxX_values;
        this.maxY_values = maxY_values;

        init();

    }

    protected void init(){

        paint.setTextAlign(Paint.Align.LEFT);
        size = values.size();

        if(hori_labels != null) {
            size = hori_labels.size();
        }

        label_size = size - 1;
        hor_ratio = maxY_values/label_size;
        paint.setColor(Color.BLACK);

        for (int i = 0; i < size; i++) {
            paint.setTextSize(18);
            createX_axis(i);

        }

        if(hori_labels != null) {
            size = hori_labels.size();

        }

        if(values.get(0).getLabels() != null){

            for(int j =0; j< size ; j++){
                createY_axis(j);
            }

            ver_height = ((graphheight / size) * size) + border;
            canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line
        } else {

            label_size = size - 1;
            ver_ratio =  maxX_values/label_size;  // Vertical label ratio

            for(int j =0; j< size ; j++){
                createY_axis(j);
            }
        }

        if(description !=null){
            Description();
        }

        paint.setTextSize(18);
    }

    protected void createY_axis(int i){
        if(values.get(0).getLabels() != null){

            ver_height = ((graphheight / size) * i) + border;

        } else {

            ver_height = ((graphheight / label_size) * i) + border;

        }

        vertical_height_list.add(ver_height);

        if(i == values.size()-1 && values.get(0).getLabels() == null){

            canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line

        } else if(values.get(0).getLabels() != null && i == values.size()) {

            canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line
        }
        else {

            canvas.drawLine(horstart, ver_height , border, ver_height, paint); // Draw vertical line
        }

        paint.setColor(Color.BLACK);


        if(values.get(0).getLabels() != null){

            DrawLabelsString(i,ver_height);

        } else {

            int Y_labels =  (int) size - 1- i;
            String y_labels = String.format("%.1f", Y_labels * ver_ratio);
            canvas.drawText(y_labels, horstart-10, ver_height - 10, paint);
        }

    }

    protected void createX_axis(int i){

        horizontal_width = ((graphwidth / label_size) * i) + horstart;
        if(i==0){
            canvas.drawLine(horizontal_width, graphheight +border, horizontal_width, border, paint);

        } else{
            canvas.drawLine(horizontal_width,graphheight + border, horizontal_width, graphheight + 2 * border, paint);
        }
        //canvas.drawLine(horstart, graphheight + border, horstart, border, paint);

        DrawHorizotalLabels(i);
    }

    protected void DrawLabelsString(int i, float ver_height){
        paint.setColor(Color.BLACK);

        paint.setTextAlign(Paint.Align.RIGHT);

        if(i >0){

            float ver_height1 = vertical_height_list.get(1) -  vertical_height_list.get(0);
            canvas.drawText(values.get(label_size-i).getLabels(), horstart - 10, ver_height + ver_height1/1.5f, paint);

        } else if(i==0){

            canvas.drawText(values.get(label_size-i).getLabels(), horstart - 10, ver_height + ver_height*2 , paint);
        }

    }

    protected void DrawHorizotalLabels(int i){

        paint.setTextAlign(Paint.Align.RIGHT);

        if (i==0)
            paint.setTextAlign(Paint.Align.LEFT);


        String x_values = String.format("%.1f", i * hor_ratio);
        paint.setColor(Color.BLACK);

        if(hori_labels != null){

            canvas.drawText(hori_labels.get(i), horizontal_width-10, height - 38, paint);

        }else {

            canvas.drawText(x_values, horizontal_width -10, height - 38, paint);
        }
    }

    protected void Description(){

        paint.setTextSize(28);
        float text_width = paint.measureText(description, 0, description.length());
        this.canvas.drawText(description, graphwidth - text_width, height + 50, paint);
    }


}
