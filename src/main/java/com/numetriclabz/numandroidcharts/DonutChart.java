package com.numetriclabz.numandroidcharts;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class DonutChart extends View {

    private int mInnerCircleRatio;
    private ArrayList<ChartData> mSlices = new ArrayList<ChartData>();
    private Paint mPaint = new Paint();
    private RectF mRectF = new RectF();

    public DonutChart(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.DonutChart, 0, 0);
        mInnerCircleRatio = a.getInt(R.styleable.DonutChart_pieInnerCircleRatio, 0);
    }

    public void onDraw(Canvas canvas) {
        float midX, midY, radius, innerRadius;

        canvas.drawColor(Color.TRANSPARENT);
        mPaint.reset();
        mPaint.setAntiAlias(true);

        float currentAngle = 270;
        float currentSweep = 0;
        float totalValue = 0;

        midX = getWidth() / 2;
        midY = getHeight() / 2;

        if (midX < midY) {
            radius = midX;
        } else {
            radius = midY;
        }

        innerRadius = radius * mInnerCircleRatio / 255;

        for (ChartData slice : mSlices) {
            totalValue += slice.getSectorValue();
        }


        for (ChartData slice : mSlices) {
            Path p = slice.getPath();
            p.reset();

            Random rnd = new Random();
            int color = Color.argb(255, (int) slice.getSectorValue(), rnd.nextInt(256), rnd.nextInt(256));

            mPaint.setColor(color);

            currentSweep = (slice.getSectorValue() / totalValue) * (360);

            mRectF.set(midX - radius, midY - radius, midX + radius, midY + radius);
            createArc(p, mRectF, currentSweep, currentAngle, currentSweep);
            mRectF.set(midX - innerRadius, midY - innerRadius, midX + innerRadius, midY + innerRadius);
            createArc(p, mRectF, currentSweep, currentAngle  + currentSweep ,-currentSweep);

            p.close();

            // Create selection region
            Region r = slice.getRegion();
            r.set((int) (midX - radius),
                    (int) (midY - radius),
                    (int) (midX + radius),
                    (int) (midY + radius));
            canvas.drawPath(p, mPaint);
            currentAngle = currentAngle + currentSweep;
        }
    }

    private void createArc(Path p, RectF mRectF, float currentSweep, float startAngle, float sweepAngle) {
        if (currentSweep == 360) {
            p.addArc(mRectF, startAngle, sweepAngle);
        } else {
            p.arcTo(mRectF, startAngle, sweepAngle);
        }
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
