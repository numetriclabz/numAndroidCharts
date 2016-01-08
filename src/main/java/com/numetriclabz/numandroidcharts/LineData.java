package com.numetriclabz.numandroidcharts;


import android.app.Application;

import java.io.Serializable;

public class LineData extends Application implements Serializable {

        private static final long serialVersionUID = 1L;

        public Float y_values, x_values;

        public LineData(Float y_values, Float x_values){
            this.y_values = y_values;
            this.x_values = x_values;
        }

        public Float getY_values(){return y_values;}

        public Float getX_values(){ return x_values;}

        public void setY_values(Float y_values){ this.y_values = y_values;}

        public  void setX_values(Float x_values){ this.x_values = x_values;}

}


