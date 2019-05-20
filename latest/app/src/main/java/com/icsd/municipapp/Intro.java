package com.icsd.municipapp;

import android.Manifest;
import android.accounts.NetworkErrorException;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class Intro extends AppCompatActivity
{
    ObjectOutputStream out;
    ObjectInputStream in;

    private static final String[] PERMISSIONS={
            android.Manifest.permission_group.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission_group.LOCATION,
            android.Manifest.permission.INTERNET,
            android.Manifest.permission.ACCESS_WIFI_STATE,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.READ_PHONE_STATE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        setTitle(getString(R.string.intro_title));

        if(checkPermission()!=0)
            request_permissions();

        new Connect().execute(getIntent().getExtras().getString("loginType"));
        String loginType = getIntent().getExtras().getString("loginType");

        ((vars)this.getApplicationContext()).setLogin_type(loginType);

    }












    private class Connect extends AsyncTask<String, String, String>
    {

        ProgressBar progressBar;
        TextView textIntro;
        String old;

        @Override
        protected void onPreExecute()
        {
            progressBar = (ProgressBar)findViewById(R.id.progressBarIntro);
            textIntro = (TextView) findViewById(R.id.progressIntro);
            progressBar.setProgress(0);
            progressBar.setMax(5);

        }

        @Override
        protected String doInBackground(String... loginType) {
            try
            {
                while(checkPermission()!=0) {
                    System.out.println("sleeping... Permission: "+checkPermission());
                    Thread.sleep(100);
                }

                publishProgress("Getting device ID...");
                //gettting device identifier
                try {
                    @SuppressLint("HardwareIds")
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String id = telephonyManager.getDeviceId();

                    System.out.println(id);
                    System.out.println(id);
                    System.out.println(id);

                    //Generating users id
                    MessageDigest md = MessageDigest.getInstance("MD5");
                    md.update(id.getBytes());
                    ((vars) Intro.this.getApplication()).setDeviceID((id));
                }catch(NullPointerException |NoSuchAlgorithmException ex){((vars) Intro.this.getApplication()).setDeviceID(("Emulator id"));}

                publishProgress("Creating DataBase...");
                DataBase db = new DataBase(Intro.this);
               // ((vars)Intro.this.getApplicationContext()).setDataBase();




                if(loginType[0].equals("facebook"))
                {
                    publishProgress("Establishing connections with server...");
                    try{
                        Socket socket = new Socket("83.212.111.82", 4444);
                        System.out.println("got the socket");

                        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                        ((vars)Intro.this.getApplicationContext()).setWrite(out);
                        //Set inputStream
                        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                        ((vars)Intro.this.getApplicationContext()).setRead(in);



                        publishProgress("Checking if user is banned...");
                        //Check if user is banned
                        ((vars) Intro.this.getApplicationContext()).Write().writeObject("CONNECT");
                        ((vars) Intro.this.getApplicationContext()).Write().flush();
                        //Write deviceID
                        ((vars) Intro.this.getApplicationContext()).Write().writeObject(((vars) Intro.this.getApplicationContext()).getDeviceID());
                        ((vars) Intro.this.getApplicationContext()).Write().flush();
                        //Write Facebook ID
                        ((vars) Intro.this.getApplicationContext()).Write().writeObject(((vars) Intro.this.getApplicationContext()).getFacebookInfo(1));
                        ((vars) Intro.this.getApplicationContext()).Write().flush();

                        old=(String)((vars) Intro.this.getApplicationContext()).Read().readObject();
                        if("YES".equals(old))
                        {
                            //write name
                            ((vars) Intro.this.getApplicationContext()).Write().writeObject(((vars) Intro.this.getApplicationContext()).getFacebookInfo(2));
                            ((vars) Intro.this.getApplicationContext()).Write().flush();
                            //write email
                            ((vars) Intro.this.getApplicationContext()).Write().writeObject(((vars) Intro.this.getApplicationContext()).getFacebookInfo(3));
                            ((vars) Intro.this.getApplicationContext()).Write().flush();
                            //write birthday
                            ((vars) Intro.this.getApplicationContext()).Write().writeObject(((vars) Intro.this.getApplicationContext()).getFacebookInfo(4));
                            ((vars) Intro.this.getApplicationContext()).Write().flush();
                        }
                        String banned = (String)((vars) Intro.this.getApplicationContext()).Read().readObject();
                        if("NO".equalsIgnoreCase(banned)) {
                            //Read DBuserID
                            ((vars) Intro.this.getApplicationContext()).setDBuserID((int) ((vars) Intro.this.getApplicationContext()).Read().readObject());
                            //Read Recon id
                            ((vars) Intro.this.getApplicationContext()).setReconnectString((String) ((vars) Intro.this.getApplicationContext()).Read().readObject());
                        }
                        return banned;
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Intro.this);
                        builder.setTitle("Connection error");
                        builder.setMessage("Internet connection is required. Connect and press ok!!");
                        builder.setIcon(R.drawable.conn_refused);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                startActivity(new Intent(Intro.this,Intro.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("loginType","facebook"));
                                dialog.dismiss();
                            }
                        });
                        builder.show();
                    }


                }
                else if(loginType[0].equals("app"))
                {
                    try {
                        publishProgress("Checking if user is banned...");
                        //Write deviceID
                        ((vars) Intro.this.getApplicationContext()).Write().writeObject(((vars) Intro.this.getApplicationContext()).getDeviceID());
                        ((vars) Intro.this.getApplicationContext()).Write().flush();

                        String banned = (String) ((vars) Intro.this.getApplicationContext()).Read().readObject();
                        if (banned.equalsIgnoreCase("YES")) {
                            ((vars) Intro.this.getApplicationContext()).setUserInfo(null, null, null, null);
                            return "YES";
                        }

                        publishProgress("Downloading user settings...");
                        ((vars) Intro.this.getApplicationContext()).setUserInfo(null, null, null, BitmapFactory.decodeResource(Intro.this.getResources(),
                                R.drawable.profile_image));

                        //Read name
                        String name = (String) ((vars) Intro.this.getApplicationContext()).Read().readObject();
                        System.err.println(name);
                        if (name != null)
                            ((vars) Intro.this.getApplicationContext()).setUserInfo(null, name, null, null);

                        //Read Image
                        byte[] Image = (byte[]) ((vars) Intro.this.getApplicationContext()).Read().readObject();
                        if (Image != null) {
                            Bitmap bitmap = BitmapFactory.decodeByteArray(Image, 0, Image.length);
                            ((vars) Intro.this.getApplicationContext()).setUserInfo(null, null, null, bitmap);
                        }

                        //Read BDay
                        String bday = (String) ((vars) Intro.this.getApplicationContext()).Read().readObject();
                        if (bday != null)
                            ((vars) Intro.this.getApplicationContext()).setUserInfo(null, null, bday, null);
                        System.err.println(bday);

                        //Read DBuserID
                        ((vars) Intro.this.getApplicationContext()).setDBuserID((int) ((vars) Intro.this.getApplicationContext()).Read().readObject());
                        //Read Recon id
                        ((vars) Intro.this.getApplicationContext()).setReconnectString((String) ((vars) Intro.this.getApplicationContext()).Read().readObject());
                        System.err.println(((vars) Intro.this.getApplicationContext()).getDBuserID());


                        return banned;
                    }
                    catch(SocketException ex)
                    {
                        ex.printStackTrace();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Intro.this);
                        builder.setTitle("Connection error");
                        builder.setMessage("Internet connection is required. Connect and press ok!!");
                        builder.setIcon(R.drawable.conn_refused);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                                Intro.super.onBackPressed();
                            }
                        });
                        builder.show();
                    }
                }

                return "YES";



            }
            catch(SocketException ex){
                ex.printStackTrace();
                return "ServerNotResponding";
            }
            catch (IOException  | ClassNotFoundException | InterruptedException  e) {
                e.printStackTrace();
                return "ServerNotResponding";
            }
        }


        @Override
        protected void onProgressUpdate(String... values)
        {
            progressBar.incrementProgressBy(1);
            textIntro.setText(values[0]);

        }


        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("ServerNotResponding"))
            {
                Toast.makeText(getApplicationContext(),"Server not Responding",Toast.LENGTH_LONG).show();
                startActivity(new Intent(getApplicationContext(),Login.class));
                finish();
            }
            else
            {
                Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("banned?",result);
                startActivity(intent);
                finish();
            }

        }


    }



    public  void request_permissions()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(PERMISSIONS,32000);
    }



    private int checkPermission()
    {
        return ContextCompat.checkSelfPermission(Intro.this,
                Manifest.permission.READ_PHONE_STATE);
    }


}
