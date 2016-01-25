package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class BubbleChart extends View {

    private Paint paint;
    private List<ChartData>  values;
    private List<String> hori_labels;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private String description;
    private float border = 30, horstart = border * 2;
    private int parentHeight ,parentWidth;
    private static final int INVALID_POINTER_ID = -1;
    private float mPosX;
    private float mPosY;
    private float mLastTouchX;
    private float mLastTouchY;
    private int mActivePointerId = INVALID_POINTER_ID;
    private Boolean gesture = false;
    private ScaleGestureDetector mScaleDetector;
    private float mScaleFactor = 1.f;
    private Canvas canvas;
    private List<ChartData> list_cordinate = new ArrayList<>();
    private float y_cordinate, height ,width, maxY_values, maxX_values, min, graphheight, graphwidth;
    private AxisFormatter axisFormatter = new AxisFormatter();

    public BubbleChart(Context context, AttributeSet attrs){
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        Paint paint = new Paint();
        this.paint = paint;
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
    protected void onDraw(Canvas canvas) {

        intilaizeValue(canvas);

        if(gesture == true) {

            CanvasScaleFator();
        }

        axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                horizontal_width_list, paint, values, maxX_values, description, false, false);

        if (maxY_values != min && values != null) {

            paint.setColor(Color.BLUE);

            float colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

            list_cordinate = StoredCordinate(graphheight,colwidth);

            ChartHelper chartHelper = new ChartHelper(list_cordinate,canvas, paint);
            chartHelper.DrawCircle();
            chartHelper.DrawText();

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
        maxX_values = axisFormatter.getMaxX_Values(values);
        min = axisFormatter.getMinValues(values);
        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;

    }

    private  List<ChartData> StoredCordinate(Float graphheight,Float colwidth){

        for(int i = 0;i<values.size(); i++){

            float x_ratio = (maxX_values/(values.size()-1));
            float x_cordinate = (colwidth/x_ratio) *values.get(i).getX_values();
            float line_height = (graphheight / maxY_values) * values.get(i).getY_values();
            y_cordinate = (border - line_height) + graphheight ;
            list_cordinate.add(new ChartData(y_cordinate,x_cordinate + horstart,values.get(i).getSize(),
                    "(" + values.get(i).getX_values() + ", " + values.get(i).getY_values() + ")"));
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
