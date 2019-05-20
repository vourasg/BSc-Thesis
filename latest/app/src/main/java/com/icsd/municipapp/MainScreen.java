package com.icsd.municipapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.ProfilePictureView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.Locale;

public class MainScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener
{

    private long mBackPressed;
    ProfilePictureView profilePictureView;
    ImageView Picture;
    TextView ProfileName;
    String state;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Locale locale = new Locale(getSharedPreferences("LANGUAGE",MODE_PRIVATE).getString("PREF_LANGUAGE", null));
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main_screen);
        setTitle(R.string.main_screen_title);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        profilePictureView=(ProfilePictureView)header.findViewById(R.id.ProfilePic);
        Picture = (ImageView)header.findViewById(R.id.Picture);
        ProfileName=(TextView) header.findViewById(R.id.ProfileName);
        if("facebook".equals(((vars)this.getApplicationContext()).getLogin_type()))
        {
            profilePictureView.setVisibility(View.VISIBLE);
            profilePictureView.setProfileId(((vars)this.getApplicationContext()).getFacebookInfo(1));
            ProfileName.setText( ((vars)this.getApplicationContext()).getFacebookInfo(2));
        }
        else
        {
            Picture.setVisibility(View.VISIBLE);
            if(((vars)this.getApplicationContext()).getUserInfo(4)!=null) {
                Picture.setImageBitmap((Bitmap) ((vars) this.getApplicationContext()).getUserInfo(4));
                Picture.setMaxHeight((int)getResources().getDimension(R.dimen.nav_header_height)*4/6);

            }
            if(((vars)this.getApplicationContext()).getUserInfo(4)!=null)
                ProfileName.setText((CharSequence) ((vars)this.getApplicationContext()).getUserInfo(2));
        }

        if(getIntent().getExtras()==null)
            getIntent().putExtra("banned?","NO");
        state="main";
    }






    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch(id)
        {
            case R.id.nav_profile:
            {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                Intent intent = new Intent(MainScreen.this,Settings.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                return true;
            }
            case R.id.nav_logout:
            {
                    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                    drawer.closeDrawer(GravityCompat.START);
                    LoginManager.getInstance().logOut();
                    getSharedPreferences("CREDENTIAL", MODE_PRIVATE)
                            .edit()
                            .putString("EMAIL", null)
                            .putString("PASSWORD", null)
                            .apply();
                    new MainScreen.exit().execute();
                    return true;
            }
            case R.id.nav_greek:
            {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                getSharedPreferences("LANGUAGE",MODE_PRIVATE)
                        .edit()
                        .putString("PREF_LANGUAGE", "el")
                        .apply();

                Intent intent = new Intent(MainScreen.this,MainScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                return true;
            }
            case R.id.nav_english:
            {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);

                getSharedPreferences("LANGUAGE",MODE_PRIVATE)
                        .edit()
                        .putString("PREF_LANGUAGE", "en")
                        .apply();

                Intent intent = new Intent(MainScreen.this,MainScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                return true;
            }

            case R.id.nav_help:
            {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this,Help.class));
                return true;
            }
            case R.id.nav_report:
            {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                startActivity(new Intent(this,AppReport.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                overridePendingTransition(R.anim.anim_in, R.anim.anim_out);
                return true;
            }
            default:
                return true;
        }


    }





    @Override
    public void onClick(View v)
    {
        Intent intent;
        if("NO".equals(getIntent().getExtras().getString("banned?")))
        {
            AlertDialog diaBox;
            if(v.getId()==R.id.my_reports_button || v.getId()==R.id.my_reports_tv || v.getId()==R.id.my_reports_RL|| v.getId()==R.id.circle3)
            {
                startActivity(new Intent(MainScreen.this,ReviewMyReports.class));
            }

            else if(v.getId()==R.id.add_button || v.getId()==R.id.add_tv || v.getId()==R.id.add_RL || v.getId()==R.id.circle1)
            {
                startActivity(new Intent(this,SelectReport.class));
                this.finish();
            }

            else if(v.getId()==R.id.locality_review_button || v.getId()==R.id.locality_review_tv || v.getId()==R.id.locality_review_RL || v.getId()==R.id.circle2) {
                    diaBox = AskLocality(MainScreen.this);
                    diaBox.show();
            }

            else if(v.getId()==R.id.achievement_button || v.getId()==R.id.achievement_tv || v.getId()==R.id.achievement_RL || v.getId()==R.id.circle4)
            {
                    intent = new Intent(this,Achievements.class);
                    startActivity(intent);
            }
        }
        else
            Snackbar.make(v,"You are BANNED due to inappropriate activities", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
    }





    private class exit extends AsyncTask<Void,Void,Void>
    {
        @Override
        protected Void doInBackground(Void... params)
        {
            try{


                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out =  new ObjectOutputStream(socket.getOutputStream());
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                out.writeObject("LOGOUT");
                out.flush();
                out.writeObject(((vars)MainScreen.this.getApplicationContext()).getReconnectString());
                out.flush();

            } catch(Exception e){e.printStackTrace();}

            finishAffinity();
            MainScreen.this.finish();
            System.exit(0);

            return null;
        }

    }





    private static AlertDialog AskLocality(final Context context)
    {
        return new AlertDialog.Builder(context)
                .setTitle(R.string.ask_for_review)
                .setMessage(R.string.ask_for_review_message)
                .setIcon(R.drawable.map_marker_icon)
                .setPositiveButton(R.string.dialog_current_location, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                        new FindLocation(context).execute();
                        dialog.dismiss();

                    }

                })
                .setNegativeButton(R.string.dialog_select_manually, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(context,change_locality.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        dialog.dismiss();
                    }
                })
                .create();

    }





    private static class FindLocation extends AsyncTask<Void,Void,String>
    {
        ProgressDialog waitDialog;
        Context context;
        Geocoder geocoder;
        List<Address> addresses;

        FindLocation(final Context context){
            this.context=context;
        }

        @Override
        protected void onPreExecute()
        {
            waitDialog=new ProgressDialog(context);
            waitDialog.setCanceledOnTouchOutside(false);
            waitDialog.setTitle(context.getApplicationContext().getResources().getString(R.string.locate_title));
            waitDialog.setMessage(context.getApplicationContext().getResources().getString(R.string.locate_message));
            waitDialog.setIndeterminate(true);
            waitDialog.setCancelable(true);
            waitDialog.show();

            geocoder = new Geocoder(context, Locale.getDefault());
        }

        @Override
        protected String doInBackground(Void...objects)
        {
            try {
                Looper.prepare();
            }catch(RuntimeException ex){System.err.println("Looper is already prepared");}

            GPSTracker gps = new GPSTracker(context);
            if(gps.canGetLocation()) {
                double latitude=0;
                double longitude=0;
                for(int i=0; i<10000; i++){
                latitude = gps.getLatitude();
                longitude = gps.getLongitude();}
                gps.stopUsingGPS();
                try {
                    addresses= geocoder.getFromLocation(latitude,longitude,1);
                    return addresses.get(0).getLocality();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

                return null;
        }

        @Override
        protected void onPostExecute(String locality)
        {
            Intent intent;
            intent = new Intent(context,ReviewReports.class);

            waitDialog.dismiss();
            intent.putExtra("locality",locality);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(intent);

        }

    }








    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else if (mBackPressed + 3000 > System.currentTimeMillis())
            new MainScreen.exit().execute();
        else {
            Toast.makeText(getBaseContext(), "Tap back button in order to exit", Toast.LENGTH_SHORT).show();
            mBackPressed = System.currentTimeMillis();
        }

    }

}
