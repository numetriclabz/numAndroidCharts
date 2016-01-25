package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ScatterChart extends View {

    private Paint paint;
    private List<ChartData>  values;
    private List<String> hori_labels;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private String description;
    private float minY_values,  border = 30, horstart = border * 2, circleSize = 15f;
    private int parentHeight ,parentWidth;
    private static final int INVALID_POINTER_ID = -1;
    private float mPosX, mPosY, mLastTouchX, mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private Boolean gesture = false;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private Canvas canvas;
    private  List<ChartData> list_cordinate = new ArrayList<>();
    private  float y_cordinate, height ,width, maxY_values, maxX_values, min, graphheight, graphwidth;
    private Boolean inverseAxis = false;

    public ScatterChart(Context context, AttributeSet attrs){
        super(context,attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        Paint paint = new Paint();

        this.paint = paint;
    }

    // Get the Width and Height defined in the activity xml file
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public void setData(List<ChartData> values){

        if(values != null)
            this.values = values;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setLabels(List<String> hori_labels){

        if (hori_labels != null)
            this.hori_labels = hori_labels;
    }

    public void setCircleSize(Float circleSize){
        this.circleSize = circleSize;
    }

    public void setGesture(Boolean gesture){
        this.gesture = gesture;
    }


    public void setInverseY_Axis(boolean inverseAxis){
        this.inverseAxis = inverseAxis;
    }

    protected void onDraw(Canvas canvas) {

        intilaizeValue(canvas);

        if(gesture == true) {

            CanvasScaleFator();
        }

        AxisFormatter axisFormatter = new AxisFormatter();
        axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                horizontal_width_list, paint, values, maxX_values, description, inverseAxis, true);

        if (maxY_values != min && values != null) {

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(0)));
            list_cordinate = StoredCordinate(graphheight);

            DrawCircle();
            DrawText();

            if(gesture == true) {
                canvas.restore();
            }
        }
    }

    private void CanvasScaleFator(){

        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);
    }

    private void intilaizeValue(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;
        AxisFormatter axisFormatter = new AxisFormatter();
        maxY_values = axisFormatter.getMaxY_Values(values);
        if (values.get(0).getLabels() == null)
        maxX_values = axisFormatter.getMaxX_Values(values);
        minY_values = axisFormatter.getMinValues(values);
        min = axisFormatter.getMinValues(values);
        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;
    }

    private void DrawCircle(){

        for(int i=0; i< list_cordinate.size(); i++) {

            canvas.drawCircle(list_cordinate.get(i).getX_values(), list_cordinate.get(i).getY_values(), circleSize, paint);
        }
    }

    private void DrawText() {
        paint.setTextAlign(Paint.Align.RIGHT);
        for (int i = 0; i < values.size(); i++) {
            canvas.drawText(values.get(i).getY_values() + "",
                    list_cordinate.get(i).getX_values() - 30,
                    list_cordinate.get(i).getY_values(), paint);
        }
    }

    private  List<ChartData> StoredCordinate(Float graphheight){

        float colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

        for(int i = 0;i<values.size(); i++){

            float x_ratio = 0;
            float x_cordinate;
            float ver_ratio = maxY_values/values.size();
            float line_height = (graphheight / (maxY_values + (int) ver_ratio)) * (values.get(i).getY_values() - minY_values);
            if(inverseAxis == true){
                y_cordinate = line_height + border;

            } else {

                y_cordinate = (border - line_height) + graphheight;
            }

            if (values.get(0).getLabels() == null) {

                x_ratio = (maxX_values / (values.size() - 1));
                x_cordinate = (colwidth / x_ratio) * values.get(i).getX_values();


            } else {

                x_cordinate = (i * colwidth) + horstart;

            }

            list_cordinate.add(new ChartData(y_cordinate,x_cordinate + horstart));
        }

        return list_cordinate;
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