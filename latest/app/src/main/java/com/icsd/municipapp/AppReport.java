package com.icsd.municipapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class AppReport extends AppCompatActivity implements OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_report);
        setTitle(getString(R.string.app_report_title));
    }

    @Override
    public void onClick(View view)
    {
        if(view.getId()==R.id.submit)
        {
            ProgressDialog dialog=new ProgressDialog(AppReport.this);
            dialog.setTitle(getResources().getString(R.string.sending));
            dialog.setMessage(getResources().getString(R.string.sending_message));
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            new send(dialog).execute(String.valueOf(((EditText)findViewById(R.id.app_report_et)).getText()));
        }
    }


    private class send extends AsyncTask<String,Void,String>
    {
        ProgressDialog dialog;
        String string;

        send(ProgressDialog dialog){
            this.dialog=dialog;
        }
        @Override
        protected String doInBackground(String... strings)
        {
            string=strings[0];
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
                out.writeObject(((vars)AppReport.this.getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {
                    out.writeObject("report_app");
                    out.flush();
                    out.writeObject(strings[0]);
                    out.flush();

                    return (String)in.readObject();
                }
                else
                    return "reconnect";

            }catch(SocketException ex){ex.printStackTrace();}
            catch(Exception ex){}
            return "no_inet";
        }

        @Override
        protected void onPostExecute(String response)
        {
            dialog.dismiss();
            if(!"reconnect".equals(response) && !"no_inet".equals(response)) {
                AlertDialog.Builder dialog2 = new AlertDialog.Builder(AppReport.this);
                dialog2.setTitle(getResources().getString(R.string.sending));
                dialog2.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), MainScreen.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        finishAffinity();
                    }
                });
                if (response.equals("OK"))
                    dialog2.setMessage(getResources().getString(R.string.sending_ok));
                else
                    dialog2.setMessage(getResources().getString(R.string.sending_error));
                dialog2.show();
            }
            else
            {
                if("reconnect".equals(response)) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(AppReport.this);
                    alert.setTitle(getString(R.string.reconnect));
                    alert.setMessage(getString(R.string.reconnect_msg));
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                            startActivity(new Intent(AppReport.this, Login.class));
                            cancel(true);
                            finish();
                        }
                    });
                    alert.show();
                }
                else{
                    final View rootView = AppReport.this.getWindow().getDecorView().findViewById(android.R.id.content);
                    final Snackbar snackbar = Snackbar.make(rootView, getString(R.string.noNetwork), Snackbar.LENGTH_LONG)
                            .setAction("OK", null);
                    snackbar.show();
                }
            }
        }
    }

}
