package com.icsd.municipapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

public class CommentsActivity extends AppCompatActivity  {

    static final ArrayList<CommentList> comments=new ArrayList<>();
    int report_id;
    private refreshComments refreshComments;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_layout);
        report_id=getIntent().getExtras().getInt("report_id");
        final int report_id=getIntent().getExtras().getInt("report_id");

        final SwipeRefreshLayout comment_list_refresh = (SwipeRefreshLayout) findViewById(R.id.comment_list_refresh);
        final ListView Comment_List = (ListView) findViewById(R.id.Comment_List);
        final CommentListAdapter commentAdapter = new CommentListAdapter(this, comments);

        comment_list_refresh.post(new Runnable() {
            @Override
            public void run() {
                comment_list_refresh.setRefreshing(true);
                new refreshComments().execute(CommentsActivity.this, report_id, comment_list_refresh, Comment_List, commentAdapter);
            }
        });

        comment_list_refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshComments= new refreshComments();
                refreshComments.execute(CommentsActivity.this, report_id, comment_list_refresh, Comment_List, commentAdapter);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_coments_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.comments) {
            Intent intent = new Intent(this,AddComment.class);
            intent.putExtra("action","reportComment");
            intent.putExtra("report_id",report_id);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class refreshComments extends AsyncTask<Object,Void,Object[]>
    {
        int comSize;
        String error;

        @Override
        protected void onPreExecute()
        {
            for(int i=comments.size()-1; i>=0; i--)
                comments.remove(i);
            error=null;
        }

        @Override
        protected Object[] doInBackground(Object...objects)
        {
            Context context = (Context)objects[0];
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
                out.writeObject(((vars)context.getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {

                    out.writeObject("refresh_comments");
                    out.flush();

                    out.writeObject(objects[1]);
                    out.flush();

                    comSize = (int)in.readObject();
                    System.out.println("Comments size: " + comSize);
                    System.out.println("Comments size: " + comSize);
                    System.out.println("Comments size: " + comSize);
                    String facebook_id, username;
                    byte[] image_byte;
                    URL imageURL;
                    Bitmap image;
                    for (int i = 0; i < comSize; i++) {
                        facebook_id = null;
                        image = null;
                        int level = (int)in.readObject();
                        int id = (int)in.readObject();
                        int user_id = (int)in.readObject();
                        String comment_text = (String)in.readObject();
                        String date = (String)in.readObject();
                        String Case = (String)in.readObject();
                        switch (Case) {
                            case "facebook": {
                                facebook_id = (String)in.readObject();
                                username = (String)in.readObject();
                                imageURL = new URL("https://graph.facebook.com/" + facebook_id + "/picture?type=square");
                                image = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                                break;
                            }
                            case "app_image": {
                                username = (String)in.readObject();
                                image_byte = (byte[])in.readObject();
                                image = BitmapFactory.decodeByteArray(image_byte, 0, image_byte.length);
                                break;
                            }
                            default: {
                                username = (String)in.readObject();
                                break;
                            }
                        }

                        comments.add(new CommentList(id, user_id, comment_text, level, username, image, facebook_id, date));
                        error=null;
                    }
                }
                else {
                    error="reconnect";
                }

            }catch(Exception ex){ex.printStackTrace(); error="no_inet";}
            Object[] return_object=new Object[3];
            return_object[0]=objects[2];
            return_object[1]=objects[3];
            return_object[2]=objects[4];
            return return_object;
        }

        @Override
        protected void onPostExecute(Object[] object)
        {
            if(error==null) {
                ((ListView) object[1]).setAdapter((ListAdapter) object[2]);
                ((SwipeRefreshLayout) object[0]).setRefreshing(false);
            }
            else
            {
                if(error.equals("reconnect")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(CommentsActivity.this);
                    alert.setTitle(getString(R.string.reconnect));
                    alert.setMessage(getString(R.string.reconnect_msg));
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                            startActivity(new Intent(CommentsActivity.this, Login.class));
                            cancel(true);
                            finish();
                        }
                    });
                    alert.show();
                }
                else{
                    final View rootView = CommentsActivity.this.getWindow().getDecorView().findViewById(android.R.id.content);
                    final Snackbar snackbar = Snackbar.make(rootView, getString(R.string.noNetwork), Snackbar.LENGTH_LONG)
                            .setAction("OK", null);
                    snackbar.show();
                }
                ((SwipeRefreshLayout) object[0]).setRefreshing(false);
            }
        }
    }

}
