package com.numetriclabz.numandroidcharts;


import android.app.Application;
import android.graphics.Path;
import android.graphics.Region;

import java.io.Serializable;

public class ChartData extends Application implements Serializable {

        private static final long serialVersionUID = 1L;

        private Float y_values, x_values, size, left, top, right, bottom, data, mValue;
        private final Path mPath = new Path();
        private final Region mRegion = new Region();
        private String cordinate;

        public ChartData(){}
    
        public ChartData(Float y_values, Float x_values){
            this.y_values = y_values;
            this.x_values = x_values;
        }

        public ChartData(Float y_values, Float x_values, Float size){
            this.y_values = y_values;
            this.x_values = x_values;
            this.size = size;

        }

        protected ChartData(Float left, Float top, Float right,Float bottom){
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;

        }

        protected ChartData(Float y_axis, Float x_axis, Float size,String cordinate){
            this.y_values = y_axis;
            this.x_values = x_axis;
            this.size = size;
            this.cordinate = cordinate;

        }

         public ChartData(Float val){

            this.data = val;
        }

        public Float getY_values(){return y_values;}

        public Float getX_values(){ return x_values;}

        public void setY_values(Float y_values){ this.y_values = y_values;}

        public  void setX_values(Float x_values){ this.x_values = x_values;}

        public  void setSize(Float size){ this.size = size;}

        public Float getSize(){ return  size; }

        public  void setLeft(Float left){ this.left = left;}

        public Float getLeft(){ return  left; }

        public  void setTop(Float top){ this.top = top;}

        public Float getTop(){ return  top; }

        public  void setRight(Float right){ this.right = right;}

        public Float getRight(){ return  right; }

        public  void setBottom(Float bottom){ this.bottom = bottom;}

        public Float getBottom(){ return  bottom; }

        public  void setValue(Float data){ this.data = data;}

        public Float getValue(){ return  this.data; }

        public  void setCordinate(String cordinate){ this.cordinate = cordinate;}

        public String getCordinate(){ return  this.cordinate; }


        public Path getPath() {
            return mPath;
        }

        public Region getRegion() {
            return mRegion;
        }

        public float getSectorValue() {
            return mValue;
        }

        public void setSectorValue(float value) {
            mValue = value;
        }

}


