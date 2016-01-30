package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

public class LegendRenderer {

    float  width, graphwidth, height, border = 30;

    Canvas canvas;
    Paint paint, textPaint;

    private  int legendTop,legendLeft, legendRight, legendBottom;

    private  RectF legends;

    private List<String> colorList = new ArrayList<>();

    private void  init(){

        paint = new Paint();
        paint.setTextSize(18);

        graphwidth = Math.min(width, height) - (3 * border);
    }

    public LegendRenderer(float width, float height, Canvas canvas){

        this.width = width;
        this.height = height;
        this.canvas = canvas;

        init();
    }


    public void setLegendPoint(List<String> legends_list, List<Integer>color_code_list){

        legendTop = (int) Math.min(width, height);
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
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(r, paint);
        canvas.drawText(label, left + 40, top + 20, textPaint);
    }

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
        colorList.add("#000000");
        return colorList;
    }
}
