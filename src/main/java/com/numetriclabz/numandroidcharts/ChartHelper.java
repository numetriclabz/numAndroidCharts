package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.List;

public class ChartHelper {

    private List<ChartData> list_cordinate;
    private Canvas canvas;
    private Paint paint;
    private float border = 30, horstart = 2* border;
    public float left, tragetRight, tragetTop,bottom, targetY_cordinate1, targetY_cordinate2;
    private AxisFormatter axisFormatter = new AxisFormatter();

    public ChartHelper(List<ChartData> list_cordinate, Canvas canvas, Paint paint){
        this.list_cordinate = list_cordinate;
        this.canvas = canvas;
        this.paint = paint;

    }

    public void createBar(){
        for(int i=0;i<list_cordinate.size();i++){

            canvas.drawRect(list_cordinate.get(i).getLeft(), list_cordinate.get(i).getTop(),
                    list_cordinate.get(i).getRight(), list_cordinate.get(i).getBottom(), paint);
        }

    }

    public void DrawCircle(){

        paint.setColor(Color.parseColor("#FFB888"));
        for(int i=0; i< list_cordinate.size(); i++) {

            canvas.drawCircle(list_cordinate.get(i).getX_values(), list_cordinate.get(i).getY_values(), list_cordinate.get(i).getSize(), paint);

        }
    }

    public void DrawText() {
        paint.setColor(Color.BLUE);
        for (int i = 0; i < list_cordinate.size(); i++) {
            canvas.drawText(list_cordinate.get(i).getCordinate(),
                    list_cordinate.get(i).getX_values() - list_cordinate.get(i).getSize(),
                    list_cordinate.get(i).getY_values(), paint);
        }
    }

    public void  DrawTrendzone(int size, float colwidth, float graphheight, float maxY_values){
        left = horstart;
        tragetRight = (((size-1) * colwidth) + horstart) + (colwidth - 1);

        for(int i=0; i< list_cordinate.size(); i++){
            float barheight = (graphheight/maxY_values)*list_cordinate.get(i).getY_values() ;
            float barheight1 = (graphheight/maxY_values)*list_cordinate.get(i).getX_values() ;
            if(barheight < barheight1){
                bottom = (border - barheight) + graphheight;
                tragetTop = (border - barheight1) + graphheight;

            } else {
                tragetTop = (border - barheight) + graphheight;
                bottom = (border - barheight1) + graphheight;

            }

            targetTrendzoneText(list_cordinate.get(i).getCordinate());
        }
        paint.setColor(Color.parseColor(axisFormatter.getColorList().get(2)));

        canvas.drawRect(left, tragetTop, tragetRight, bottom, paint);
    }

    private void targetTrendzoneText(String targetText){

        paint.setColor(Color.BLACK);
        paint.setTextSize(25);

        float text_width = paint.measureText(targetText, 0, targetText.length());
        canvas.drawText(targetText, tragetRight - text_width, tragetTop - 10, paint);
    }

    public void DrawTrendlines(float graphheight, float maxY_values, float graphwidth){

        for(int i=0; i< list_cordinate.size();i++) {
            float line_height1 = (graphheight / maxY_values) * list_cordinate.get(i).getY_values();
            float line_height2 = (graphheight / maxY_values) * list_cordinate.get(i).getX_values();
            targetY_cordinate1 = (border - line_height1) + graphheight;
            targetY_cordinate2 = (border - line_height2) + graphheight;

            paint.setStrokeWidth(3);
            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(5)));
            canvas.drawLine(horstart, targetY_cordinate1, graphwidth + horstart, targetY_cordinate1, paint);

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(6)));
            canvas.drawLine(horstart, targetY_cordinate2, graphwidth + horstart, targetY_cordinate2, paint);
            paint.setStrokeWidth(0);

            targetLineText(graphwidth, list_cordinate.get(i).getCordinate(), list_cordinate.get(i).getTrendlineText());
        }
    }

    public void targetLineText(float graphwidth, String targettext, String targetText1){

        float text_width1 = paint.measureText(targetText1, 0, targetText1.length());
        float text_width = paint.measureText(targettext, 0, targettext.length());

        if(targetY_cordinate1 < targetY_cordinate2){

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(5)));
            canvas.drawText(targetText1, graphwidth+horstart - text_width1, targetY_cordinate1 - 10, paint);
            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(6)));
            canvas.drawText(targettext, graphwidth+horstart - text_width, targetY_cordinate2 - 10, paint);

        } else {
            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(6)));
            canvas.drawText(targetText1, graphwidth + horstart - text_width1, targetY_cordinate2 - 10, paint);
            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(5)));
            canvas.drawText(targettext, graphwidth+horstart - text_width, targetY_cordinate1 - 10, paint);
        }

    }


}
