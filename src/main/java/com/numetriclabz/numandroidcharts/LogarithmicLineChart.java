package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class LogarithmicLineChart extends View {

    float colwidth, x_cordinate, y_cordinate,graphheight, graphwidth;
    private float border = 30, horstart = border * 2;

    int height, width, base, color_no = 0;
    Paint paint;

    List<String> labels;
    List<ChartData> values;
    private List<Float> horizontal_width_list = new ArrayList<>();

    private List<ChartData> list_cordinate = new ArrayList<>();
    private List<ChartData> line_cordinate_list = new ArrayList<>();

    private  AxisFormatter axisFormatter = new AxisFormatter();
    private  List<String> legends_list;

    private List<Integer> color_code_list = new ArrayList<>();

    LogAxis axisRenderer;


    public LogarithmicLineChart(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

        paint = new Paint();
    }

    public void setLegends(List<String> legends){
        if (legends != null)
            this.legends_list = legends;
    }

    public void setData(List<ChartData> values){

        if(values != null)
            this.values = values;
    }


    public void setLabels(List<String> hori_labels){

        if (hori_labels != null)
            this.labels = hori_labels;
    }


    public void setBase(int base){
        this.base = base;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        int minW = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();
        int minH = getPaddingTop() + getPaddingBottom() + getSuggestedMinimumHeight();

        width = Math.max(minW, MeasureSpec.getSize(widthMeasureSpec));
        height = Math.max(minH, MeasureSpec.getSize(heightMeasureSpec));
        Log.e("height", height+"    "+MeasureSpec.getSize(heightMeasureSpec));

        setMeasuredDimension(width, height);
    }


    @Override
    protected void onDraw(Canvas canvas){

        init();

        axisRenderer = new LogAxis(width, height, labels, canvas, horizontal_width_list, paint, values, null, base);
        colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

        line_cordinate_list = StoredCordinate(graphheight);

        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);

        DrawLines(canvas);

        if(legends_list != null)
            new LegendRenderer(width, height, canvas).setLegendPoint(legends_list, color_code_list);
    }


    private List<ChartData> StoredCordinate(Float graphheight) {

        int mLogMax = axisRenderer.getMaxValue(values);
        int mLogMin = axisRenderer.getMinValue(values);

        int size = mLogMax - mLogMin;
        Log.e("size", size+"");

        for (int listIndex = 0; listIndex < values.size(); listIndex++) {

            list_cordinate = new ArrayList<>();

            List<ChartData> value = values.get(listIndex).getList();

            for (int index = 0; index < value.size(); index++) {

                float line_height = (graphheight / size ) * ((float)(Math.log(value.get(index).getYValue())/Math.log(base)) - size);


                y_cordinate = (border - line_height) + graphheight;
                x_cordinate = (index * colwidth) + horstart - 10;

                list_cordinate.add(new ChartData(y_cordinate, x_cordinate + horstart));
            }

            line_cordinate_list.add(new ChartData(list_cordinate));
        }
        return line_cordinate_list;
    }


    private void init() {

        graphheight = Math.min(height, width) - (3 * border);
        graphwidth = Math.min(height, width) - (3 * border);
    }


    private void DrawLines(Canvas canvas) {

        for(int listIndex = 0; listIndex < line_cordinate_list.size(); listIndex++){

            if(color_no > axisFormatter.getColorList().size())
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));

            List<ChartData> list = line_cordinate_list.get(listIndex).getList();

            Path path = new Path();
            path.reset();

            path.moveTo(list.get(0).getX_values(), list.get(0).getY_values());

            for (int i = 0; i < list.size(); i++) {

                path.lineTo(list.get(i).getX_values(), list.get(i).getY_values());
            }

            color_code_list.add(color_no);
            color_no += 1;
            canvas.drawPath(path, paint);
        }
    }
}
