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
import android.widget.Switch;

import java.util.ArrayList;
import java.util.List;

public class MultiScatterChart extends View{
    private Paint paint;
    private List<ChartData> values;
    private List<String> hori_labels;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private String description;
    private float horizontal_width,  border = 30, horstart = border * 2, circleSize = 15f, colwidth;
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
    private float x_cordinate, y_cordinate, height ,width, maxY_values, maxX_values, graphheight, graphwidth;
    private AxisFormatter axisFormatter = new AxisFormatter();
    private List<Integer> color_code_list = new ArrayList<>();
    private  List<String> legends_list;

    public MultiScatterChart(Context context, AttributeSet attrs){
        super(context,attrs);

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

    public void setLegends(List<String> legends){
        if (legends != null)
            this.legends_list = legends;
    }

//    public void setCircleSize(Float circleSize){
//        this.circleSize = circleSize;
//    }

    protected void onDraw(Canvas canvas) {
        if (values != null) {

            intilaizeValue(canvas);

            if (gesture == true) {
                CanvasScaleFator();
            }

            int largestSize = axisFormatter.getLargestSize(values);

            axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                    horizontal_width_list, paint, values.get(largestSize).getList(), maxX_values, null, false, false);

            colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);

            DrawScatter();
            Log.e("color_code_list",color_code_list.size()+"");
            if(legends_list != null)
                axisFormatter.setLegegendPoint(legends_list, color_code_list);

            if (gesture == true) {
                canvas.restore();
            }

        }
    }

    private void DrawScatter(){
        int remainder;

       for(int i =0; i < values.size(); i++){
           remainder = i % 3;

           switch(remainder){
               case 0:
                   DrawCircle(i);

                   break;

               case 1:
                   DrawRectangle(i);

                   break;

               case 2:
                   Drawtriangle(i);
                   break;
           }
       }

    }

    private void DrawCircle(int j){
        list_cordinate = new ArrayList<>();

        list_cordinate = StoredCordinate(j);

        for(int i=0; i< list_cordinate.size(); i++) {
            if(color_no >axisFormatter.getColorList().size())
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));
            canvas.drawCircle(list_cordinate.get(i).getX_values(), list_cordinate.get(i).getY_values(), circleSize, paint);
        }

        for(int k =0; k< list_cordinate.size(); k++) {

            canvas.drawText(list_cordinate.get(k).getCordinate(),
                    list_cordinate.get(k).getX_values() - border,
                    list_cordinate.get(k).getY_values(), paint);

        }
        color_code_list.add(color_no);
        color_no += 1;
    }

    private void DrawRectangle(int j){
        list_cordinate = new ArrayList<>();
        list_cordinate = StoredCordinate_rectangle(j);
        for(int i=0;i<list_cordinate.size();i++){
            if(color_no > axisFormatter.getColorList().size())
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));
            canvas.drawRect(list_cordinate.get(i).getLeft(), list_cordinate.get(i).getTop(),
                    list_cordinate.get(i).getRight(), list_cordinate.get(i).getBottom(), paint);
        }
        color_code_list.add(color_no);
        DrawRectangleText(j);

        color_no += 1;
    }

    private void DrawRectangleText(int j){

        for(int k =0; k< list_cordinate.size(); k++) {
            if((list_cordinate.get(k).getTop() -border) > 0) {

                canvas.drawText("("+values.get(j).getList().get(k).getX_values()+
                                ","+values.get(j).getList().get(k).getY_values()+")",
                        list_cordinate.get(k).getLeft() +border,
                        list_cordinate.get(k).getTop()-border, paint);
            } else {

                canvas.drawText("("+values.get(j).getList().get(k).getX_values()+
                                ","+values.get(j).getList().get(k).getY_values()+")",
                        list_cordinate.get(k).getLeft() +border-40,
                        list_cordinate.get(k).getTop()+border, paint);
            }
        }
    }

    private void Drawtriangle(int j){
        list_cordinate = new ArrayList<>();

        list_cordinate = StoredCordinate(j);
        Path path = new Path();

        for(int i=0; i< list_cordinate.size(); i++) {
            if(color_no >axisFormatter.getColorList().size())
                color_no = 0;

            paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color_no)));
            path.moveTo(list_cordinate.get(i).getX_values(), list_cordinate.get(i).getY_values());
            path.lineTo(list_cordinate.get(i).getX_values() - 40, list_cordinate.get(i).getY_values());
            path.lineTo(list_cordinate.get(i).getX_values(), list_cordinate.get(i).getY_values() - 40);
            canvas.drawPath(path, paint);

        }

        for(int k =0; k< list_cordinate.size(); k++) {

            canvas.drawText(list_cordinate.get(k).getCordinate(),
                    list_cordinate.get(k).getX_values() - 25,
                    list_cordinate.get(k).getY_values() - 20, paint);

        }
        color_code_list.add(color_no);
        color_no += 1;
    }

    private  List<ChartData> StoredCordinate(int j){

            list_cordinate = new ArrayList<>();

            for(int i =0; i< values.get(j).getList().size(); i++){

                float x_ratio = (maxX_values / (axisFormatter.getSmallestSize(values)));

                x_cordinate = (colwidth / x_ratio) * values.get(j).getList().get(i).getX_values();
                float line_height = (graphheight / maxY_values) * values.get(j).getList().get(i).getY_values();
                y_cordinate = (border - line_height) + graphheight;
                list_cordinate.add(new ChartData(y_cordinate, x_cordinate + horstart,
                        "(" + values.get(j).getList().get(i).getX_values() + ", " + values.get(j).getList().get(i).getY_values() + ")"));
            }

        return list_cordinate;
    }

    private  List<ChartData> StoredCordinate_rectangle(int i){

            list_cordinate = new ArrayList<>();

            for(int j =0; j< values.get(i).getList().size();j++) {

                float x_ratio = (maxX_values / (axisFormatter.getSmallestSize(values) - 1));
                float barheight = (graphheight / maxY_values) * values.get(i).getList().get(j).getY_values();
                float left = ((colwidth / x_ratio) * values.get(i).getList().get(j).getX_values()) + border;
                float top = (border - barheight) + graphheight;
                float right = ((colwidth / x_ratio) * values.get(i).getList().get(j).getX_values()) + horstart;
                float bottom = (border - barheight) + graphheight + border ;

                list_cordinate.add(new ChartData(left, top, right, bottom));
            }
        return  list_cordinate;
    }

    // Get the Width and Height defined in the activity xml file
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        this.setMeasuredDimension(parentWidth, parentHeight);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
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
