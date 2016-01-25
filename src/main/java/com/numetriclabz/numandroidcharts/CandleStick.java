package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;

public class CandleStick extends View{

    private Paint paint;
    private List<ChartData> values;
    private List<String> hori_labels;
    private Boolean gesture = false;
    private int parentWidth, parentHeight;
    private Canvas canvas;
    private List<ChartData> list_cordinate = new ArrayList<>();
    private List<ChartData> line_cordinate = new ArrayList<>();
    private float y1_cordinate, y2_cordinate, height ,width, maxY_values, maxX_values, min, graphheight, graphwidth;
    private float border = 30, horstart = border * 2;
    private String description;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private static final int INVALID_POINTER_ID = -1;
    private float mPosX;
    private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;

    public CandleStick(Context context, AttributeSet attrs){
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        Paint paint = new Paint();
        this.paint = paint;

    }

    public void setData(List<ChartData> values){
        AxisFormatter axisFormatter = new AxisFormatter();
        float Max_x = axisFormatter.getMaxX_Values(values);
        float Max_y = getMaxY_Values(values);
        values.add(new ChartData(Max_x+1f, Max_y+1f, 0f,    0f, 0f));
        this.values = values;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setLabels(List<String> hori_labels){

        if (hori_labels != null)
            this.hori_labels = hori_labels;
    }

    public void setGesture(Boolean gesture){
        this.gesture = gesture;
    }

    // Get the Width and Height defined in the activity xml file
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    protected void onDraw(Canvas canvas){
        intilaizeValue(canvas);

        if(gesture == true) {
            CanvasScaleFator();
        }

        AxisFormatter axisFormatter = new AxisFormatter();
        axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                horizontal_width_list, paint, values, maxX_values, description, false, false);

        LineCordinate(graphheight);

        DrawLine();
        RectCordinate();
        DrawRectangle();
        Drawtext();

        if(gesture == true) {
            canvas.restore();
        }

    }

    private  List<ChartData> LineCordinate(Float graphheight){

        float colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

        for(int i = 0;i<values.size(); i++){

            float x_ratio = (maxX_values/ (values.size()-1));
            float x_cordinate = (colwidth/x_ratio) *values.get(i).getX_values();
            float line_height1 = (graphheight / maxY_values) * values.get(i).getLowest_value();
            float line_height2 = (graphheight / maxY_values) * values.get(i).getHighest_value();
            y1_cordinate = (border - line_height1) + graphheight ;
            y2_cordinate = (border - line_height2) + graphheight ;
            line_cordinate.add(new ChartData(y1_cordinate,x_cordinate + horstart,y2_cordinate, values.get(i).getHighest_value()));
        }

        return line_cordinate;
    }

    private List<ChartData> RectCordinate(){

        float colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

        for(int i = 0;i<values.size(); i++){

            float x_ratio = (maxX_values/(values.size()-1));
            float barheight1 = (graphheight/maxY_values)*values.get(i).getClosing() ;
            float barheight2 = (graphheight/maxY_values)*values.get(i).getOpening() ;
            float left = ((colwidth/x_ratio) *values.get(i).getX_values() )+ border +10 ;
            float top = (border - barheight1) + graphheight;
            float right = ((colwidth/x_ratio) *values.get(i).getX_values() + horstart+20) ;
            float bottom =  (border - barheight2) + graphheight;;

            list_cordinate.add(new ChartData(left, top, right, bottom));
        }

        return list_cordinate;
    }

    protected void DrawLine(){
        paint.setColor(Color.parseColor("#FF00FF"));

        for(int i=0;i<line_cordinate.size()-1;i++){

            canvas.drawLine(line_cordinate.get(i).getTop(), line_cordinate.get(i).getLeft(),
                    line_cordinate.get(i).getTop(), line_cordinate.get(i).getRight(), paint);
        }
    }

    private void Drawtext(){
        paint.setTextSize(25);
        paint.setColor(Color.BLUE);
        for (int i = 0; i < line_cordinate.size(); i++) {

              if(maxY_values != line_cordinate.get(i).getBottom())

                canvas.drawText(Float.toString(line_cordinate.get(i).getBottom()), line_cordinate.get(i).getTop(),
                        line_cordinate.get(i).getRight() - 10, paint);

        }
    }

    protected void DrawRectangle(){

        paint.setColor(Color.parseColor("#8B0000"));
        for(int i=0;i<list_cordinate.size();i++){

            canvas.drawRect(list_cordinate.get(i).getLeft(), list_cordinate.get(i).getTop(),
                    list_cordinate.get(i).getRight(), list_cordinate.get(i).getBottom(), paint);
        }
    }

    private void intilaizeValue(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;
        AxisFormatter axisFormatter = new AxisFormatter();
        maxY_values = getMaxY_Values(values);
        maxX_values = axisFormatter.getMaxX_Values(values);
        min = getMinValues(values);
        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;
    }

    //  return the maximum y_value
    public float getMaxY_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getHighest_value() > largest)
                largest = values.get(i).getHighest_value();
        return largest;
    }

    // return the minimum value
    public float getMinValues(List<ChartData> values) {

        float smallest = Integer.MAX_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getHighest_value() < smallest)
                smallest = values.get(i).getX_values();
        return smallest;
    }

    private void CanvasScaleFator(){

        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(.1f, Math.min(mScaleFactor, 10.0f));

            invalidate();
            return true;
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        // Let the ScaleGestureDetector inspect all events.
        mScaleDetector.onTouchEvent(ev);

        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: {
                final float x = ev.getX();
                final float y = ev.getY();

                mLastTouchX = x;
                mLastTouchY = y;
                mActivePointerId = ev.getPointerId(0);
                break;
            }

            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);

                // Only move if the ScaleGestureDetector isn't processing a gesture.
                if (!mScaleDetector.isInProgress()) {
                    final float dx = x - mLastTouchX;
                    final float dy = y - mLastTouchY;

                    mPosX += dx;
                    mPosY += dy;

                    invalidate();
                }

                mLastTouchX = x;
                mLastTouchY = y;

                break;
            }

            case MotionEvent.ACTION_UP: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_CANCEL: {
                mActivePointerId = INVALID_POINTER_ID;
                break;
            }

            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK)
                        >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    // This was our active pointer going up. Choose a new
                    // active pointer and adjust accordingly.
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastTouchX = ev.getX(newPointerIndex);
                    mLastTouchY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }

        return true;
    }
}
