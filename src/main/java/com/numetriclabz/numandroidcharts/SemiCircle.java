package com.numetriclabz.numandroidcharts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.Random;

public class SemiCircle extends View {

    private int mInnerCircleRatio;
    private ArrayList<ChartData> mSlices = new ArrayList<ChartData>();
    private Paint mPaint = new Paint();
    private RectF mRectF = new RectF();
    private boolean mSemi;
    private float mCurrentAngle;
    private int mFullSweep, width, height;
    private int legendTop, legendLeft, legendRight, legendBottom;

    Paint textPaint, legendPaint;
    RectF legends;


    public SemiCircle(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SemiCircle, 0, 0);

        try {
            mInnerCircleRatio = a.getInt(R.styleable.SemiCircle_semiInnerCircleRatio, 0);
            mSemi = a.getBoolean(R.styleable.SemiCircle_semi_circle, false);
        }
        finally {
            a.recycle();
        }

        init();
    }


    private void init(){

        if(mSemi){
            mFullSweep = 180;
            mCurrentAngle = 180;
        }
        else{
            mFullSweep = 360;
            mCurrentAngle = 270;
        }

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20f);
        legendPaint = new Paint();

        setScreenMeasure();
    }

    private void setScreenMeasure(){

        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        width = display.getWidth();
        height = display.getHeight();

        legendTop = height -120;
        legendLeft = 60;
        legendRight = width;
        legendBottom = height;

        legends = new RectF(legendLeft, legendTop, legendRight, legendBottom);
    }


    protected void onDraw(Canvas canvas) {

        float midX, midY, radius, innerRadius;

        canvas.drawColor(Color.TRANSPARENT);
        mPaint.reset();
        mPaint.setAntiAlias(true);

        float currentSweep = 0;
        float totalValue = 0;

        midX = Math.min(getWidth() / 2, getHeight()/2);
        midY = Math.min(getWidth() / 2, getHeight()/2);

        if (midX < midY)
            radius = midX;
        else
            radius = midY;

        innerRadius = radius * mInnerCircleRatio / 255;

        for (ChartData slice : mSlices) {
            totalValue += slice.getSectorValue();
        }

        for (ChartData slice : mSlices) {

            Path p = slice.getPath();
            p.reset();

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            mPaint.setColor(color);

            currentSweep = (slice.getSectorValue() / totalValue) * (mFullSweep);

            mRectF.set(midX - radius, midY - radius, midX + radius, midY + radius);
            createArc(p, mRectF, currentSweep, mCurrentAngle, currentSweep);
            mRectF.set(midX - innerRadius, midY - innerRadius, midX + innerRadius, midY + innerRadius);
            createArc(p, mRectF, currentSweep, mCurrentAngle + currentSweep, -currentSweep);

            p.close();


            Region r = slice.getRegion();
            r.set((int) (midX - radius),
                    (int) (midY - radius),
                    (int) (midX + radius),
                    (int) (midY + radius));

            canvas.drawPath(p, mPaint);
            mCurrentAngle = mCurrentAngle + currentSweep;


            try{
                addLabel(slice, canvas, color);
            }
            catch (NullPointerException e){
                e.printStackTrace();
            }

        }
    }


    private void addLabel(ChartData slice, Canvas canvas, int color){

        String label = slice.getSectorLabel();
        float text_width = textPaint.measureText(label, 0, label.length());

        if(!((width - legendLeft) > (text_width + 60))){

            legendTop -= 60;
            legendLeft = 60;
        }

        addLegends(canvas, color, legendTop, legendLeft, legendRight, legendBottom, label);
        legendLeft += ((int)text_width + 60);
    }


    private void createArc(Path p, RectF mRectF, float currentSweep, float startAngle, float sweepAngle) {

        if (currentSweep == mFullSweep)
            p.addArc(mRectF, startAngle, sweepAngle);
         else
            p.arcTo(mRectF, startAngle, sweepAngle);
    }

    private void addLegends(Canvas canvas, int color, int top, int left, int right, int bottom, String label){

        legends = new RectF(left, top, right, bottom);

        Rect r = new Rect(left, top, left + 30, top + 30);
        mPaint.setColor(color);
        canvas.drawRect(r, mPaint);
        canvas.drawText(label, left + 40, top + 20, textPaint);
    }


    public void setInnerCircleRatio(int innerCircleRatio) {
        mInnerCircleRatio = innerCircleRatio;
        postInvalidate();
    }


    public void addSector(ChartData sector) {
        mSlices.add(sector);
        postInvalidate();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // Try for a width based on our minimum
        int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        int width = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        //int minh = (width -10)  + getPaddingBottom() + getPaddingTop();
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

}
