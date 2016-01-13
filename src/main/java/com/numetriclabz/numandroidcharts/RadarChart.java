package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class RadarChart extends View {

    int pointNum = 0;
    private Paint paint, webPaint, coPaint, textPaint;
    private Path path, webPath, coPath;
    private JSONObject values;
    private List<String>labels;
    private List<ChartData> data;
    Context context;
    RectF legends;


    public RadarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public void init(){

        paint = new Paint();
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(3);
        paint.setStyle(Paint.Style.STROKE);

        coPaint = new Paint();
        coPaint.setColor(Color.GRAY);
        coPaint.setStrokeWidth(1);
        coPaint.setStyle(Paint.Style.STROKE);

        webPaint = new Paint();

        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20f);

        path = new Path();
        coPath = new Path();
        webPath = new Path();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        pointNum = calculatePoints();

        int w = Math.min(getWidth(), getHeight());
        int h = Math.min(getWidth(), getHeight());

        int legendTop = getHeight() -60;
        int legendLeft = 60;
        int legendRight = getWidth();
        int legendBottom = getHeight();

        legends = new RectF(legendLeft, legendTop, legendRight, legendBottom);

        if((w == 0) || (h == 0)){
            return;
        }

        float x = (float)w/2.0f;
        float y = (float)h/2.0f;
        float radius;

        radius = (w-(w*0.2f)) * 0.5f;

        drawRadar(canvas, x, y, radius, pointNum);
        drawCoordinates(x, y, radius, pointNum);

        canvas.drawPath(path, paint);
        canvas.drawPath(coPath, coPaint);

        processWeb(canvas, radius, x, y, legendLeft, legendTop, legendRight, legendBottom);
    }


    public void processWeb(Canvas canvas,float radius, float x, float y, int legendLeft, int legendTop, int legendRight, int legendBottom){

        Iterator<?> keys = values.keys();

        while( keys.hasNext() ) {

            String key = (String)keys.next();

            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

            webPaint.setColor(color);
            webPaint.setStrokeWidth(2);
            webPaint.setStyle(Paint.Style.STROKE);

            try {

                String str = values.getString(key);
                str = str.replace("[","").replace("]", "");

                List<String> val = new ArrayList<String>(Arrays.asList(str.split(",")));

                drawWeb(canvas, x, y, radius, pointNum, val);
                canvas.drawPath(webPath, webPaint);

                float text_width = textPaint.measureText(key, 0, key.length());

                if(!((getWidth() - legendLeft) > (text_width + 60))){

                    legendTop -= 60;
                    legendLeft = 60;
                }
                addLegends(canvas, color, legendTop, legendLeft, legendRight, legendBottom, key);
                legendLeft += ((int)text_width + 60);
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }


    private void addLegends(Canvas canvas, int color,  int top, int left, int right, int bottom, String label){

        legends = new RectF(left, top, right, bottom);

        Rect r = new Rect(left, top, left + 30, top + 30);
        webPaint.setColor(color);
        webPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(r, webPaint);
        canvas.drawText(label, left + 40, top + 20, textPaint);
    }


    public void drawWeb(Canvas canvas, float x, float y, float radius, int points, List<String> val){

        double section = 2.0 * Math.PI/points;

        float rad = (radius / points) * Float.parseFloat(val.get(0));

        webPath.reset();
        webPath.moveTo(
                (float) (x + rad * Math.cos(0)),
                (float) (y + rad * Math.sin(0)));

        for(int i = 0; i < points; i++){

            float radi = (radius / points) * Float.parseFloat(val.get(i));

            webPath.lineTo(
                    (float) (x + radi * Math.cos(section * i)),
                    (float) (y + radi * Math.sin(section * i)));

            canvas.drawText(val.get(i),
                    (float) (x + radi * Math.cos(section * i)),
                    (float) (y + radi * Math.sin(section * i)), textPaint);
        }

        webPath.close();
    }


    public void drawRadar(Canvas canvas, float x, float y, float radius, int pts){

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();


        double section = 2.0 * Math.PI/pts;

        int sWidth = display.getWidth();

        path.reset();
        path.moveTo(
                (float) (x + radius * Math.cos(0)),
                (float) (y + radius * Math.sin(0)));

        for(int i = 0; i < pts; i++){

            path.lineTo(
                    (float) (x + radius * Math.cos(section * i)),
                    (float) (y + radius * Math.sin(section * i)));

            float x_axis = (float) (x + radius * Math.cos(section * i));
            float y_axis = (float) (y + radius * Math.sin(section * i));

            if(y_axis > (y/2)){
                y_axis += 30f;
            }
            else{
                y_axis -= 20f;
            }

            float text_width = textPaint.measureText(labels.get(i), 0, labels.get(i).length());

            if((sWidth - x_axis) > (text_width + 20f) && x_axis > (text_width+20f)){

                canvas.drawText(labels.get(i), x_axis, y_axis, textPaint);
            }
            else if(x_axis > (text_width + 20f)){

                canvas.save();
                canvas.rotate((float) 90, x_axis, y_axis );
                canvas.drawText(labels.get(i), x_axis - ((text_width/2) + 20f), y_axis - 20f, textPaint);
                canvas.restore();
            }
            else {
                canvas.save();
                canvas.rotate((float) 90, x_axis, y_axis );
                canvas.drawText(labels.get(i), x_axis - ((text_width/2) + 20f), y_axis + 30f, textPaint);
                canvas.restore();
            }
        }

        path.close();
    }


    public void drawCoordinates(float x, float y, float radius, int points){

        double section = 2.0 * Math.PI/points;

        coPath.reset();

        for(int i = 0; i < points; i++){

            coPath.moveTo(x, y);

            coPath.lineTo(
                    (float) (x + radius * Math.cos(section * i)),
                    (float) (y + radius * Math.sin(section * i)));
        }
        coPath.close();
    }

    public void getData() throws JSONException{

        JSONObject obj = new JSONObject();

        for(int i = 0; i < this.data.size(); i++){
            obj = this.data.get(i).getRadarData();
        }

        String str = obj.getString("labels");
        str = str.replace("[","").replace("]", "");
        labels = new ArrayList<String>(Arrays.asList(str.split(",")));
        String val = obj.getString("values");

        values = new JSONObject(val);
    }

    public void setData(List<ChartData> data){

        this.data = data;
        invalidate();
        try {
            getData();
        }
        catch (JSONException e){
            e.printStackTrace();
        }
    }

    public int calculatePoints(){
        return labels.size();
    }
}

