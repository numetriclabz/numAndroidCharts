package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class LineChart extends View {

    public Paint paint;
    public List<LineData>  values;
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
    Canvas canvas;
    public Boolean gesture = false;



    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
    }

    public void setData(List<LineData> values){

        if(values != null)
            this.values = values;

        paint = new Paint();
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

    //  return the maximum y_value
    private float getMaxY_Values() {

        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getY_values() > largest)
                largest = values.get(i).getY_values();
        return largest;
    }

    //  return the maximum y_value
    private float getMaxX_Values() {

        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getX_values() > largest)
                largest = values.get(i).getX_values();
        return largest;
    }

    // return the minimum value
    private float getMinValues() {

        float smallest = Integer.MAX_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getY_values() < smallest)
                smallest = values.get(i).getX_values();
        return smallest;
    }


    public void set_horizontal_labels(List<String> hori_labels){

        if (hori_labels != null)
            this.hori_labels = hori_labels;
    }

    public void setGesture(Boolean gesture){
        this.gesture = gesture;
    }

    protected void onDraw(Canvas canvas) {

        float height = parentHeight;
        float width = parentWidth;
        float maxY_values = getMaxY_Values();
        float maxX_values = getMaxX_Values();
        float min = getMinValues();
        float graphheight = height - (2 * border);
        float graphwidth = width - (2 * border);
        this.canvas = canvas;

        if(gesture == true) {

            canvas.save();
            canvas.translate(mPosX, mPosY);
            canvas.scale(mScaleFactor, mScaleFactor);
        }

        ChartingHelper chartingHelper = new ChartingHelper();

        chartingHelper.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                horstart, border, horizontal_width_list, horizontal_width, paint, values, maxX_values);

        //canvas.restore();

        if (maxY_values != min && values != null) {

            paint.setColor(Color.BLUE);

            float colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);
            float last_height = 0;

            for (int i = 0; i < values.size(); i++) {

                float line_height = (graphheight / maxY_values) * values.get(i).getY_values();
                Log.e("total_width",Float.toString(graphwidth));
                float x_width = (graphwidth/maxX_values);
                Log.e("x_width",Float.toString(x_width));
                float stop_x = (i * colwidth) + (horstart);
                float y_cordinate = (border - line_height) + graphheight ;
                float start_x = ((i - 1) * (colwidth) ) + (horstart  );
                if(gesture == true) {
                    canvas.save();
                    canvas.translate(mPosX, mPosY);
                    canvas.scale(mScaleFactor, mScaleFactor);
                }

                if (i > 0)
                    canvas.drawLine(start_x, (border - last_height) + graphheight,
                            stop_x  , y_cordinate, paint);

                canvas.drawCircle(stop_x, y_cordinate, 5, paint);
                canvas.drawText(Float.toString(values.get(i).getY_values()), stop_x - 20, y_cordinate, paint);


                last_height = line_height;
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