package com.numetriclabz.numandroidcharts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.ArrayList;
import java.util.List;


public class ColumnRangeChart extends View {
    private Paint paint;
    private List<ChartData>  values;
    private List<Float> vertical_height_list = new ArrayList<>();
    private List<Float> smallestRange  = new ArrayList<>();
    private List<Float> largestRange  = new ArrayList<>();
    private String description;
    private float  border = 30, horstart = border * 2;
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
    private float height ,width, maxY_values, maxX_values, min, graphheight, graphwidth;
    private float left, right, top, bottom, barheight, verheight, barheight2;


    public ColumnRangeChart(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        Paint paint = new Paint();
        this.paint = paint;
    }

    public void setData(List<ChartData> values){

        if(values != null)
            this.values = values;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGesture(Boolean gesture) {
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

        HorizontalAxisFormatter axisFormatter = new HorizontalAxisFormatter();
        axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, null, maxY_values, canvas,
                vertical_height_list, paint, values, maxX_values, description);

        if (values != null) {
            AxisFormatter axisFormatter1 = new AxisFormatter();
            paint.setColor(Color.parseColor(axisFormatter1.getColorList().get(0)));


            list_cordinate = StoredCordinate(graphheight);
            ChartHelper chartHelper = new ChartHelper(list_cordinate, canvas, paint);
            chartHelper.createBar();
            DrawText();

            if(gesture == true) {
                canvas.restore();
            }
        }
    }

    private  List<ChartData> StoredCordinate(Float graphheight){

        verheight = vertical_height_list.get(2) - vertical_height_list.get(1);
        float x_ratio = 0;

        for(int i = 0;i<values.size(); i++){

            getBarHeight(i);
            if(values.get(0).getLabels() != null){

                left = barheight2+horstart;
                right = barheight + horstart ;
                top =  graphheight - vertical_height_list.get(i) ;
                bottom = top + border;

            }
            else{

                x_ratio = (maxX_values/(values.size()-1));
                left = barheight2+horstart;
                top = graphheight - ((verheight/x_ratio) *values.get( i).getSize()) +border ;
                right = barheight + horstart ;
                bottom =  top + border;
            }

            list_cordinate.add(new ChartData(left, top, right, bottom));
        }

        return list_cordinate;
    }

    private void getBarHeight(int i){
        float range1 = (graphwidth/maxY_values)*values.get(i).getX_values();
        float range2 = (graphwidth/maxY_values)*values.get(i).getY_values();

        if(range1 > range2){
            barheight = range1;
            barheight2 = range2;
            smallestRange.add(values.get(i).getY_values());
            largestRange.add(values.get(i).getX_values());

        } else {

            barheight = range2;
            barheight2 = range1;
            smallestRange.add(values.get(i).getX_values());
            largestRange.add(values.get(i).getY_values());

        }

    }

    private void intilaizeValue(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;

        maxY_values = getMaxY_Values(values);

        if(values.get(0).getLabels() == null)
            maxX_values = getMaxX_Values(values);

        // min = axisFormatter.getMinValues(values);
        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;

    }

    private void DrawText(){
        AxisFormatter axisFormatter = new AxisFormatter();
        paint.setColor(Color.parseColor(axisFormatter.getColorList().get(1)));
        for(int i =0; i< list_cordinate.size();i++){
            canvas.drawText(smallestRange.get(i)+"",
                    list_cordinate.get(i).getLeft() - border,
                    list_cordinate.get(i).getTop()+border, paint);

            canvas.drawText(largestRange.get(i)+"",
                    list_cordinate.get(i).getRight() + border,
                    list_cordinate.get(i).getTop()+border, paint);

        }
    }

    //  return the maximum y_value
    public float getMaxY_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).getY_values() > largest)
                largest = values.get(i).getY_values();
            if (values.get(i).getX_values() > largest)
                largest = values.get(i).getX_values();
        }
        return largest;
    }
    //  return the maximum y_value
    public float getMaxX_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getSize() > largest)
                largest = values.get(i).getSize();
        return largest;
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

    private void CanvasScaleFator(){

        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);
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
