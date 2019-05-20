package com.icsd.municipapp;


import android.graphics.Bitmap;

public class CommentList {
    public int comment_id;
    public int level;
    public int user_id;
    public String comment_text;
    public String username;
    public Bitmap user_image;
    public String facebook_id;
    public String date;


    CommentList(int comment_id,int user_id,String comment_text,int level,String username,Bitmap user_image,String facebook_id,String date)
    {
        this.comment_id=comment_id;
        this.user_id=user_id;
        this.comment_text=comment_text;
        this.username=username;
        this.user_image=user_image;
        this.facebook_id=facebook_id;
        this.date=date;
        this.level=level;
    }
}
