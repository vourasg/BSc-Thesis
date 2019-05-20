package com.icsd.municipapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;

public class Achievements extends AppCompatActivity {


    static ArrayList<String> listMine;
    static ArrayList<String> listTopSingle;
    static ArrayList<String> listTopTotal;
    private static setTab1 tab1;
    private static setTab2 tab2;
    private static setTab3 tab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievements);
        setTitle(getString(R.string.achievements_title));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        ViewPager mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        new LoadAchievements().execute();

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
            View rootView;

            if(getArguments().getInt(ARG_SECTION_NUMBER)==1)
            {
                rootView = inflater.inflate(R.layout.fragment_achievements_personal, container, false);
                showProgress(true,rootView);
                tab1=new setTab1();
                tab1.execute(rootView);
                return rootView;
            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {
                rootView = inflater.inflate(R.layout.fragment_achievements_top, container, false);
                showProgress(true,rootView);
                tab2=new setTab2();
                tab2.execute(rootView);
                return rootView;

            }
            else if(getArguments().getInt(ARG_SECTION_NUMBER)==3)
            {
                rootView = inflater.inflate(R.layout.fragment_achievements_top, container, false);
                showProgress(true,rootView);
                tab3=new setTab3();
                tab3.execute(rootView);
                return rootView;
            }
            else
                return null;
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
                    return "Personal";
                case 1:
                    return "Top 10 Single";
                case 2:
                    return "Top 10 Total";
            }
            return null;
        }
    }




    private static void showProgress(final boolean show,View rootView) {

        int shortAnimTime = rootView.getResources().getInteger(android.R.integer.config_shortAnimTime);

        final View view = rootView.findViewById(R.id.view);
        final View load_progress = rootView.findViewById(R.id.load_progress);

        view.setVisibility(show ? View.GONE : View.VISIBLE);
        view.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(show ? View.GONE : View.VISIBLE);
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







    private static class setTab1 extends AsyncTask<View,Void,View>
    {
        @Override
        protected View doInBackground(View...view)
        {
            int i=0;
            while(listMine==null)
                try {
                    Thread.sleep(50);
                    i += 50;
                    if(i>4000)
                        return null;
                }
                catch(Exception ex){ex.printStackTrace();}
            return view[0];
        }

        @Override
        protected void onPostExecute(View rootView)
        {
            if(listMine!=null && rootView!=null) {
                TextView tvRankSingle, tvRankTotal;
                TextView tvScoreSingle, tvScoreTotal;
                tvRankSingle = (TextView) rootView.findViewById(R.id.personal_single_best_rank);
                tvRankTotal = (TextView) rootView.findViewById(R.id.personal_total_best_rank);
                tvScoreSingle = (TextView) rootView.findViewById(R.id.personal_single_best);
                tvScoreTotal = (TextView) rootView.findViewById(R.id.personal_total_best);

                String rank;
                if (!listMine.get(1).equals("0"))
                    rank = rootView.getResources().getString(R.string.rank) + listMine.get(1);
                else
                    rank = rootView.getResources().getString(R.string.rank) + "-";
                tvRankSingle.setText(rank);
                String score = rootView.getResources().getString(R.string.score) + listMine.get(0);
                tvScoreSingle.setText(score);
                if (!listMine.get(3).equals("0"))
                    rank = rootView.getResources().getString(R.string.rank) + listMine.get(3);
                else
                    rank = rootView.getResources().getString(R.string.rank) + "-";
                tvRankTotal.setText(rank);
                score = rootView.getResources().getString(R.string.score) + listMine.get(2);
                tvScoreTotal.setText(score);
                showProgress(false, rootView);
            }
        }
    }




    private static class setTab2 extends AsyncTask<View,Void,View>
    {
        @Override
        protected View doInBackground(View...view)
        {
            int i=0;
            while(listTopSingle==null)
                try {
                    Thread.sleep(50);
                    i += 50;
                    if(i>4000)
                        return null;
                }
                catch(Exception ex){ex.printStackTrace();}

            return view[0];
        }

        @Override
        protected void onPostExecute(View rootView)
        {
            if(listTopSingle!=null) {
                TextView[] textViews = new TextView[10];
                textViews[0] = (TextView) rootView.findViewById(R.id.first);
                textViews[1] = (TextView) rootView.findViewById(R.id.second);
                textViews[2] = (TextView) rootView.findViewById(R.id.third);
                textViews[3] = (TextView) rootView.findViewById(R.id.fourth);
                textViews[4] = (TextView) rootView.findViewById(R.id.fifth);
                textViews[5] = (TextView) rootView.findViewById(R.id.sixth);
                textViews[6] = (TextView) rootView.findViewById(R.id.seventh);
                textViews[7] = (TextView) rootView.findViewById(R.id.eighth);
                textViews[8] = (TextView) rootView.findViewById(R.id.ninth);
                textViews[9] = (TextView) rootView.findViewById(R.id.tenth);

                ArrayList<String> list;
                String text;
                list = listTopSingle;
                for (int i = 0; i < list.size(); i += 3) {
                    text = rootView.getResources().getString(R.string.score) + list.get(i + 1) +
                            "\n" + rootView.getResources().getString(R.string.user) + list.get(i + 2);
                    textViews[i / 3].setText(text);
                    textViews[i / 3].setId(Integer.parseInt(list.get(i)));
                }
                showProgress(false, rootView);
            }
        }
    }




    private static class setTab3 extends AsyncTask<View,Void,View>
    {
        @Override
        protected View doInBackground(View...view)
        {
            int i=0;
            while(listTopTotal==null) {
                try {
                    Thread.sleep(50);
                    i += 50;
                    if(i>4000)
                        return null;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return view[0];
        }

        @Override
        protected void onPostExecute(View rootView)
        {
            if(listTopTotal!=null) {
                TextView[] textViews = new TextView[10];
                textViews[0] = (TextView) rootView.findViewById(R.id.first);
                textViews[1] = (TextView) rootView.findViewById(R.id.second);
                textViews[2] = (TextView) rootView.findViewById(R.id.third);
                textViews[3] = (TextView) rootView.findViewById(R.id.fourth);
                textViews[4] = (TextView) rootView.findViewById(R.id.fifth);
                textViews[5] = (TextView) rootView.findViewById(R.id.sixth);
                textViews[6] = (TextView) rootView.findViewById(R.id.seventh);
                textViews[7] = (TextView) rootView.findViewById(R.id.eighth);
                textViews[8] = (TextView) rootView.findViewById(R.id.ninth);
                textViews[9] = (TextView) rootView.findViewById(R.id.tenth);


                ArrayList<String> list;
                String text;
                list = listTopTotal;
                for (int i = 0; i < list.size(); i += 3) {
                    text = rootView.getResources().getString(R.string.score) + list.get(i + 1) + "\n" +
                            rootView.getResources().getString(R.string.user) + list.get(i + 2);
                    textViews[i / 3].setText(text);
                    textViews[i / 3].setId(Integer.parseInt(list.get(i)));
                }
                showProgress(false, rootView);
            }
        }
    }






    private class LoadAchievements extends AsyncTask<Void, Integer, String>
    {
        ArrayList<String> ListMine;
        ArrayList<String> ListTopSingle;
        ArrayList<String> ListTopTotal;



        @Override
        protected void onPreExecute()
        {
            ListMine=new ArrayList<>();
            ListTopSingle=new ArrayList<>();
            ListTopTotal=new ArrayList<>();

        }

        @Override
        @SuppressWarnings("unchecked")
        protected String doInBackground(Void... params) {
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
                out.writeObject(((vars)Achievements.this.getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {

                    out.writeObject("achievements");
                    out.flush();

                    ListMine = (ArrayList<String>)in.readObject();
                    ListTopSingle = (ArrayList<String>)in.readObject();
                    ListTopTotal = (ArrayList<String>)in.readObject();
                    return "ok";
                }
                else {
                    if(tab1!=null)
                        if(!tab1.isCancelled())
                            tab1.cancel(true);
                    if(tab2!=null)
                        if(!tab2.isCancelled())
                            tab2.cancel(true);
                    if(tab3!=null)
                        if(!tab3.isCancelled())
                            tab3.cancel(true);
                    return "reconnect";
                }


            }catch(SocketException ex){
                if(tab1!=null)
                    if(!tab1.isCancelled())
                        tab1.cancel(true);
                if(tab2!=null)
                    if(!tab2.isCancelled())
                        tab2.cancel(true);
                if(tab3!=null)
                    if(!tab3.isCancelled())
                        tab3.cancel(true);
                return "no_inet";
            }
            catch (Exception e) {
                e.printStackTrace();
                if(tab1!=null)
                    if(!tab1.isCancelled())
                        tab1.cancel(true);
                if(tab2!=null)
                    if(!tab2.isCancelled())
                        tab2.cancel(true);
                if(tab3!=null)
                    if(!tab3.isCancelled())
                        tab3.cancel(true);
                return"error";
            }
        }

        @Override
        protected void onCancelled() {
            final View rootView = Achievements.this.getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar snackbar = Snackbar
                    .make(rootView,hasInternetAccess()?getString(R.string.noNetwork):getString(R.string.noServer),Snackbar.LENGTH_INDEFINITE )
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(Achievements.this,Achievements.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            Achievements.this.finish();
                        }
                    });

            snackbar.show();

        }


        @Override
        protected void onPostExecute(String result)
        {
            if(result.equals("ok")) {
                listMine = ListMine;
                listTopSingle = ListTopSingle;
                listTopTotal = ListTopTotal;
            }
            else if(result.equals("reconnect")){


                AlertDialog.Builder alert = new AlertDialog.Builder(Achievements.this);
                alert.setTitle(getString(R.string.reconnect));
                alert.setMessage(getString(R.string.reconnect_msg));
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(tab1!=null)
                            tab1.cancel(true);
                        if(tab2!=null)
                            tab2.cancel(true);
                        if(tab3!=null)
                            tab3.cancel(true);
                        startActivity(new Intent(Achievements.this,Login.class));
                        finish();
                    }
                });
                if(tab1!=null)
                    tab1.cancel(true);
                if(tab2!=null)
                    tab2.cancel(true);
                if(tab3!=null)
                    tab3.cancel(true);
            }
            else
            {
                final View rootView = Achievements.this.getWindow().getDecorView().findViewById(android.R.id.content);
                final Snackbar snackbar = Snackbar.make(rootView, getString(R.string.noNetwork), Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(tab1!=null)
                                    if(!tab1.isCancelled())
                                        tab1.cancel(true);
                                if(tab2!=null)
                                    if(!tab2.isCancelled())
                                        tab2.cancel(true);
                                if(tab3!=null)
                                    if(!tab3.isCancelled())
                                        tab3.cancel(true);
                                Intent intent = new Intent(Achievements.this,Achievements.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                        });
                if(tab1!=null)
                    if(!tab1.isCancelled())
                        tab1.cancel(true);
                if(tab2!=null)
                    if(!tab2.isCancelled())
                        tab2.cancel(true);
                if(tab3!=null)
                    if(!tab3.isCancelled())
                        tab3.cancel(true);
                snackbar.show();
            }
        }


    }




    @Override
    public void onBackPressed(){
        if(tab1!=null)
            tab1.cancel(true);
        if(tab2!=null)
            tab2.cancel(true);
        if(tab3!=null)
            tab3.cancel(true);
        startActivity(new Intent(this,MainScreen.class));
        finish();

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
}
