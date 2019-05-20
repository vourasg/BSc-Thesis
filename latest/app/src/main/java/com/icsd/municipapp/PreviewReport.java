package com.icsd.municipapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;

public class PreviewReport extends AppCompatActivity {


    private static final String ARG_SECTION_NUMBER = "section_number";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_report);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Preview Report");
        toolbar.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        FloatingActionButton send_report = (FloatingActionButton) findViewById(R.id.send_report);
        send_report.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //PreviewReport.this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                ProgressDialog dialog=new ProgressDialog(PreviewReport.this);
                dialog.setTitle(getString(R.string.sending));
                dialog.setMessage(getString(R.string.sending_message));
                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                new sendReport(dialog).execute();

            }
        });

    }





    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }


        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_preview_report, container, false);
            if (getArguments().getInt(ARG_SECTION_NUMBER)==1)
            {
                TextView section_label = (TextView) rootView.findViewById(R.id.section_label);
                section_label.setText(rootView.getResources().getString(R.string.report_category));
                section_label.setVisibility(View.VISIBLE);

                TextView section_label_text = (TextView) rootView.findViewById(R.id.section_label_text);
                section_label_text.setText(((vars)rootView.getContext().getApplicationContext()).getReport_cat());
                section_label_text.setVisibility(View.VISIBLE);

                TextView section_label_2 = (TextView) rootView.findViewById(R.id.section_label_2);
                section_label_2.setText(rootView.getResources().getString(R.string.report_type));
                section_label_2.setVisibility(View.VISIBLE);

                TextView sectio_label_2_text = (TextView) rootView.findViewById(R.id.section_label_2_text);
                sectio_label_2_text.setText(((vars)rootView.getContext().getApplicationContext()).getReport_type());
                sectio_label_2_text.setVisibility(View.VISIBLE);

                TextView section_label_3 = (TextView) rootView.findViewById(R.id.section_label_3);
                section_label_3.setText(rootView.getResources().getString(R.string.description));
                section_label_3.setVisibility(View.VISIBLE);

                TextView sectio_label_3_text = (TextView) rootView.findViewById(R.id.section_label_3_text);
                String description=((vars)rootView.getContext().getApplicationContext()).getDescription();
                sectio_label_3_text.setVisibility(View.VISIBLE);
                if(description.equals("") || description.equals(" ") || description==null)
                    sectio_label_3_text.setText(rootView.getResources().getString(R.string.no_description));
                else
                    sectio_label_3_text.setText(((vars)rootView.getContext().getApplicationContext()).getDescription());

            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {

                TextView section_label = (TextView) rootView.findViewById(R.id.section_label);
                section_label.setText(rootView.getResources().getString(R.string.image));
                section_label.setVisibility(View.VISIBLE);

                TextView section_label_text = (TextView) rootView.findViewById(R.id.section_label_text);
                if(((vars)rootView.getContext().getApplicationContext()).getImage_comment()!=null) {
                    section_label_text.setText(((vars) rootView.getContext().getApplicationContext()).getImage_comment());
                    section_label_text.setVisibility(View.VISIBLE);
                }

                ImageView section_label_image = (ImageView) rootView.findViewById(R.id.section_label_image);
                section_label_image.setVisibility(View.VISIBLE);
                if(((vars) rootView.getContext().getApplicationContext()).getImage()!=null)
                    section_label_image.setImageBitmap(((vars) rootView.getContext().getApplicationContext()).getImage());
                else
                    section_label_image.setImageResource(R.drawable.noimagefound);

                TextView section_label_3 = (TextView) rootView.findViewById(R.id.section_label_3);
                TextView sectio_label_3_text = (TextView) rootView.findViewById(R.id.section_label_3_text);
                section_label_3.setVisibility(View.GONE);
                sectio_label_3_text.setVisibility(View.GONE);

            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==3)
            {
                TextView section_label_3 = (TextView) rootView.findViewById(R.id.section_label_3);
                TextView sectio_label_3_text = (TextView) rootView.findViewById(R.id.section_label_3_text);
                section_label_3.setVisibility(View.GONE);
                sectio_label_3_text.setVisibility(View.GONE);

                TextView section_label = (TextView) rootView.findViewById(R.id.section_label);
                section_label.setText(rootView.getResources().getString(R.string.location));
                section_label.setVisibility(View.VISIBLE);

                final Geocoder geocoder = new Geocoder(rootView.getContext().getApplicationContext(), Locale.getDefault());

                TextView section_label_text = (TextView) rootView.findViewById(R.id.section_label_text);
                section_label_text.setVisibility(View.VISIBLE);
                ImageView section_label_image = (ImageView)rootView.findViewById(R.id.section_label_image);
                section_label_image.setVisibility(View.VISIBLE);

                Resources resources = getResources();
                DisplayMetrics dm = resources.getDisplayMetrics();

                Configuration config = resources.getConfiguration();

                int screenWidthInPixels = (int)(config.screenWidthDp * dm.density);


                double lat,lng;

                lat=((vars) rootView.getContext().getApplicationContext()).getRepLatLng().latitude;
                lng=((vars) rootView.getContext().getApplicationContext()).getRepLatLng().longitude;


                try {
                    section_label_text.setText(geocoder.getFromLocation(lat,lng, 1).get(0).getAddressLine(0)+", "+geocoder.getFromLocation(lat,lng, 1).get(0).getLocality());

                }catch(Exception ex){ex.printStackTrace();}

                new getGoogleMapThumbnail().execute(lat,lng,screenWidthInPixels,section_label_image);
            }
            return rootView;
        }
    }


    private class SectionsPagerAdapter extends FragmentPagerAdapter {

        SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.tab1);
                case 1:
                    return getString(R.string.tab2);
                case 2:
                    return getString(R.string.tab3);
            }
            return null;
        }
    }




    private static class getGoogleMapThumbnail extends AsyncTask<Object,Void,Bitmap>
    {
        double lat,lng;
        int width;
        ImageView section_label_image;

        @Override
        protected Bitmap doInBackground(Object... objects)
        {
            lat=(double)objects[0];
            lng=(double)objects[1];
            width=(int)objects[2];
            section_label_image=(ImageView)objects[3];

            String URL = "http://maps.google.com/maps/api/staticmap?center=" +lat + "," + lng + "&zoom=15&size="+width+"x"+width+"&sensor=false&markers=color:blue%7Clabel:S%7C"+lat+","+lng;
            Bitmap bmp = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet request = new HttpGet(URL);

            InputStream in;
            try {
                in = httpclient.execute(request).getEntity().getContent();
                bmp = BitmapFactory.decodeStream(in);
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return bmp;
        }


        @Override
        protected void onPostExecute(Bitmap bmp)
        {
            section_label_image.requestLayout();
            section_label_image.setImageBitmap(bmp);
            section_label_image.getLayoutParams().height = width;
        }
    }



    private class sendReport extends AsyncTask<Void,Void,String>
    {
        ProgressDialog dialog;

        sendReport(ProgressDialog dialog){
            this.dialog=dialog;
        }

        @Override
        protected String doInBackground(Void... params)
        {
            try {

                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                out.writeObject("RECONNECT");
                out.flush();
                out.writeObject(((vars)PreviewReport.this.getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {

                    String Locality;
                    final Geocoder geocoder = new Geocoder(PreviewReport.this, Locale.getDefault());

                    //ADD
                    out.writeObject("ADD");
                    out.flush();

                    //UserID
                    out.writeObject(((vars) PreviewReport.this.getApplicationContext()).getUserInfo(1));
                    out.flush();

                    //Report category
                    out.writeObject(((vars) PreviewReport.this.getApplicationContext()).getReport_cat());
                    out.flush();

                    //Report type
                    out.writeObject(((vars) PreviewReport.this.getApplicationContext()).getReport_type());
                    out.flush();

                    //Report description
                    if (((vars) PreviewReport.this.getApplicationContext()).getDescription() != null)
                        out.writeObject(((vars) PreviewReport.this.getApplicationContext()).getDescription());
                    else
                        out.writeObject(getResources().getString(R.string.no_description));

                    out.flush();

                    //Report Image
                    if (((vars) PreviewReport.this.getApplicationContext()).getImage() != null) {
                        out.writeObject("image");
                        out.flush();

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        ((vars) PreviewReport.this.getApplicationContext()).getImage().compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        out.writeObject(byteArray);
                        out.flush();

                        //Report Image comment
                        out.writeObject(((vars) PreviewReport.this.getApplicationContext()).getImage_comment());
                        out.flush();
                    } else {
                        out.writeObject("no_image");
                        out.flush();
                    }


                    //Report Latitude, Longitude
                    out.writeObject(((vars) PreviewReport.this.getApplicationContext()).getRepLatLng().latitude);
                    out.flush();

                    out.writeObject(((vars) PreviewReport.this.getApplicationContext()).getRepLatLng().longitude);
                    out.flush();

                    Locality = geocoder.getFromLocation(((vars) PreviewReport.this.getApplicationContext()).getRepLatLng().latitude,
                            ((vars) PreviewReport.this.getApplicationContext()).getRepLatLng().longitude, 1).get(0).getLocality();

                    out.writeObject(Locality);
                    out.flush();

                    return (String)in.readObject();
                }
                else
                {
                    return null;
                }

            }catch(Exception ex){ex.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(String s)
        {
            if(null!=s) {
                if (((vars) PreviewReport.this.getApplicationContext()).getImage() != null)
                    ((vars) PreviewReport.this.getApplicationContext()).getImage().recycle();
                AlertDialog.Builder alert = new AlertDialog.Builder(PreviewReport.this);
                alert.setTitle(getString(R.string.uploading));
                if (s != null)
                    alert.setMessage(s.equals("ADDED") ? getString(R.string.sending_ok) : getString(R.string.sending_error));
                else
                    alert.setMessage(getString(R.string.error));

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finishAffinity();
                    }
                });
                dialog.dismiss();
                alert.show();
            }
            else
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(PreviewReport.this);
                alert.setTitle(getString(R.string.reconnect));
                alert.setMessage(getString(R.string.reconnect_msg));
                alert.setIcon(getDrawable(R.drawable.conn_refused));
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        startActivity(new Intent(PreviewReport.this,Login.class));
                        cancel(true);
                        finish();
                    }
                });
            }
        }
    }

}
