package com.numetriclabz.numandroidcharts;


import android.app.Application;

import java.io.Serializable;

public class BubbleData extends Application implements Serializable {

    private static final long serialVersionUID = 1L;

    public Float values;
    Float size;

    public BubbleData(Float values, Float size){
        this.values = values;
        this.size = size;
    }

    public Float getSize(){return size;}

    public Float getValues(){ return values;}

    public void setValues(Float values){ this.values = values;}

    public  void setSize(Float size){ this.size = size;}

}
