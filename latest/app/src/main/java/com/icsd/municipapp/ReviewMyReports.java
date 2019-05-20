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
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class ReviewMyReports extends AppCompatActivity {

    static String locality;
    static final ArrayList<ReviewList> list=new ArrayList<>();
    static int[] ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_closed);

        setTitle(getString(R.string.my_report_title));
        locality=" ";

        final SwipeRefreshLayout review_closed_refresh = (SwipeRefreshLayout)findViewById(R.id.review_closed_refresh);
        final ListView closed_reports_list = (ListView)findViewById(R.id.closed_reports_list);
        final ReviewListAdapter reviewListAdapterAdapter = new ReviewListAdapter(this,list);


        review_closed_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                new RefreshList().execute(ReviewMyReports.this.getApplicationContext(),locality,review_closed_refresh,closed_reports_list,reviewListAdapterAdapter);
            }
        });

        closed_reports_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ReviewMyReports.this,ReviewDetailed.class);
                intent.putExtra("report_id",ids[position]);
                startActivity(intent);
            }
        });
    }





    private static class RefreshList extends AsyncTask<Object,Void,Object[]>
    {
        Context context;
        @Override
        protected Object[] doInBackground(Object...object)
        {
            context = (Context) object[0];
            try
            {

                if(hasInternetAccess()) {
                    int id, i, size;
                    String cat, type, description, locality, date;
                    Geocoder geocoder = new Geocoder((Context) object[0]);
                    double lat, lng;
                    List<Address> addressList;
                    String ApiKey = "AIzaSyDM89VUfs6dYnOSU3kSxtoVhyGjzhLVQGA";

                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress("83.212.111.82", 4444), 10000);
                    //Set outputStream
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    //Set inputStream
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                    out.writeObject("RECONNECT");
                    out.flush();
                    out.writeObject(((vars) ((Context) object[0]).getApplicationContext()).getReconnectString());
                    out.flush();

                    String connected = (String) in.readObject();
                    if ("reconnected".equals(connected)) {
                        out.writeObject("refresh_my");
                        out.flush();

                        size = (int) in.readObject();
                        System.err.println("List size: " + size);
                        ids = new int[size];

                        for (i = list.size() - 1; i >= 0; i--)
                            list.remove(i);

                        for (i = 0; i < size; i++) {

                            id = (int) in.readObject();
                            cat = (String) in.readObject();
                            type = (String) in.readObject();
                            description = (String) in.readObject();
                            lat = (double) in.readObject();
                            lng = (double) in.readObject();
                            locality = (String) in.readObject();
                            date = (String) in.readObject();
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
                            ids[i] = id;
                            addressList = geocoder.getFromLocation(lat, lng, 1);
                            list.add(new ReviewList(id, "\n" + type + "\n\n" + description, date, addressList.get(0).getAddressLine(0) + ", " + locality, bmp));
                        }
                    } else
                        return null;
                }
                else
                    cancel(true);
            }catch(Exception ex){ex.printStackTrace();}
            Object[] return_object=new Object[3];
            return_object[0]=object[2];
            return_object[1]=object[3];
            return_object[2]=object[4];

            return return_object;
        }

        @Override
        protected void onCancelled(){
            View view = View.inflate(context, R.layout.activity_review_closed, null);
            Snackbar.make(view,context.getString(R.string.noNetwork),Snackbar.LENGTH_LONG ).show();
        }

        @Override
        protected void onPostExecute(final Object[] object)
        {
            if(object!=null) {
                ((ListView) object[1]).setAdapter((ListAdapter) object[2]);
                ((SwipeRefreshLayout) object[0]).setRefreshing(false);
            }
            else
            {
                final Context context_temp=context;
                AlertDialog.Builder alert = new AlertDialog.Builder(context_temp);
                alert.setTitle(context_temp.getString(R.string.reconnect));
                alert.setMessage(context_temp.getString(R.string.reconnect_msg));
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        context_temp.startActivity(new Intent(context_temp,Login.class));
                    }
                });
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
        Intent intent = new Intent(ReviewMyReports.this,MainScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        ReviewMyReports.this.finish();
    }
}
