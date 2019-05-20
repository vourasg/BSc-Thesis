package com.icsd.municipapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Locale;

public class ReviewDetailed extends AppCompatActivity {

    ViewPager mViewPager;
    static FloatingActionButton fab_upvote ;
    static FloatingActionButton fab_downvote ;
    static FloatingActionButton fab_delete ;

    static int countLoaded;
    static boolean action_done;
    static String action;
    static int report_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_detailed);
        setTitle(getString(R.string.review_detailed_title));
        countLoaded=0;
        ((vars)this.getApplicationContext()).setReport(null);

        report_id=getIntent().getExtras().getInt("report_id");

        fab_delete=(FloatingActionButton)findViewById(R.id.fab_delete);
        fab_upvote=(FloatingActionButton)findViewById(R.id.fab_upvote);
        fab_downvote=(FloatingActionButton)findViewById(R.id.fab_downvote);

        new loadReports().execute(this,report_id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(3);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
    }







    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_review_detailed, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.comments) {
            if(countLoaded==3){
                Intent intent = new Intent(this,CommentsActivity.class);
                intent.putExtra("report_id",report_id);
                startActivity(intent);
                return true;
            }
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
        public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
        {
            View rootView;

            if (getArguments().getInt(ARG_SECTION_NUMBER)==1)
            {
                rootView = inflater.inflate(R.layout.fragment_review_detailed, container, false);
                showProgress(true,rootView);
                new setTab1().execute(rootView.getContext().getApplicationContext(),rootView);
                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {
                rootView = inflater.inflate(R.layout.fragment_review_detailed, container, false);
                showProgress(true,rootView);
                new setTab2().execute(rootView.getContext().getApplicationContext(),rootView);
                return rootView;
            }
            else
            {
                rootView = inflater.inflate(R.layout.fragment_review_detailed, container, false);
                showProgress(true,rootView);
                new setTab3().execute(rootView.getContext().getApplicationContext(),rootView);
                return rootView;
            }
        }
    }





    private static class setTab1 extends AsyncTask<Object,Void,View>
    {
        Reports report;
        @Override
        protected View doInBackground(Object...objects)
        {
            while(((vars)((Context)objects[0]).getApplicationContext()).getReport()==null)
                try{Thread.sleep(50);
                    System.err.println("sleep 1 called");}catch(Exception ex){ex.printStackTrace();}
            System.err.println("notified");
            report=((vars)((Context)objects[0]).getApplicationContext()).getReport();
            return (View)objects[1];
        }

        @Override
        protected void onPostExecute(View rootView)
        {
            TextView section_label = (TextView) rootView.findViewById(R.id.section_label);
            section_label.setText(rootView.getResources().getString(R.string.report_category));
            section_label.setVisibility(View.VISIBLE);

            TextView section_label_text = (TextView) rootView.findViewById(R.id.section_label_text);
            section_label_text.setText(report.getReportCategory());
            section_label_text.setVisibility(View.VISIBLE);

            TextView section_label_2 = (TextView) rootView.findViewById(R.id.section_label_2);
            section_label_2.setVisibility(View.VISIBLE);
            section_label_2.setText(rootView.getResources().getString(R.string.report_type));

            TextView section_label_2_text = (TextView) rootView.findViewById(R.id.section_label_2_text);
            section_label_2_text.setVisibility(View.VISIBLE);
            section_label_2_text.setText(report.getReportType());

            TextView section_label_3 = (TextView) rootView.findViewById(R.id.section_label_3);
            section_label_3.setVisibility(View.VISIBLE);
            section_label_3.setText(rootView.getResources().getString(R.string.description));

            TextView section_label_3_text = (TextView) rootView.findViewById(R.id.section_label_3_text);
            section_label_3_text.setVisibility(View.VISIBLE);
            section_label_3_text.setText(report.getDescription());

            showProgress(false,rootView);
            countLoaded++;
        }
    }



    private static class setTab2 extends AsyncTask<Object,Void,View>
    {
        Reports report;
        @Override
        protected View doInBackground(Object...objects)
        {
            report=((vars)((Context)objects[0]).getApplicationContext()).getReport();
            return (View)objects[1];
        }

        @Override
        protected void onPostExecute(View rootView)
        {
            TextView section_label = (TextView) rootView.findViewById(R.id.section_label);
            section_label.setText(rootView.getResources().getString(R.string.image));

            TextView section_label_text = (TextView) rootView.findViewById(R.id.section_label_text);
            if((report.getImageComment()!=null))
                section_label_text.setText(String.valueOf(report.getImageComment()));
            else
                section_label_text.setText(String.valueOf("No Comment Available"));
            section_label_text.setTypeface(null, Typeface.ITALIC);

            ImageView section_label_image = (ImageView) rootView.findViewById(R.id.section_label_image);
            section_label_image.setVisibility(View.VISIBLE);
            if(report.getImage()!=null){
                Bitmap bitmap = BitmapFactory.decodeByteArray(report.getImage(),0,report.getImage().length);
                section_label_image.setImageBitmap(bitmap);
            }
            else
                section_label_image.setImageResource(R.drawable.noimagefound);
            showProgress(false,rootView);
            countLoaded++;
        }
    }



    private static class setTab3 extends AsyncTask<Object,Void,View>
    {
        Reports report;

        @Override
        protected View doInBackground(Object...objects)
        {
            report=((vars)((Context)objects[0]).getApplicationContext()).getReport();
            return (View)objects[1];
        }

        @Override
        protected void onPostExecute(View rootView)
        {
            Geocoder geocoder = new Geocoder(rootView.getContext().getApplicationContext(), Locale.getDefault());

            TextView section_label = (TextView) rootView.findViewById(R.id.section_label);
            section_label.setText(rootView.getResources().getString(R.string.location));

            TextView section_label_text = (TextView) rootView.findViewById(R.id.section_label_text);
            try {
                section_label_text.setText((
                        geocoder.getFromLocation(report.getLatitude(), report.getLongitude(), 1).get(0).getAddressLine(0)+", "
                                +geocoder.getFromLocation(report.getLatitude(), report.getLongitude(), 1).get(0).getLocality()));
            }catch(Exception ex){ex.printStackTrace();}


            ImageView section_label_image = (ImageView)rootView.findViewById(R.id.section_label_image);
            section_label_image.setVisibility(View.VISIBLE);

            Resources resources = rootView.getResources();
            DisplayMetrics dm = resources.getDisplayMetrics();

            Configuration config = resources.getConfiguration();

            int screenWidthInPixels = (int)(config.screenWidthDp * dm.density);

            new getGoogleMapThumbnail().execute(report.getLatitude(),report.getLongitude(),screenWidthInPixels,section_label_image);

            showProgress(false,rootView);
            countLoaded++;
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





    private static void showProgress(final boolean show,View rootView) {

        int shortAnimTime = rootView.getResources().getInteger(android.R.integer.config_shortAnimTime);

        final View detailed_scrollview = rootView.findViewById(R.id.detailed_scrollview);
        final View load_progress = rootView.findViewById(R.id.load_progress);

        detailed_scrollview.setVisibility(show ? View.GONE : View.VISIBLE);
        detailed_scrollview.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                detailed_scrollview.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        load_progress.setVisibility(show ? View.VISIBLE : View.GONE);
        load_progress.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                load_progress.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }




    private static class loadReports extends AsyncTask<Object,Void,Object[]>
    {
        String last_Action;
        @Override
        protected Object[] doInBackground(Object...objects)
        {
            Reports report=null;

            try
            {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out =  new ObjectOutputStream(socket.getOutputStream());
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                out.writeObject("RECONNECT");
                out.flush();
                out.writeObject(((vars) ((Context) objects[0]).getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {
                    // Write details
                    out.writeObject("Details");
                    out.flush();

                    // Write reports id for details
                    out.writeObject(objects[1]);
                    out.flush();

                    // Read details
                    if (in.readObject().equals("SENDING")) {
                        String Report_Category, Report_Type, ImageComment, Locality, Description;
                        int ReportID, UserID;
                        byte[] Image;
                        double Longitude, Latitude;
                        int upVotes, downVotes;


                        ReportID = (int)in.readObject();
                        System.err.println("read report id");
                        UserID = (int)in.readObject();
                        System.err.println("read userid");
                        Report_Category = (String)in.readObject();
                        System.err.println("read report category");
                        Report_Type = (String)in.readObject();
                        System.err.println("read report type");
                        Description = (String)in.readObject();
                        System.err.println("read report Description");
                        Image = (byte[])in.readObject();
                        System.err.println("reade image");
                        ImageComment = (String)in.readObject();
                        System.err.println("read image comment");
                        Latitude = (double)in.readObject();
                        System.err.println("read latitude");
                        Longitude = (double)in.readObject();
                        System.err.println("read longitude");
                        Locality = (String)in.readObject();
                        System.err.println("read locality");
                        upVotes = (int)in.readObject();
                        System.err.println("read upvotes");
                        downVotes = (int)in.readObject();
                        System.err.println("read downvotes");
                        last_Action = (String)in.readObject();
                        System.err.println("read last action");

                        report = new Reports(ReportID, UserID, Report_Category, Report_Type, Description, Image, ImageComment, Latitude, Longitude, Locality, upVotes, downVotes);
                    }
                }
                else
                    return null;


            }catch(Exception ex){ex.printStackTrace();}

            Object[] return_object = new Object[2];
            return_object[0]=objects[0];
            return_object[1]=report;
            return return_object;
        }

        @Override
        protected void onPostExecute(Object[] objects)
        {
            final Context context = (Context)objects[0];
            final Reports report=(Reports)objects[1];
            ((vars)context.getApplicationContext()).setReport(report);
            if(report.getUserID()==((vars)context.getApplicationContext()).getDBuserID())
            {
                fab_delete.setVisibility(View.VISIBLE);
                fab_downvote.setVisibility(View.INVISIBLE);
                fab_upvote.setVisibility(View.INVISIBLE);

                fab_delete.setImageDrawable(context.getResources().getDrawable(R.drawable.delete));
            }
            else
            {
                fab_delete.setVisibility(View.INVISIBLE);
                fab_downvote.setVisibility(View.VISIBLE);
                fab_upvote.setVisibility(View.VISIBLE);


                if(last_Action.equals("upvoted"))
                {

                    fab_upvote.setImageDrawable(context.getResources().getDrawable(R.drawable.like));
                    fab_upvote.setBackgroundTintList(null);

                }
                else if(last_Action.equals("downvoted"))
                {
                    fab_downvote.setImageDrawable(context.getResources().getDrawable(R.drawable.dislike));
                    fab_downvote.setBackgroundTintList(null);
                }
            }

            fab_upvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(last_Action.equals("upvoted"))
                    {
                        Snackbar.make(view,context.getString(R.string.upvoted_already), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else
                    {
                        action_done = false;
                        new sendRequest().execute("upvote",report.getReportID(),context);
                        while (!action_done) {
                            try{Thread.sleep(50);}catch (InterruptedException ex){ex.printStackTrace();}
                        }
                        if (!action.equals("ERROR")) {

                            fab_upvote.setImageDrawable(context.getResources().getDrawable(R.drawable.like));
                            fab_downvote.setImageDrawable(context.getResources().getDrawable(R.drawable.dislike_un));
                            Snackbar.make(view, context.getString(R.string.upvoting_completed), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            last_Action="upvoted";
                        }
                        else{
                            Snackbar.make(view, context.getString(R.string.upvoting_error), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }


                    }
                }
            });

            fab_downvote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(last_Action.equals("downvoted"))
                    {
                        Snackbar.make(view,context.getString(R.string.downvoted_already), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else {
                        action_done = false;
                        new sendRequest().execute("downvote",report.getReportID(),context);
                        while (!action_done) {
                            try{Thread.sleep(50);}catch (InterruptedException ex){ex.printStackTrace();}
                        }
                        if (!action.equals("ERROR"))
                        {

                            fab_downvote.setImageDrawable(context.getResources().getDrawable(R.drawable.dislike));
                            fab_upvote.setImageDrawable(context.getResources().getDrawable(R.drawable.like_un));
                            Snackbar.make(view,context.getString(R.string.downvoting_completed), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            last_Action="downvoted";
                        }
                        else {
                            Snackbar.make(view, context.getString(R.string.downvoting_error), Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    }
                }
            });

            fab_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog diaBox = AskOption(view,report.getReportID(),context);
                    diaBox.show();
                }
            });

        }
    }




    private static class sendRequest extends AsyncTask<Object, Void, Void>
    {

        @Override
        protected Void doInBackground(Object... params) {
            try
            {
                final Context context = (Context) params[2];
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out =  new ObjectOutputStream(socket.getOutputStream());
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                out.writeObject("RECONNECT");
                out.flush();
                out.writeObject(((vars)context.getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {

                    out.writeObject(params[0]);
                    out.flush();

                    out.writeObject(params[1]);
                    out.flush();

                    action = (String)in.readObject();
                }
                else{
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle(context.getString(R.string.reconnect));
                    alert.setMessage(context.getString(R.string.reconnect_msg));
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.startActivity(new Intent(context,Login.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            cancel(true);
                        }
                    });
                }
                action_done=true;
            } catch (IOException |ClassNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }


    }





    private static AlertDialog AskOption(final View v,final int report_id,final Context context)
    {
        return new AlertDialog.Builder(context)
                .setTitle("Delete")
                .setMessage("Do you want to Delete")
                .setIcon(R.drawable.delete)

                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        action_done=false;
                        new sendRequest().execute("delete",report_id,context);
                        while(!action_done)
                        {
                            try
                            {
                                Thread.sleep(100);
                            }catch (InterruptedException ex){ex.printStackTrace();}
                        }
                        dialog.dismiss();
                        Snackbar snackbar=Snackbar.make(v,"Delete "+ action, Snackbar.LENGTH_LONG)
                                .setAction("Action", null);
                        snackbar.show();

                    }

                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

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



}
