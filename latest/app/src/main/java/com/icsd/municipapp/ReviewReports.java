package com.icsd.municipapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ReviewReports extends AppCompatActivity {

    static int[] idOpen,idClosed,idProg;
    static final ArrayList<ReviewList> ReviewOpen=new ArrayList<>(),ReviewProg=new ArrayList<>(),ReviewClosed=new ArrayList<>();
    static String locality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        if(!("manually".equals(getIntent().getExtras().getString("locality"))))
            locality=getIntent().getExtras().getString("locality");
        else
        {
            Intent intent = new Intent(this,change_locality.class);
            startActivity(intent);
            this.finish();
        }

        setContentView(R.layout.activity_review_reports);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(locality);
        setSupportActionBar(toolbar);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(2);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_review_reports, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.change_locality) {
            Intent intent = new Intent(this,change_locality.class);
            startActivity(intent);
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }


    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

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

            if(getArguments().getInt(ARG_SECTION_NUMBER)==1)
            {
                final View rootView= inflater.inflate(R.layout.fragment_review_reports, container, false);
                final SwipeRefreshLayout review_open_refresh = (SwipeRefreshLayout)rootView.findViewById(R.id.review_loc_refresh);
                final ListView Review_List = (ListView)rootView.findViewById(R.id.Review_List);
                final ReviewListAdapter locAdapter = new ReviewListAdapter(rootView.getContext(),ReviewOpen);

                review_open_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh()
                    {
                        new RefreshOpenList().execute(rootView.getContext().getApplicationContext(),locality,review_open_refresh,Review_List,locAdapter);
                    }
                });

                Review_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(rootView.getContext(),ReviewDetailed.class);
                        intent.putExtra("report_id",idOpen[position]);
                        startActivity(intent);
                    }
                });


                return rootView;
            }

            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {
                final View rootView= inflater.inflate(R.layout.fragment_review_reports, container, false);
                final SwipeRefreshLayout review_prog_refresh = (SwipeRefreshLayout)rootView.findViewById(R.id.review_loc_refresh);
                final ListView Review_List = (ListView)rootView.findViewById(R.id.Review_List);
                final ReviewListAdapter progAdapter = new ReviewListAdapter(rootView.getContext(),ReviewProg);

                review_prog_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh()
                    {
                        new RefreshProgList().execute(rootView.getContext().getApplicationContext(),locality,review_prog_refresh,Review_List,progAdapter);
                    }
                });

                Review_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(rootView.getContext(),ReviewDetailed.class);
                        intent.putExtra("report_id",idProg[position]);
                        startActivity(intent);
                    }
                });

                return rootView;
            }

            else
            {
                final View rootView= inflater.inflate(R.layout.fragment_review_reports, container, false);
                final SwipeRefreshLayout review_closed_refresh = (SwipeRefreshLayout)rootView.findViewById(R.id.review_loc_refresh);
                final ListView Review_List = (ListView)rootView.findViewById(R.id.Review_List);
                final ReviewListAdapter locAdapter = new ReviewListAdapter(rootView.getContext(),ReviewClosed);

                review_closed_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh()
                    {
                        new RefreshClosedList().execute(rootView.getContext().getApplicationContext(),locality,review_closed_refresh,Review_List,locAdapter);
                    }
                });

                Review_List.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(rootView.getContext(),ReviewDetailed.class);
                        intent.putExtra("report_id",idClosed[position]);
                        startActivity(intent);
                    }
                });


                return rootView;

            }



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
                    return getResources().getString(R.string.open_reports);
                case 1:
                    return getResources().getString(R.string.in_progress);
                case 2:
                    return getResources().getString(R.string.closed_reports);
            }
            return null;
        }
    }



    private static class RefreshOpenList extends AsyncTask<Object,Void,Object[]>
    {
        Context context;
        @Override
        protected Object[] doInBackground(Object...object)
        {
            try
            {
                context=(Context)object[0];
                int id,i,sizeLoc;
                String cat, type,description, locality,date;
                Geocoder geocoder = new Geocoder((Context)object[0]);
                double lat, lng;
                List<Address> addressList;
                String ApiKey="AIzaSyAbde-wf7JVhyENoBZzSv7fMAqymTKE3xE";


                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out =  new ObjectOutputStream(socket.getOutputStream());
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                out.writeObject("RECONNECT");
                out.flush();
                out.writeObject(((vars) ((Context)object[0]).getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {


                    out.writeObject("refresh_open");
                    out.flush();
                    out.writeObject(object[1]);
                    out.flush();

                    sizeLoc = (int)in.readObject();

                    idOpen = new int[sizeLoc];

                    for (i = ReviewOpen.size() - 1; i >= 0; i--)
                        ReviewOpen.remove(i);

                    for (i = 0; i < sizeLoc; i++) {

                        id = (int)in.readObject();
                        cat = (String)in.readObject();
                        type = (String)in.readObject();
                        description = (String)in.readObject();
                        lat = (double)in.readObject();
                        lng = (double)in.readObject();
                        locality = (String)in.readObject();
                        date = (String)in.readObject();
                        System.err.println("date: " + date);
                        String URL = "https://maps.googleapis.com/maps/api/streetview?size=400x400&location=" + lat + "," + lng +
                                "&fov=90&heading=235&pitch=10" +
                                "&key=" + ApiKey;
                        Bitmap bmp;
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpGet request = new HttpGet(URL);
                        InputStream in2;
                        try {
                            in2 = httpclient.execute(request).getEntity().getContent();
                            bmp = BitmapFactory.decodeStream(in2);
                            in2.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            bmp = null;
                        }
                        idOpen[i] = id;
                        addressList = geocoder.getFromLocation(lat, lng, 1);
                        ReviewOpen.add(new ReviewList(id, "\n" + type + "\n\n" + description, date, addressList.get(0).getAddressLine(0) + ", " + locality, bmp));

                    }
                }
                else
                    return null;
            }catch(Exception ex){ex.printStackTrace();return null;}
            Object[] return_object=new Object[4];
            return_object[0]=object[2];
            return_object[1]=object[3];
            return_object[2]=object[4];

            return return_object;
        }

        @Override
        protected void onPostExecute(final Object[] object)
        {
            if(object!=null) {
                ((ListView) object[1]).setAdapter((ListAdapter) object[2]);
                ((SwipeRefreshLayout) object[0]).setRefreshing(false);
            }
            else {
                //if()
                final Context context_temp=context;
                AlertDialog.Builder alert = new AlertDialog.Builder(context_temp);
                alert.setTitle(context_temp.getString(R.string.reconnect));
                alert.setMessage(context_temp.getString(R.string.reconnect_msg));
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context_temp.startActivity(new Intent(context_temp, Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    }
                });
            }
        }
    }





    private static class RefreshProgList extends AsyncTask<Object,Void,Object[]>
    {
        Context context;
        @Override
        protected Object[] doInBackground(Object...object)
        {
            try
            {
                context=(Context)object[0];
                int id,i,sizeLoc;
                String cat, type,description, locality,date;
                Geocoder geocoder = new Geocoder((Context)object[0]);
                double lat, lng;
                List<Address> addressList;
                String ApiKey="AIzaSyAbde-wf7JVhyENoBZzSv7fMAqymTKE3xE";


                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out =  new ObjectOutputStream(socket.getOutputStream());
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                out.writeObject("RECONNECT");
                out.flush();
                out.writeObject(((vars) ((Context)object[0]).getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {


                    out.writeObject("refresh_progress");
                    out.flush();
                    out.writeObject(object[1]);
                    out.flush();

                    sizeLoc = (int)in.readObject();

                    idProg = new int[sizeLoc];

                    for (i = ReviewProg.size() - 1; i >= 0; i--)
                        ReviewProg.remove(i);

                    for (i = 0; i < sizeLoc; i++) {

                        id = (int)in.readObject();
                        cat = (String)in.readObject();
                        type = (String)in.readObject();
                        description = (String)in.readObject();
                        lat = (double)in.readObject();
                        lng = (double)in.readObject();
                        locality = (String)in.readObject();
                        date = (String)in.readObject();
                        System.err.println("date: " + date);
                        String URL = "https://maps.googleapis.com/maps/api/streetview?size=400x400&location=" + lat + "," + lng +
                                "&fov=90&heading=235&pitch=10" +
                                "&key=" + ApiKey;
                        Bitmap bmp;
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpGet request = new HttpGet(URL);
                        InputStream in2;
                        try {
                            in2 = httpclient.execute(request).getEntity().getContent();
                            bmp = BitmapFactory.decodeStream(in2);
                            in2.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            bmp = null;
                        }
                        idProg[i] = id;
                        addressList = geocoder.getFromLocation(lat, lng, 1);
                        ReviewProg.add(new ReviewList(id, "\n" + type + "\n\n" + description, date, addressList.get(0).getAddressLine(0) + ", " + locality, bmp));

                    }
                }
                else
                    return null;
            }catch(Exception ex){ex.printStackTrace();}
            Object[] return_object=new Object[3];
            return_object[0]=object[2];
            return_object[1]=object[3];
            return_object[2]=object[4];


            return return_object;
        }

        @Override
        protected void onPostExecute(final Object[] object)
        {
            if(object!=null) {
                ((ListView) object[1]).setAdapter((ListAdapter) object[2]);
                ((SwipeRefreshLayout) object[0]).setRefreshing(false);
            }
            else {
                final Context context_temp=context;
                AlertDialog.Builder alert = new AlertDialog.Builder(context_temp);
                alert.setTitle(context_temp.getString(R.string.reconnect));
                alert.setMessage(context_temp.getString(R.string.reconnect_msg));
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context_temp.startActivity(new Intent(context_temp, Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));

                    }
                });
            }
        }
    }





    private static class RefreshClosedList extends AsyncTask<Object,Void,Object[]>
    {
        Context context;
        @Override
        protected Object[] doInBackground(Object...object)
        {
            try
            {
                context=(Context)object[0];
                int id,i,sizeLoc;
                String cat, type,description, locality,date;
                Geocoder geocoder = new Geocoder((Context)object[0]);
                double lat, lng;
                List<Address> addressList;
                String ApiKey="AIzaSyAbde-wf7JVhyENoBZzSv7fMAqymTKE3xE";


                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out =  new ObjectOutputStream(socket.getOutputStream());
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                out.writeObject("RECONNECT");
                out.flush();
                out.writeObject(((vars) ((Context)object[0]).getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {


                    out.writeObject("refresh_closed");
                    out.flush();
                    out.writeObject(object[1]);
                    out.flush();

                    sizeLoc = (int)in.readObject();

                    idClosed = new int[sizeLoc];

                    for (i = ReviewClosed.size() - 1; i >= 0; i--)
                        ReviewClosed.remove(i);

                    for (i = 0; i < sizeLoc; i++) {

                        id = (int)in.readObject();
                        cat = (String)in.readObject();
                        type = (String)in.readObject();
                        description = (String)in.readObject();
                        lat = (double)in.readObject();
                        lng = (double)in.readObject();
                        locality = (String)in.readObject();
                        date = (String)in.readObject();
                        System.err.println("date: " + date);
                        String URL = "https://maps.googleapis.com/maps/api/streetview?size=400x400&location=" + lat + "," + lng +
                                "&fov=90&heading=235&pitch=10" +
                                "&key=" + ApiKey;
                        Bitmap bmp;
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpGet request = new HttpGet(URL);
                        InputStream in2;
                        try {
                            in2 = httpclient.execute(request).getEntity().getContent();
                            bmp = BitmapFactory.decodeStream(in2);
                            in2.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                            bmp = null;
                        }
                        idClosed[i] = id;
                        addressList = geocoder.getFromLocation(lat, lng, 1);
                        ReviewClosed.add(new ReviewList(id, "\n" + type + "\n\n" + description, date, addressList.get(0).getAddressLine(0) + ", " + locality, bmp));

                    }
                }
                else
                    return null;
            }catch(Exception ex){ex.printStackTrace();}
            Object[] return_object=new Object[3];
            return_object[0]=object[2];
            return_object[1]=object[3];
            return_object[2]=object[4];

            return return_object;
        }

        @Override
        protected void onPostExecute(final Object[] object)
        {
            if(object!=null) {
                ((ListView) object[1]).setAdapter((ListAdapter) object[2]);
                ((SwipeRefreshLayout) object[0]).setRefreshing(false);
            }
            else {
                if(hasInternetAccess()) {
                    final Context context_temp = context;
                    AlertDialog.Builder alert = new AlertDialog.Builder(context_temp);
                    alert.setTitle(context_temp.getString(R.string.reconnect));
                    alert.setMessage(context_temp.getString(R.string.reconnect_msg));
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            context_temp.startActivity(new Intent(context_temp, Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

                        }
                    });
                }
            }
        }
    }



    public static boolean hasInternetAccess() {
        try {
            HttpURLConnection urlc = (HttpURLConnection)
                    (new URL("http://clients3.google.com/generate_204")
                            .openConnection());
            urlc.setRequestProperty("User-Agent", "Android");
            urlc.setRequestProperty("Connection", "close");
            urlc.setConnectTimeout(1500);
            urlc.connect();
            return (urlc.getResponseCode() == 204 &&
                    urlc.getContentLength() == 0);
        } catch (IOException e) {
            return false;
        }
    }



    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this,MainScreen.class));
        finishAffinity();
        finish();
    }




}
