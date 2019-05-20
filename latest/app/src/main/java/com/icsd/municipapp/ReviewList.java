package com.icsd.municipapp;


import android.graphics.Bitmap;

public class ReviewList {
    public int report_id;
    public String display_text;
    public String date;
    public String address;
    public Bitmap streetView;

    ReviewList(int report_id,String display_text,String date,String address,Bitmap streetView)
    {
        this.report_id=report_id;
        this.display_text=display_text;
        this.date=date;
        this.address=address;
        this.streetView=streetView;
    }



}
