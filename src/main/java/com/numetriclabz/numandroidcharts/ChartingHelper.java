package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.List;

public class ChartingHelper {

    // Plot XY Lables
    public void PlotXYLabels(float graphheight,float width,
                             float graphwidth, float height,
                             List<String> hori_labels, float maxY_values, Canvas canvas,
                             float horstart, float border,  List<Float> horizontal_width_list,
                             float horizontal_width, Paint paint, List<LineData> values,
                             float maxX_values){

        paint.setTextAlign(Paint.Align.LEFT);
        int label_size = values.size() - 1;
        float ver_ratio =  maxY_values/label_size;  // Vertical label ratio
        float hor_ratio = maxX_values/label_size;

        for (int i = 0; i < values.size(); i++) {

            paint.setColor(Color.BLACK);
            paint.setTextSize(18);

            float ver_height = ((graphheight / label_size) * i) + border;
            canvas.drawLine(horstart, ver_height, width, ver_height, paint); // Draw vertical line
            paint.setColor(Color.BLACK);
            int Y_labels =  (int) values.size()-1- i;
            String y_labels = String.format("%.1f", Y_labels*ver_ratio);
            String x_values = String.format("%.1f", i * hor_ratio);
            canvas.drawText(y_labels, border, ver_height, paint);

            paint.setColor(Color.BLACK);

            horizontal_width = ((graphwidth / label_size) * i) + horstart;
            horizontal_width_list.add(horizontal_width);
            canvas.drawLine(horizontal_width, height - border, horizontal_width, border, paint);
            paint.setTextAlign(Paint.Align.CENTER);

            if (i== values.size()-1)
                paint.setTextAlign(Paint.Align.RIGHT);

            if (i==0)
                paint.setTextAlign(Paint.Align.LEFT);

            paint.setColor(Color.BLACK);
            canvas.drawText(x_values, horizontal_width, height - 4, paint);
        }

        //  paint.setTextAlign(Paint.Align.CENTER);
        // this.canvas.drawText(title, (graphwidth / 2) + horstart, border - 4, paint);
    }

}
