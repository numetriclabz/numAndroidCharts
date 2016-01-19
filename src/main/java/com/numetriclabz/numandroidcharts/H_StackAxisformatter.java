package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class H_StackAxisformatter  {

    public List<Float> vertical_height_list = new ArrayList<>();
    float hor_ratio, horstart, graphheight, width,border, horizontal_width, graphwidth, height;
    float colwidth, ver_height;
    int label_size, size;
    Canvas canvas;
    Paint paint, textPaint;
    Float[] values;
    List<String> hori_labels;
    String description;
    private List<String> colorList = new ArrayList<>();
    private  int legendTop,legendLeft, legendRight, legendBottom;
    private  RectF legends;
    private boolean percentage_stacked = false;

    // Plot XY Lables
    public void PlotXYLabels(float graphheight,float width,
                             float graphwidth, float height,
                             List<String> hori_labels, float maxX_values, Canvas canvas,
                             float horstart, float border,  List<Float> vertical_height_list,
                             float horizontal_width, Paint paint, Float[] values,
                             float maxY_values, String description, boolean perc){

        initializeValues(graphheight, width, graphwidth, height, hori_labels, canvas, horstart, border,
                vertical_height_list, horizontal_width, paint, values, description, perc);

        paint.setTextAlign(Paint.Align.LEFT);
        size = values.length;

        if(hori_labels != null) {
            size = hori_labels.size();
        }

        label_size = size - 1;
        hor_ratio =  maxX_values/label_size;  // Vertical label ratio
        paint.setColor(Color.BLACK);


        if(!percentage_stacked) {

            for (int i = 0; i < size; i++) {

                paint.setTextSize(18);
                createX_axis(i);
            }
        }
        else{
            paint.setTextSize(18);
            horizontal_percentage();
        }


        if(hori_labels != null) {
            size = hori_labels.size();

        }

        for(int j =0; j< size ; j++){
            createY_axis(j);
        }

        ver_height = ((graphheight / size) * size) + border;
        canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line

        if(description !=null){
            Description();
        }

        paint.setTextSize(18);

    }

    protected void createY_axis(int i){

        ver_height = ((graphheight / size) * i) + border;

        vertical_height_list.add(ver_height);

        if(i == values.length) {

            canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line
        }
        else {

            canvas.drawLine(horstart, ver_height, border, ver_height, paint); // Draw vertical line
        }

        paint.setColor(Color.BLACK);


        DrawLabelsString(i, ver_height);

    }

    protected void createX_axis(int i){

        horizontal_width = ((graphwidth / label_size) * i) + horstart;
        Log.e("horizontal width", horizontal_width+"");
        Log.e("label size", label_size+"");
        if(i==0){
            canvas.drawLine(horizontal_width, graphheight +border, horizontal_width, border, paint);

        } else{
            canvas.drawLine(horizontal_width,graphheight +border, horizontal_width, graphheight + 2*border, paint);
        }
        //canvas.drawLine(horstart, graphheight + border, horstart, border, paint);

        DrawHorizotalLabels(i);
    }


    protected void horizontal_percentage(){

        for(int i = 0; i < 5; i++){

            float ver_width = ((graphwidth / 4) * i) + horstart;

            if(i == 0){

                canvas.drawLine(ver_width, graphheight +border, ver_width, border, paint); // Draw vertical line
                paint.setTextAlign(Paint.Align.LEFT);
            }
            else {
                canvas.drawLine(ver_width, graphheight +border , ver_width, graphheight + 2*border, paint); // Draw vertical line
            }

            paint.setTextAlign(Paint.Align.RIGHT);
            paint.setColor(Color.BLACK);

            int y_labels = i * 25;

            canvas.drawText(""+y_labels, ver_width-10, height - 38, paint);
        }
    }


    protected void DrawLabelsString(int i, float ver_height){

        paint.setColor(Color.BLACK);
        paint.setTextAlign(Paint.Align.RIGHT);

        if(i >0){

            float ver_height1 = vertical_height_list.get(1) -  vertical_height_list.get(0);
            canvas.drawText(hori_labels.get(label_size-i), horstart-10, ver_height + ver_height1/1.5f, paint);

        } else if(i==0){

            canvas.drawText(hori_labels.get(label_size-i), horstart-10, ver_height + ver_height*2 , paint);
        }
    }

    protected void DrawHorizotalLabels(int i){

        paint.setTextAlign(Paint.Align.RIGHT);

        if (i==0)
            paint.setTextAlign(Paint.Align.LEFT);

        String x_values = String.format("%.1f", i * hor_ratio);
        paint.setColor(Color.BLACK);

        canvas.drawText(x_values, horizontal_width - 10, height - 38, paint);

    }

    protected void Description(){

        paint.setTextSize(28);
        paint.setTextAlign(Paint.Align.CENTER);
        this.canvas.drawText(description, graphwidth * 0.8f, height + 60, paint);
    }

    protected void initializeValues(float graphheight,float width,
                                    float graphwidth, float height,
                                    List<String> hori_labels, Canvas canvas,
                                    float horstart, float border,  List<Float> vertical_height_list,
                                    float horizontal_width, Paint paint, Float[] values,
                                    String description, boolean perc){

        this.graphheight = graphheight;
        this.width = width;
        this.graphwidth = graphwidth;
        this.height = height;
        this.hori_labels = hori_labels;
        this.canvas = canvas;
        this.horstart = horstart;
        this.border = border;
        this.vertical_height_list = vertical_height_list;
        this.horizontal_width = horizontal_width;
        this.paint = paint;
        this.values = values;
        this.description = description;
        this.percentage_stacked = perc;
    }

}
