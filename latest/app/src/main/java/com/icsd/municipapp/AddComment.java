package com.icsd.municipapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;

public class AddComment extends AppCompatActivity {

    FloatingActionButton fab_send_comment;
    EditText comment_et;
    static boolean action_done;
    static String action;
    static int send_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_comment);
        setTitle(getString(R.string.comments_title));

        comment_et = (EditText) findViewById(R.id.comment_et);
        fab_send_comment = (FloatingActionButton) findViewById(R.id.fab_send_comment);
        if("reportComment".equals(getIntent().getExtras().getString("action")))
            action="comment";
        else
            action="subComment";
        send_id=getIntent().getExtras().getInt("report_id");
        fab_send_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment = String.valueOf(comment_et.getText());
                System.out.println("Pressed send button");

                AlertDialog diaBox = AskOption(v,send_id,comment);
                diaBox.show();
            }
        });

    }

    private class send_comment extends AsyncTask<Object, Void,Void>
    {
        int a;
        String b;
        @Override
        protected Void doInBackground(Object... params)
        {
            a=(int)params[0];
            b=(String)params[1];
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
                out.writeObject(((vars)AddComment.this.getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {
                    out.writeObject(action);
                    out.flush();

                    out.writeObject(params[0]);
                    out.flush();

                    out.writeObject(params[1]);
                    out.flush();

                    action = (String)in.readObject();
                    action_done=true;
                    return null;
                }
                else
                {
                    action="reconnect";
                    action_done=true;
                    return null;
                }

            }catch(SocketException e)
            {
                action="no_inet";
                action_done=true;
                return null;
            }
            catch(Exception ex){
                ex.printStackTrace();
                action="no_inet";
                action_done=true;
                return null;
            }


        }
        @Override
        protected void onCancelled(){

            final View rootView = AddComment.this.getWindow().getDecorView();
            final Snackbar snackbar = Snackbar
                    .make(rootView,getString(R.string.notCompleted),Snackbar.LENGTH_INDEFINITE )
                    .setAction(getString(R.string.cancel), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            action_done=true;
                        }
                    }).setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new send_comment().execute(a,b);
                        }
                    });

            snackbar.show();
        }
    }





    public AlertDialog AskOption(final View v,final int send_id, final String comment)
    {
        return new AlertDialog.Builder(this)
                .setTitle(getString(R.string.dialog_comment_title))
                .setMessage(getString(R.string.dialog_comment_msg))
                .setIcon(R.drawable.done)
                .setPositiveButton(getString(R.string.dialog_comment_opt1), new DialogInterface.OnClickListener()
                {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        action_done=false;
                        new send_comment().execute(send_id,comment);
                        while(!action_done)
                        {
                            try
                            {
                                Thread.sleep(100);
                            }catch (InterruptedException ex){ex.printStackTrace();}
                        }
                        if("reconnect".equals(action)){
                            dialog.dismiss();
                            final Snackbar snackbar = Snackbar.make(v, getString(R.string.reconnect_msg), Snackbar.LENGTH_INDEFINITE)
                                    .setAction(getString(R.string.reconnect_msg), new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(AddComment.this,Login.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            snackbar.show();
                            new Wait().execute(snackbar);
                        }
                        else  {
                            dialog.dismiss();
                            final Snackbar snackbar = Snackbar.make(v, getString(R.string.comment) + ("OK".equals(action) ? getString(R.string.uploaded) : getString(R.string.not_uploaded)), Snackbar.LENGTH_SHORT)
                                    .setAction("Action", null);
                            snackbar.show();
                            new Wait().execute(snackbar);
                        }


                    }

                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();

    }


    private class Wait extends AsyncTask<Snackbar,Void,Void>
    {
        @Override
        protected Void doInBackground(Snackbar... snackbars)
        {
            System.out.println("WAIT called");
            int i=0;
            while(snackbars[0].isShown() && i<10000) {
                try {
                    Thread.sleep(10);
                    i+=10;
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v)
        {
            Intent intent = new Intent(AddComment.this,CommentsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("report_id",send_id);
            startActivity(intent);
        }
    }
}
