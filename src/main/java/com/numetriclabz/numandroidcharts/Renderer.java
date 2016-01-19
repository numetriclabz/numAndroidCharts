package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.List;

public class Renderer {

    private static final float curve_intensity = 0.16f;

    protected void DrawCubicPath(Canvas canvas, List<ChartData> dataList, Paint paint, float height, boolean area_spline) {

        final int lineSize = dataList.size();
        float prePriviousX = Float.NaN;
        float prePreviousY = Float.NaN;
        float previousX = Float.NaN;
        float previousY = Float.NaN;
        float curr_x = Float.NaN;
        float curr_y = Float.NaN;
        float next_x= Float.NaN;
        float next_y = Float.NaN;

        Path path = new Path();

        if(area_spline){
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setAlpha(100);
            path.moveTo(dataList.get(0).getX_values(), height);
            path.lineTo(dataList.get(0).getX_values(),
                    dataList.get(0).getY_values());
        }

        for (int valueIndex = 0; valueIndex < lineSize; ++valueIndex) {

            if (Float.isNaN(curr_x)) {

                curr_x = dataList.get(valueIndex).getX_values();
                curr_y = dataList.get(valueIndex).getY_values();
            }
            if (Float.isNaN(previousX)) {

                if (valueIndex > 0) {

                    previousX = dataList.get(valueIndex - 1).getX_values();
                    previousY = dataList.get(valueIndex - 1).getY_values();
                } else {
                    previousX = curr_x;
                    previousY = curr_y;
                }
            }

            if (Float.isNaN(prePriviousX)) {

                if (valueIndex > 1) {

                    prePriviousX = dataList.get(valueIndex - 2).getX_values();
                    prePreviousY = dataList.get(valueIndex - 2).getY_values();
                }
                else {
                    prePriviousX = previousX;
                    prePreviousY = previousY;
                }
            }

            // nextPoint is always new one or it is equal currentPoint.
            if (valueIndex < lineSize - 1) {

                next_x = dataList.get(valueIndex + 1).getX_values();
                next_y = dataList.get(valueIndex + 1).getY_values();
            }
            else {
                next_x = curr_x;
                next_y = curr_y;
            }

            if (valueIndex == 0) {

                if(!area_spline)
                    path.moveTo(curr_x, curr_y);
            }
            else {
                // Calculate control points.
                final float first_diff_x = (curr_x - prePriviousX);
                final float fisrt_diff_y = (curr_y - prePreviousY);
                final float sec_diff_x = (next_x - previousX);
                final float sec_diff_y = (next_y - previousY);
                final float first_control_x = previousX + (curve_intensity * first_diff_x);
                final float first_control_y = previousY + (curve_intensity * fisrt_diff_y);
                final float secondControlPointX = curr_x - (curve_intensity * sec_diff_x);
                final float secondControlPointY = curr_y - (curve_intensity * sec_diff_y);
                path.cubicTo(first_control_x, first_control_y, secondControlPointX, secondControlPointY,
                        curr_x, curr_y);
            }

            prePriviousX = previousX;
            prePreviousY = previousY;
            previousX = curr_x;
            previousY = curr_y;
            curr_x = next_x;
            curr_y = next_y;
        }

        if(area_spline){
            path.lineTo(dataList.get(dataList.size()-1).getX_values(),
                    height);
        }

        canvas.drawPath(path, paint);
        path.reset();
    }

    protected void DrawCircle(Canvas canvas, List<ChartData> dataset, Paint paint, float radius){

        paint.setStyle(Paint.Style.FILL);

        for(int i=0; i< dataset.size(); i++) {

            canvas.drawCircle(dataset.get(i).getX_values(),
                    dataset.get(i).getY_values(),
                    radius, paint);
        }
    }
}
