package com.numetriclabz.numandroidcharts;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Environment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AxisFormatter {

    public List<Float> horizontal_width_list = new ArrayList<>();
    float ver_ratio, hor_ratio, border = 30, horstart = border * 2, graphheight, width, horizontal_width;
    float colwidth, maxY_values, maxX_values, graphwidth, height, minY_values;
    int label_size, size;
    Canvas canvas;
    Paint paint, textPaint;
    List<ChartData> values;
    List<String> hori_labels;
    String description, Yaxis_labels;
    private List<String> colorList = new ArrayList<>();
    private  int legendTop,legendLeft, legendRight, legendBottom;
    private Boolean inverseAxis = false;
    private  RectF legends;
    private  boolean chartType;

    // Plot XY Lables
    public void PlotXYLabels(float graphheight,float width,
                             float graphwidth, float height,
                             List<String> hori_labels, float maxY_values, Canvas canvas,
                             List<Float> horizontal_width_list,
                             Paint paint, List<ChartData> values,
                             float maxX_values, String description,
                             boolean inverseAxis,
                             boolean ChartType){

        this.graphheight = graphheight;
        this.width = width;
        this.graphwidth = graphwidth;
        this.height = height;
        this.hori_labels = hori_labels;
        this.canvas = canvas;
        this.horizontal_width_list = horizontal_width_list;
        this.paint = paint;
        this.values = values;
        this.description = description;
        this.maxY_values = maxY_values;
        this.maxX_values = maxX_values;
        this.inverseAxis = inverseAxis;
        minY_values = getMinValues(values);
        this.chartType = ChartType;

        init();
    }

    private void  init(){

        paint.setTextAlign(Paint.Align.LEFT);
        size = values.size();

        label_size = size - 1;
        ver_ratio = maxY_values/size;
        ver_ratio =  (maxY_values + (int) ver_ratio)/size;  // Vertical label ratio
        paint.setColor(Color.BLACK);

        for (int i = 0; i < size+1; i++) {
            paint.setTextSize(18);
            createY_axis(i);

        }

        if(hori_labels != null) {
            size = hori_labels.size();

        }

        if(values.get(0).getLabels() != null){

            for(int j =0; j< size+1 ; j++){

                createX_axis(j);
            }
        }
        else {

            label_size = size - 1;
            hor_ratio = maxX_values/label_size;

            for(int j =0; j< size ; j++){

                createX_axis(j);
            }
        }

        if(description !=null){
            Description();
        }

        paint.setTextSize(18);
    }


	protected void createY_axis(int i){
        
        float ver_height = ((graphheight / (size)) * i) + border;
        if(i== values.size()){
            canvas.drawLine(horstart, ver_height, width - (border), ver_height, paint); // Draw vertical line
        }
        else {
            canvas.drawLine(horstart, ver_height, border, ver_height, paint); // Draw vertical line
        }
 
        paint.setColor(Color.BLACK);
        int Y_labels =  (int) size- i;
        String y_labels = getYaxis_labels(i, Y_labels);

        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(y_labels, horstart - 15, ver_height - 10, paint);
        paint.setTextAlign(Paint.Align.LEFT);
    }

    protected String getYaxis_labels(int i, int Y_labels){


        if(inverseAxis == true && chartType == true){

            Yaxis_labels = String.format("%.1f",(i*ver_ratio) + minY_values);

        }  else if(inverseAxis == true ){

            Yaxis_labels = String.format("%.1f",(i*ver_ratio));

        } else if(chartType == true){

            Yaxis_labels = String.format("%.1f",(Y_labels*ver_ratio) + minY_values);
        }

        else {

            Yaxis_labels = String.format("%.1f", (Y_labels*ver_ratio));
        }
        return  Yaxis_labels;

    }


    protected void createX_axis(int i){

        if(values.get(0).getLabels() != null){

            horizontal_width = ((graphwidth / size) * i) + horstart;
        }
        else {

            horizontal_width = ((graphwidth / label_size) * i) + horstart;
        }

        horizontal_width_list.add(horizontal_width);

        if(i==0){
            canvas.drawLine(horizontal_width, graphheight +border, horizontal_width, border, paint);
        }
        else{
            canvas.drawLine(horizontal_width,graphheight +border, horizontal_width, graphheight + 2*border, paint);
        }

        if(values.get(0).getLabels() != null){

            DrawLabelsString(i);
        }
        else {

            DrawHorizotalLabels(i);
        }
    }


    protected void DrawLabelsString(int i){
        paint.setColor(Color.BLACK);

        if(i >1){

            colwidth = horizontal_width_list.get(1) -  horizontal_width_list.get(0);
            canvas.drawText(values.get(i - 1).getLabels(), horizontal_width - (colwidth - 5), height - 38, paint);

        } else if(i !=0 && i==1){
            canvas.drawText(values.get(i-1).getLabels(), (horizontal_width/3) +10 , height - 38, paint);
        }
    }

    protected void DrawHorizotalLabels(int i){


        paint.setTextAlign(Paint.Align.RIGHT);
        if (i==0)
            paint.setTextAlign(Paint.Align.LEFT);


        String x_values = String.format("%.1f", i * hor_ratio);
        paint.setColor(Color.BLACK);


        if(hori_labels != null){

            canvas.drawText(hori_labels.get(i), horizontal_width-10, height - 38, paint);

        }else {

            canvas.drawText(x_values, horizontal_width -10, height - 38, paint);
        }
    }


    protected void Description(){

        paint.setTextSize(28);
        float text_width = paint.measureText(description, 0, description.length());
        if(values.get(0).getLabels() != null) {
            this.canvas.drawText(description, graphwidth - text_width, height + 50, paint);

        } else {

            this.canvas.drawText(description, graphwidth , height + 50, paint);
        }
    }


    //  return the maximum y_value
    public float getMaxY_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getY_values() > largest)
                largest = values.get(i).getY_values();
        return largest;
    }
    //  return the maximum y_value
    public float getMaxX_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getX_values() > largest)
                largest = values.get(i).getX_values();
        return largest;
    }
    // return the minimum value
    public float getMinValues(List<ChartData> values) {

        float smallest = Integer.MAX_VALUE;
        for (int i = 0; i < values.size(); i++)
            if (values.get(i).getY_values() < smallest)
                smallest = values.get(i).getY_values();
        return smallest;
    }

    public List<String> getColorList(){

        colorList.add("#FF6600");
        colorList.add("#DC143C");
        colorList.add("#40E0D0");
        colorList.add("#A52A2A");
        colorList.add("#9932CC");
        colorList.add("#228B22");
        colorList.add("#FF0000");
        colorList.add("#DAA520");
        colorList.add("#FFA500");
        colorList.add("#A0522D");
        colorList.add("#000000");
       return colorList;
    }

    protected int getLargestSize(List<ChartData> values){

        int largest = Integer.MIN_VALUE;
        int setSize = 0;

        for (int j = 0; j < values.size(); j++) {

            if (values.get(j).getList().size() > setSize) {
                setSize = values.get(j).getList().size();
                largest = j;
            }
        }
        return largest;
    }
    protected int getSmallestSize(List<ChartData> values){
        int smallest = Integer.MAX_VALUE;
        for (int j =0; j < values.size();j++) {
            if (values.get(j).getList().size() < smallest){
                smallest = values.get(j).getList().size();
            }
        }
        return smallest;
    }

    //  return the maximum y_value
    protected float getMultiMaxX_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int j =0; j < values.size();j++) {

            for(int i =0; i< values.get(j).getList().size(); i++){

                if (values.get(j).getList().get(i).getX_values() > largest)
                    largest = values.get(j).getList().get(i).getX_values();
            }

        }
        return largest;
    }

    //  return the maximum y_value
    protected float getMultiMaxY_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        for (int j =0; j < values.size();j++) {

            for(int i =0; i< values.get(j).getList().size(); i++){

                if (values.get(j).getList().get(i).getY_values() > largest)
                    largest = values.get(j).getList().get(i).getY_values();
            }

        }
        return largest;
    }

    public void setLegegendPoint(List<String> legends_list, List<Integer>color_code_list){

        legendTop = (int) height - 10;
        legendLeft = (int) (width * 0.1);
        legendRight = (int) graphwidth;
        legendBottom = (int) height;

        legends = new RectF(legendLeft, legendTop, legendRight, legendBottom);
        Legends(legends_list, color_code_list);

    }

    private void Legends(List<String> legends_list, List<Integer>color_code_list){
        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(20f);

        int left = (int) (graphwidth * 0.1);
        for (int i = 0; i < legends_list.size(); i++){

            String label = legends_list.get(i);

            float text_width = textPaint.measureText(label, 0, label.length());

            int color = color_code_list.get(i);


            if (!((graphwidth - legendLeft) > (text_width + 60))) {

                legendTop -= 60;
                legendLeft = left;
            }

            addLegends(canvas, color, legendTop, legendLeft, legendRight, legendBottom, label);
            legendLeft += ((int)text_width + 60);
        }
    }

    private void addLegends(Canvas canvas, int color, int top, int left, int right, int bottom, String label){

        legends = new RectF(left, top, right, bottom);

        Rect r = new Rect(left, top, left + 30, top + 30);
        paint.setColor(Color.parseColor(getColorList().get(color)));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(r, paint);
        canvas.drawText(label, left + 40, top + 20, textPaint);
    }

    public void saveChart(Bitmap getbitmap, float height, float width){
        File folder = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "charting");
        boolean success = false;
        if (!folder.exists())
        {
            success = folder.mkdirs();
        }


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());

        File file = new File(folder.getPath() + File.separator + "/"+timeStamp+".png");

        if ( !file.exists() )
        {
            try {
                success = file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(success + "file");



        FileOutputStream ostream = null;
        try
        {
            ostream = new FileOutputStream(file);

            System.out.println(ostream);

            Bitmap well = getbitmap; //;
            Bitmap save = Bitmap.createBitmap((int) width, (int) height, Bitmap.Config.ARGB_8888);
            Paint paint = new Paint();
            paint.setColor(Color.WHITE);
            Canvas now = new Canvas(save);
            now.drawRect(new Rect(0,0,(int) width, (int) height), paint);
            now.drawBitmap(well, new Rect(0,0,well.getWidth(),well.getHeight()), new Rect(0,0,(int) width, (int) height), null);


            if(save == null) {
                System.out.println("NULL bitmap save\n");
            }
            save.compress(Bitmap.CompressFormat.PNG, 100, ostream);

        }catch (NullPointerException e)
        {
            e.printStackTrace();
            //Toast.makeText(getApplicationContext(), "Null error", Toast.LENGTH_SHORT).show();
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
           // Toast.makeText(getApplicationContext(), "File error", Toast.LENGTH_SHORT).show();
        }

        catch (IOException e)
        {
            e.printStackTrace();
           // Toast.makeText(getApplicationContext(), "IO error", Toast.LENGTH_SHORT).show();
        }
    }


}
