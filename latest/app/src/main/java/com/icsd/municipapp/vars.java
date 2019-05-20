package com.icsd.municipapp;

import android.app.Application;
import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class vars extends Application
{
    private String reconnectString;
    private int DBuserID;
    private ArrayList<String> layout1,layout2,layout3;
    private String DeviceID;
    private String UserID;
    private Bitmap user_image;
    private Bitmap user_image_temp;
    private String username;
    private String userBDay;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private LatLng reportLocation;
    private int map_type;
    private String report_cat;
    private String report_type;
    private Bitmap image;
    private boolean test;
    private String image_comment;
    private Reports report;
    private String FacebookUserID;
    private String FacebookEmail;
    private String FacebookBDay;
    private String FacebookName;
    private String login_type;
    private String description;

    public void setReconnectString(String reconnectString){
        this.reconnectString=reconnectString;
    }

    public String getReconnectString(){
        return this.reconnectString;
    }

    public void setDescription(String description){
        this.description=description;
    }

    public String getDescription(){
        return this.description;
    }

    public void setDBuserID(int dBuserID){
        this.DBuserID=dBuserID;
    }

    public int getDBuserID(){
        return DBuserID;
    }

    public void setReport(Reports report){
        this.report=report;
    }

    public Reports getReport(){
        return this.report;
    }

    public void setImage_comment(String comment){
        this.image_comment=comment;
    }

    public String getImage_comment(){
        return this.image_comment;
    }

    public void setRead(ObjectInputStream in){
        this.in=in;
    }

    public ObjectInputStream Read(){
        return this.in;
    }

    public void setWrite(ObjectOutputStream out){
        this.out=out;
    }

    public ObjectOutputStream Write(){
        return this.out;
    }

    public LatLng getRepLatLng(){
        return this.reportLocation;
    }

    public void setRepLatLng(LatLng x){
        this.reportLocation=x;
    }

    public void setMap_type(int s){
        this.map_type=s;
    }

    public int getMap_type() {return this.map_type; }

    public void setReport_cat(String x){
        this.report_cat=x;
    }

    public String getReport_cat(){
        return this.report_cat;
    }

    public void setReport_type(String x){
        this.report_type=x;
    }

    public String getReport_type(){
        return this.report_type;
    }

    public void setImage(Bitmap image){
        this.image=image;
    }

    public Bitmap getImage(){
        return this.image;
    }

    public void setTest(boolean test){
        this.test=test;
    }

    public boolean getTest(){
        return this.test;
    }

    public void setFacebookInfo(String facebookUserID,String facebookName,String facebookEmail,String facebookBDay){
        if(facebookUserID!=null)
            FacebookUserID=facebookUserID;
        if(facebookName!=null)
            FacebookName=facebookName;
        if(facebookEmail!=null)
            FacebookEmail=facebookEmail;
        if(facebookBDay!=null)
            FacebookBDay=facebookBDay;
    }

    public String getFacebookInfo(int flag){
        switch (flag)
        {
            case 1:
                return FacebookUserID;
            case 2:
                return FacebookName;
            case 3:
                return FacebookEmail;
            case 4:
                return FacebookBDay;
            default:
                return null;
        }
    }

    public void setLogin_type(String login_type){
        this.login_type=login_type;
    }

    public String getLogin_type(){
        return this.login_type;
    }

    public void setUser_image_temp(Bitmap user_image_temp){
        this.user_image_temp=user_image_temp;
    }

    public Bitmap getUser_image_temp(){
        return user_image_temp;
    }

    public void setUserInfo(String userID,String username,String userBDay,Bitmap user_image){
        if(userID!=null)
            UserID=userID;
        if(username!=null)
            this.username=username;
        if(userBDay!=null)
            this.userBDay=userBDay;
        if(user_image!=null)
            this.user_image=user_image;
    }

    public Object getUserInfo(int flag){
        switch(flag)
        {
            case 1:
                return UserID;
            case 2:
                return username;
            case 3:
                return userBDay;
            case 4:
                return user_image;
            default:
                return null;
        }
    }

    public void setDeviceID(String deviceID){
        DeviceID=deviceID;
    }

    public String getDeviceID(){
        return DeviceID;
    }

    public void setLayout(ArrayList<String> l1,ArrayList<String> l2,ArrayList<String> l3)
    {
        layout1=l1;
        layout2=l2;
        layout3=l3;
    }

    public ArrayList<String> getLayout(int flag){
        switch(flag){
            case 1:
                return layout1;
            case 2:
                return layout2;
            case 3:
                return layout3;
        }
        return null;
    }
}


