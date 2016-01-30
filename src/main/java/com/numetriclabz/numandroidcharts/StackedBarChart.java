package com.numetriclabz.numandroidcharts;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StackedBarChart extends View {

    private Paint paint, textPaint;
    private List<ChartData>  values;
    private List<String> hori_labels;
    private List<Float> horizontal_width_list = new ArrayList<>();
    private String description;
    private float horizontal_width,  border = 30, horstart = border * 2;
    private int parentHeight ,parentWidth;
    private float lastheight;
    private int color_no = 0;;
    private Canvas canvas;
    private List<ChartData> list_cordinate = new ArrayList<>();
    private float height ,width, maxY_values, maxX_values, min, graphheight, graphwidth;
    private float left, right, top, bottom, barheight1, barheight2,colwidth, verheight;
    private List<Integer> color_code_list = new ArrayList<>();
    JSONObject jsonObject, rangeJsonObject;
    private  List<String> legends_list = new ArrayList<>();
    private  int legendTop,legendLeft, legendRight, legendBottom;
    private  RectF legends;
    private boolean percentage_stacked = false;
    private Boolean horizontalStacked = false;
    private Float[] cordinate_range = null;

    private List<Float> vertical_height_list = new ArrayList<>();


    public StackedBarChart(Context context, AttributeSet attributeSet){
        super(context, attributeSet);

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
    
    
	public void setHorizontalStckedBar(boolean stckedBar){
        if(horizontalStacked != null){
            this.horizontalStacked = stckedBar;
        }
    }

    public void setLabels(List<String> hori_labels){

        if (hori_labels != null)
            this.hori_labels = hori_labels;
    }

    public void setPercentageStacked(boolean perc){
        this.percentage_stacked = perc;
    }

    public void setRange(Float[] cordinate_range){
        this.cordinate_range = cordinate_range;

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
        
        if(horizontalStacked == false) {

            StackAxisformatter axisFormatter = new StackAxisformatter();
            axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                    horstart, border, horizontal_width_list, horizontal_width, paint, values.get(0).getY_List(),
                    maxX_values, description, percentage_stacked);

            colwidth = horizontal_width_list.get(1) - horizontal_width_list.get(0);
            getBarheight();
            StoredCordinate_Vertical();

        }
        else {

            H_StackAxisformatter axisFormatter = new H_StackAxisformatter();
            axisFormatter.PlotXYLabels(graphheight, width, graphwidth, height, hori_labels, maxY_values, canvas,
                    vertical_height_list, paint, values.get(0).getY_List(),
                    maxX_values, description, percentage_stacked);

            verheight = vertical_height_list.get(2) - vertical_height_list.get(1);
            getBarheight();
            StoredCordinate_horizontal();
        }

        if(!percentage_stacked)
            DrawText();

        setLegegendPoint(getLegends_list(), getColor_code_list());
    }

    private  void StoredCordinate_Vertical(){
       if(cordinate_range == null){

           storedWithoutRange_vertical();

       } else {
           storedRangeCordinate_Vertical();
       }

    }

    private  void storedRangeCordinate_Vertical(){
        getRangeHeight();

        AxisFormatter axisFormatter = new AxisFormatter();

        for(int i =0;i<values.get(0).getY_List().length  ;i++){

            for(int j=0; j< values.size();j++){

                left = (i * colwidth) + horstart;

                try {
                    String str = jsonObject.optString(i + "");
                    str = str.replace("[","").replace("]", "");

                    String rangestr = rangeJsonObject.optString(i + "");
                    rangestr = rangestr.replace("[","").replace("]", "");

                    List<String> rangeItems = Arrays.asList(rangestr.split(","));
                    Float rangeBarheight = Float.parseFloat(rangeItems.get(j));

                    List<String> items = Arrays.asList(str.split(","));
                    Float barheight = Float.parseFloat(items.get(j));
                    if(percentage_stacked ==false){
                        lastheight = (border - barheight) + graphheight -rangeBarheight ;

                    } else{
                        lastheight = (border - barheight) + graphheight ;
                    }

                    right = ((i * colwidth) + horstart) + (colwidth - 1);

                    if(j == 0){

                        top = lastheight;
                        bottom = graphheight + border - Float.parseFloat(rangeItems.get(j));
                    }
                    else {

                        top = top- barheight ;
                        if(cordinate_range == null){
                            bottom = graphheight -Float.parseFloat(items.get(j-1)) -
                                    Float.parseFloat(rangeItems.get(j))+ border;
                        } else {

                            bottom = graphheight -Float.parseFloat(items.get(j-1))
                                    + border;
                        }

                    }
                    paint.setColor(Color.parseColor(axisFormatter.getColorList().get(j)));
                    canvas.drawRect(left, top, right, bottom, paint);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            list_cordinate.add(new ChartData(left,top));
        }
    }

    private void storedWithoutRange_vertical(){

        AxisFormatter axisFormatter = new AxisFormatter();

        for(int i =0;i<values.get(0).getY_List().length  ;i++){

            for(int j=0; j< values.size();j++){

                left = (i * colwidth) + horstart;

                try {
                    String str = jsonObject.optString(i + "");
                    str = str.replace("[","").replace("]", "");

                    List<String> items = Arrays.asList(str.split(","));
                    Float barheight = Float.parseFloat(items.get(j));

                    lastheight = (border - barheight) + graphheight;
                    right = ((i * colwidth) + horstart) + (colwidth - 1);

                    if(j == 0){

                        top = lastheight;
                        bottom = graphheight + border;
                    }
                    else {

                        top = top- barheight ;
                        bottom = graphheight -Float.parseFloat(items.get(j-1)) + border;
                    }
                    paint.setColor(Color.parseColor(axisFormatter.getColorList().get(j)));
                    canvas.drawRect(left, top, right, bottom, paint);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            list_cordinate.add(new ChartData(left,top));
        }
    }

    private  void StoredCordinate_horizontal(){

      if (cordinate_range != null)
          store_cordinate_withRange();
        else
          storeCordinateWithoutRange();

    }

    private void store_cordinate_withRange(){
        getRangeHeight();
        AxisFormatter axisFormatter = new AxisFormatter();
        for(int i =0;i<values.get(0).getY_List().length  ;i++){

            for(int j=0; j< values.size();j++){

                try {

                    String str = jsonObject.optString(i + "");
                    str = str.replace("[","").replace("]", "");

                    List<String> items = Arrays.asList(str.split(","));
                    Float barheight = Float.parseFloat(items.get(j));

                    String rangestr = rangeJsonObject.optString(i + "");
                    rangestr = rangestr.replace("[","").replace("]", "");

                    List<String> rangeItems = Arrays.asList(rangestr.split(","));
                    Float rangeBarheight = Float.parseFloat(rangeItems.get(j));
                    if(percentage_stacked == true)
                        lastheight = barheight + horstart;
                    else
                        lastheight = barheight + horstart + rangeBarheight;

                    top = graphheight - vertical_height_list.get(i)  - verheight +horstart;
                    bottom =  top + verheight -10;

                    if(j ==0){

                        left = horstart + rangeBarheight;
                        right = lastheight;
                    }
                    else {

                        left = right ;
                        right = Float.parseFloat(items.get(j))+right ;
                    }

                    paint.setColor(Color.parseColor(axisFormatter.getColorList().get(j)));
                    canvas.drawRect(left, top, right, bottom, paint);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            list_cordinate.add(new ChartData(right + 10, bottom - 10));
        }
    }

    private void storeCordinateWithoutRange(){
        AxisFormatter axisFormatter = new AxisFormatter();
        for(int i =0;i<values.get(0).getY_List().length  ;i++){

            for(int j=0; j< values.size();j++){

                try {

                    String str = jsonObject.optString(i + "");
                    str = str.replace("[","").replace("]", "");

                    List<String> items = Arrays.asList(str.split(","));
                    Float barheight = Float.parseFloat(items.get(j));

                    lastheight = barheight + horstart;

                    top = graphheight - vertical_height_list.get(i)  - verheight +horstart;
                    bottom =  top + verheight -10;

                    if(j ==0){

                        left = horstart;
                        right = lastheight;
                    }
                    else {

                        left = right ;
                        right = Float.parseFloat(items.get(j))+right ;
                    }

                    paint.setColor(Color.parseColor(axisFormatter.getColorList().get(j)));
                    canvas.drawRect(left, top, right, bottom, paint);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

            list_cordinate.add(new ChartData(right + 10, bottom - 10));
        }
    }



    private void getBarheight(){

        try {

            int size = values.get(0).getY_List().length;
            jsonObject = new JSONObject();

            for (int j = 0; j < size; j++) {

                List<Float> barheight_list1 = new ArrayList<>();

                if(horizontalStacked && percentage_stacked)
                    barheight_list1 = percentage_width(barheight_list1, j);

                else if(horizontalStacked)
                    barheight_list1 = stacked_width(barheight_list1, j);

                else if(percentage_stacked)
                    barheight_list1 = percentage_height(barheight_list1, j);

                else
                	barheight_list1 = stacked_height(barheight_list1, j);


                jsonObject.put(j + "", barheight_list1.toString());

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getRangeHeight(){
        try {

            int size = cordinate_range.length;
            rangeJsonObject = new JSONObject();
            for (int j = 0; j < size; j++) {
                List<Float> barheight_list1 = new ArrayList<>();
                barheight_list1 = range_height(barheight_list1, j);
                rangeJsonObject.put(j + "", barheight_list1.toString());

            }

        } catch (Exception e){
            e.printStackTrace();
        }
    }


    private List<Float> percentage_height(List<Float> barHeightList, int num){

        float total = getPercentageTotal(num);
        for (int i = 0; i < values.size(); i++) {

            float barheight1 = (graphheight/total) * values.get(i).getY_List()[num] ;
            barHeightList.add(barheight1);
        }

        return barHeightList;
    }


    private float getPercentageTotal( int num){

        float total = 0f;

        for (int i = 0; i < values.size(); i++) {
            total += values.get(i).getY_List()[num] ;
        }
        return total;
    }


    private List<Float> stacked_height(List<Float> barHeightList, int num){

        for (int i = 0; i < values.size(); i++) {

            float barheight1 = (graphheight/maxY_values) * values.get(i).getY_List()[num] ;
            barHeightList.add(barheight1);
        }
        return barHeightList;
    }

    private List<Float> range_height(List<Float> barHeightList, int num){

        for (int i = 0; i < cordinate_range.length; i++) {

            float barheight1 = (graphheight/maxY_values) * cordinate_range[num] ;
            barHeightList.add(barheight1);
        }
        return barHeightList;
    }

    private List<Float> stacked_width(List<Float> barWidthList, int num){

        for (int i = 0; i < values.size(); i++) {

            float barwidth1 = (graphwidth/maxY_values)*values.get(i).getY_List()[num] ;
            barWidthList.add(barwidth1);
        }
        return barWidthList;
    }


    private List<Float> percentage_width(List<Float> barWidthList, int num){

        float total = getPercentageTotal(num);

        for (int i = 0; i < values.size(); i++) {

            float barwidth = (graphwidth/total) * values.get(i).getY_List()[num] ;
            barWidthList.add(barwidth);
        }

        return barWidthList;
    }


    private void DrawText() {

        Float total_number = 0f;

        for(int i=0; i< list_cordinate.size(); i++){

            total_number = 0f;
            for(int j=0; j< values.size();j++){
                total_number += values.get(j).getY_List()[i];

            }
            if(cordinate_range != null)
                total_number += cordinate_range[i];

            canvas.drawText(Float.toString(total_number),
                    list_cordinate.get(i).getY_values() + border,
                    list_cordinate.get(i).getX_values() - 30, paint);
        }

    }

    private List<String> getLegends_list(){

        for (int i = 0; i < values.size(); i++) {

            legends_list.add(values.get(i).getLegends());
        }
        return legends_list;
    }

    private void intilaizeValue(Canvas canvas){

        height = parentHeight -60;
        width = parentWidth;
        if(cordinate_range == null) {
            maxY_values = getMaxY_Values(values);

        } else {

            Float temp_maxYaxis = getMaxY_Values(values);
            maxY_values = getRangeMaxY_Value(temp_maxYaxis);

        }


        // min = axisFormatter.getMinValues(values);
        graphheight = height - (3 * border);
        graphwidth = width - (3 * border);
        this.canvas = canvas;
    }

    public List<Integer> getColor_code_list(){
        for (int i =0; i< values.size(); i++){
            color_code_list.add(i);
        }
        return color_code_list;
    }

    public float getMaxY_Values(List<ChartData> values) {

        float largest = Integer.MIN_VALUE;
        float largest1 = 0;

        for (int i = 0; i < values.size(); i++) {

            for (int j = 0; j < values.get(i).getY_List().length; j++){
                if (values.get(i).getY_List()[j] > largest)
                largest = values.get(i).getY_List()[j];
            }
            largest1 +=largest;
        }
        return largest1;
    }

    public float getRangeMaxY_Value(float maxY_values){
        float largest = Integer.MIN_VALUE;


        for (int j = 0; j < cordinate_range.length; j++){
                if (cordinate_range[j] > largest)
                    largest = cordinate_range[j];
        }
        maxY_values +=largest;


        return maxY_values;
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
        AxisFormatter axisFormatter = new AxisFormatter();
        Rect r = new Rect(left, top, left + 30, top + 30);
        paint.setColor(Color.parseColor(axisFormatter.getColorList().get(color)));
        canvas.drawRect(r, paint);
        canvas.drawText(label, left + 40, top + 20, textPaint);
    }


}
