package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class GaugeChart extends View{

    private List<String> colorList = new ArrayList<>();
    List<ChartData> data;
    Paint piePaint, textPaint;
    RectF mBounds;
    int width;
    int height;


    private Paint linePaint;
    private Path linePath;
    private Paint needlePaint;

    private Matrix matrix;
    private int framePS = 100;
    private long animDur = (long)16;
    private long startTime;

    float startX;
    float startY;

    private int angle;


    public GaugeChart(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        matrix = new Matrix();
        this.postInvalidate();
        init();
    }


    public void init(){

        piePaint = new Paint();

        piePaint.setAntiAlias(true);
        piePaint.setDither(true);
        piePaint.setStyle(Paint.Style.FILL);


        linePaint = new Paint();
        linePaint.setColor(Color.DKGRAY); // Set the color
        linePaint.setStyle(Paint.Style.FILL_AND_STROKE); // set the border and fills the inside of needle
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(2.0f); // width of the border

        textPaint = new Paint();
        textPaint.setTextSize(18f);
        textPaint.setColor(Color.BLACK);

    }

    public void setAngle(int angle){
        this.angle = angle;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        width = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = width  + getPaddingBottom() + getPaddingTop();
        height = Math.min(MeasureSpec.getSize(heightMeasureSpec), minh);
        setMeasuredDimension(width, height);

        this.startTime = System.currentTimeMillis();
    }


    @Override
    protected void onDraw(Canvas canvas){

        Rect bounds = new Rect();

        textPaint.getTextBounds("Good", 0, "Good".length(), bounds);

        int text_width = bounds.width() + 10;

        if (data != null){

            int arcCenterX = width/2 - 30;
            int arcCenterY = height/2 - 10;

            mBounds = new RectF(arcCenterX - 210, arcCenterY - 210, arcCenterX + 210, arcCenterY + 210);

            float[] segment = pieSegment();

            float segStartPoint = 180;

            for (int i = 0; i < segment.length; i++){

                if(i == 0){
                    canvas.drawText("Bad", arcCenterX - 210 - text_width, arcCenterY , textPaint);
                }

                if(i == segment.length - 1){
                    canvas.drawText("Good", arcCenterX + 210 + 10, arcCenterY , textPaint);
                }
                piePaint.setColor(Color.parseColor(getColorList().get(i)));
                canvas.drawArc(mBounds, segStartPoint, segment[i], true, piePaint);
                segStartPoint += segment[i];
            }

            // Draw the pointers
            final int totalNoOfPointers = 20;
            final int pointerMaxHeight = 25;
            final int pointerMinHeight = 15;

            int startX = arcCenterX - 210;
            int startY = arcCenterY-2;
            piePaint.setColor(Color.GRAY);
            piePaint.setStrokeWidth(5f);
            piePaint.setStrokeCap(Paint.Cap.ROUND);

            int pointerHeight;

            canvas.save();

            for (int i = 0; i <= totalNoOfPointers; i++) {

                if(i%5 == 0){
                    pointerHeight = pointerMaxHeight;
                }
                else{
                    pointerHeight = pointerMinHeight;
                }

                if(i == 5 || i == 10 || i == 15) {

                    int str = (((i* 100)/totalNoOfPointers) );
                    canvas.drawText(str+"%", startX - 40, startY, textPaint);
                }

                canvas.drawLine(startX, startY, startX + pointerHeight, startY, piePaint);
                canvas.rotate(180f/totalNoOfPointers, arcCenterX, arcCenterY);
            }
            canvas.restore();

            startX = arcCenterX  ;
            startY = arcCenterY ;

            linePath = new Path();
            linePath.moveTo(startX + 30, startY);
            linePath.lineTo(startX , startY - 10);
            linePath.lineTo(startX - 150, startY);
            linePath.lineTo(startX , startY + 10);
            linePath.lineTo(startX + 30, startY);
            linePath.close();

            needlePaint = new Paint();
            needlePaint.setColor(Color.BLACK);
            needlePaint.setAntiAlias(true);
            needlePaint.setShader(new RadialGradient(startX - 30, startY, 10.0f,
                    Color.DKGRAY, Color.BLACK, Shader.TileMode.CLAMP));


            long elapsedTime = System.currentTimeMillis() - startTime;

            matrix.postRotate(1.0f, startX , startY);
            canvas.concat(matrix);
            canvas.drawPath(linePath, linePaint);
            canvas.drawCircle(startX, startY, 16.0f, needlePaint);

            if(elapsedTime < animDur * angle) {
                this.postInvalidateDelayed(10000 / framePS);
                invalidate();
            }
        }
        else {
            piePaint.setColor(Color.BLUE);
            canvas.drawOval(mBounds, piePaint);
        }
    }


    private float[] pieSegment(){

        float[] segValues = new float[this.data.size()];
        float Total = getTotal();

        for (int i = 0; i < this.data.size(); i++){

            segValues[i] = (this.data.get(i).getValue()/Total) * 180;
        }

        return segValues;
    }


    private float getTotal(){

        Log.e("data string", this.data.size()+"");

        float total = 0;

        for(int i = 0; i < this.data.size(); i++){

            total += this.data.get(i).getValue();
        }
        return total;
    }


    public void setData(List<ChartData> data){

        this.data = data;
        invalidate();
    }

    public List<String> getColorList(){

        colorList.add("#FF4A4A");
        colorList.add("#FFFF38");
        colorList.add("#6FFF52");
        return colorList;
    }

}
