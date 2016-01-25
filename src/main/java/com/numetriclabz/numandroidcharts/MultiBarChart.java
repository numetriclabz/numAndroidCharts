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

public class MultiBarChart extends View {
    private Paint paint;
    private List<ChartData> values;
    private List<String> hori_labels;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private String description;
    private float horizontal_width,  border = 30, horstart = border * 2;
    private int parentHeight ,parentWidth, color_no = 0;
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
    private List<ChartData> bar_cordinate_list = new ArrayList<>();
    private float x_cordinate, y_cordinate, height ,width, maxY_values, maxX_values, graphheight, graphwidth;
    private AxisFormatter axisFormatter = new AxisFormatter();
    private List<Integer> color_code_list = new ArrayList<>();
    private  List<String> legends_list;


    public MultiBarChart(Context context, AttributeSet attrs){
        super(context, attrs);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());

        Paint paint = new Paint();
        this.paint = paint;
    }
    public void setData(List<ChartData> values){

        if(values != null) {
            this.values = values;
        }

    }

    public void setLegends(List<String> legends){
        if (legends != null)
        this.legends_list = legends;
    }

    public void setLabels(List<String> hori_labels){

        if (hori_labels != null)
            this.hori_labels = hori_labels;
    }

    public void setGesture(Boolean gesture){
        this.gesture = gesture;
    }

    protected void onDraw(Canvas canvas) {
        if (values != null) {

            intilaizeValue(canvas);

            if (gesture == true) {
                CanvasScaleFator();
            }

            int largestSize = axisFormatter.getLargestSize(values);

            axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                    horizontal_width_list, paint, values.get(largestSize).getList(), maxX_values, null, false, false);


            bar_cordinate_list = StoredCordinate(graphheight);

            DrawRect();
            DrawText();
            if (legends_list != null)
            axisFormatter.setLegegendPoint(legends_list, color_code_list);

            if (gesture == true) {
                canvas.restore();
            }


        }
    }

    private void DrawRect(){

        for(int i =0; i < bar_cordinate_list.size();i++){
            if(color_no > axisFormatter.getColorList().size())
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));

            for(int j =0; j< bar_cordinate_list.get(i).getList().size(); j++){
                canvas.drawRect(bar_cordinate_list.get(i).getList().get(j).getLeft(),
                        bar_cordinate_list.get(i).getList().get(j).getTop(),
                        bar_cordinate_list.get(i).getList().get(j).getRight(),
                        bar_cordinate_list.get(i).getList().get(j).getBottom(),paint);

            }
            color_code_list.add(color_no);
            color_no +=1;
        }
    }

    private void CanvasScaleFator(){

        canvas.save();
        canvas.translate(mPosX, mPosY);
        canvas.scale(mScaleFactor, mScaleFactor);
    }

    private  List<ChartData> StoredCordinate(Float graphheight){

        float colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

        for(int i = 0;i<values.size(); i++){
            list_cordinate = new ArrayList<>();

            for(int j =0; j< values.get(i).getList().size();j++) {

                float x_ratio = (maxX_values / (axisFormatter.getSmallestSize(values)));
                float barheight = (graphheight / maxY_values) * values.get(i).getList().get(j).getY_values();
                float left = ((colwidth / x_ratio) * values.get(i).getList().get(j).getX_values()) + border;
                float top = (border - barheight) + graphheight;
                float right = ((colwidth / x_ratio) * values.get(i).getList().get(j).getX_values()) + horstart;
                float bottom = graphheight + border;

                list_cordinate.add(new ChartData(left, top, right, bottom));
            }
            bar_cordinate_list.add(new ChartData(list_cordinate));
        }

        return bar_cordinate_list;
    }

    private void DrawText() {
        color_no = 0;

        for (int i = 0; i < values.size(); i++) {
            if(color_no > axisFormatter.getColorList().size())
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));

            for(int j =0; j< values.get(i).getList().size(); j++) {
                if((bar_cordinate_list.get(i).getList().get(j).getTop() -border) > 0) {

                    canvas.drawText(Float.toString(values.get(i).getList().get(j).getY_values()),
                            bar_cordinate_list.get(i).getList().get(j).getLeft() +border,
                            bar_cordinate_list.get(i).getList().get(j).getTop()-border, paint);
                } else {

                    canvas.drawText(Float.toString(values.get(i).getList().get(j).getY_values()),
                            bar_cordinate_list.get(i).getList().get(j).getLeft() +border -40,
                            bar_cordinate_list.get(i).getList().get(j).getTop()+border, paint);
                }
            }

            color_no +=1;

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

    private void intilaizeValue(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;
        maxY_values = axisFormatter.getMultiMaxY_Values(values);
        maxX_values = axisFormatter.getMultiMaxX_Values(values);
        // min = axisFormatter.getMinValues(values);
        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;
    }


    // Get the Width and Height defined in the activity xml file
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
