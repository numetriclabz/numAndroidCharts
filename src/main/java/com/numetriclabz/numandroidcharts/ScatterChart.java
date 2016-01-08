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

    public Paint paint;
    public List<Float>  values;
    public List<String> hori_labels;
    public List<Float> horizontal_width_list = new ArrayList<>();
    public String title;
    float horizontal_width,  border = 30, horstart = border * 2;
    int parentHeight ,parentWidth;
    private static final int INVALID_POINTER_ID = -1;
    private float mPosX;
    private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    public Boolean gesture = false;
    Canvas canvas ;
    public  int circleSize = 10;



    public ScatterChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setData(List<Float> values){

        if(values != null)
            this.values = values;

        paint = new Paint();
    }

    public void setGesture(Boolean gesture){
        this.gesture = gesture;
    }

    public void setCircleSize(int circleSize){
        this.circleSize = circleSize;
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



    // Get the Width and Height defined in the activity xml file
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    //  return the maximum value
    private float getMaxValues() {

        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i) > largest)
                largest = values.get(i);
        return largest;
    }
    // return the minimum value
    private float getMinValues() {

        float smallest = Integer.MAX_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i) < smallest)
                smallest = values.get(i);
        return smallest;
    }


    public void set_horizontal_labels(List<String> hori_labels){

        if (hori_labels != null)
            this.hori_labels = hori_labels;
    }

    protected void onDraw(Canvas canvas) {

        float height = parentHeight;
        float width = parentWidth;
        float max = getMaxValues();
        float min = getMinValues();
        float graphheight = height - (2 * border);
        float graphwidth = width - (2 * border);
        this.canvas = canvas;

        if(gesture == true) {
            canvas.save();
            canvas.translate(mPosX, mPosY);
            canvas.scale(mScaleFactor, mScaleFactor);
        }

        /*ChartingHelper chartingHelper = new ChartingHelper();
        chartingHelper.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, max, canvas,
                horstart, border, horizontal_width_list, horizontal_width, paint);*/
        if(gesture == true) {
            canvas.restore();
        }

        if (max != min && values != null) {

            paint.setColor(Color.BLUE);

            float colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

            for (int i = 0; i < values.size(); i++) {

                float line_height = (graphheight / max) * values.get(i);
                float x_cordinate = (i * colwidth) + (horstart);
                float y_cordinate = (border - line_height) + graphheight;

                if(gesture == true) {
                    canvas.save();
                    canvas.translate(mPosX, mPosY);
                    canvas.scale(mScaleFactor, mScaleFactor);
                }

                canvas.drawCircle(x_cordinate, y_cordinate, circleSize, paint);
                paint.setTextSize(20);
                canvas.drawText(Float.toString(values.get(i)), x_cordinate - circleSize-5 , y_cordinate , paint);
                if(gesture == true) {
                    canvas.restore();
                }

            }
        }
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


}