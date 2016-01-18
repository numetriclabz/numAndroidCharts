package com.numetriclabz.numandroidcharts;


import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.List;

public class ChartHelper {

    public void createBar(List<ChartData> list_cordinate, Canvas canvas, Paint paint){
        for(int i=0;i<list_cordinate.size();i++){

            canvas.drawRect(list_cordinate.get(i).getLeft(), list_cordinate.get(i).getTop(),
                    list_cordinate.get(i).getRight(), list_cordinate.get(i).getBottom(), paint);
        }

    }
}
