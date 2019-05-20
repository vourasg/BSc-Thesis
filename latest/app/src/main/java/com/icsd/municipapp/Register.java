package com.icsd.municipapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Register extends AppCompatActivity
{

    AutoCompleteTextView email;
    EditText password;
    EditText password2;
    EditText username;
    Button register_button;

    private Register.UserRegisterTask mAuthTask = null;

    private View mProgressView;
    private View mRegisterFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setTitle(getString(R.string.register_title));

        email = (AutoCompleteTextView) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        password2 = (EditText) findViewById(R.id.password2);
        username = (EditText) findViewById(R.id.username);
        register_button = (Button) findViewById(R.id.register_button);

        email.setText(getIntent().getExtras().getString("email"));

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mRegisterFormView = findViewById(R.id.register_form);
        mProgressView = findViewById(R.id.register_progress);
    }

    private void attemptLogin()
    {
        if (mAuthTask != null)
            return;


        // Reset errors.
        email.setError(null);
        password.setError(null);
        password2.setError(null);

        // Store values at the time of the login attempt.


        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password.getText().toString()) && isPasswordValid().equals("small")) {
            password.setError("Password must be at least 6 characters");
            focusView = password;
            cancel = true;
        }
        else if (!TextUtils.isEmpty(password.getText().toString()) && isPasswordValid().equals("match")) {
            password.setError("Passwords must match");
            focusView = password;
            cancel = true;
        }

        if (TextUtils.isEmpty(username.getText().toString())
                || (username.getText().toString().substring(username.getText().toString().length() - username.getText().toString().length()+1)).equalsIgnoreCase(" ")
                || (username.getText().toString().substring(username.getText().toString().length() -1)).equalsIgnoreCase(" ")) {
            username.setError("Username must starts and ends with character of number");
            focusView = username;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email.getText().toString())) {
            email.setError(getString(R.string.error_field_required));
            focusView = email;
            cancel = true;
        } else if (!isEmailValid()) {
            email.setError(getString(R.string.error_invalid_email));
            focusView = email;
            cancel = true;
        }

        if (cancel)
            focusView.requestFocus();
        else
            new UserRegisterTask().execute(email.getText().toString(),password.getText().toString(),username.getText().toString());
    }






    private boolean isEmailValid() {
        boolean contains_at = email.getText().toString().contains("@");
        boolean ends_properly = email.getText().toString().length() >= 6 && (email.getText().toString().substring(email.getText().toString().length() - 4)).equalsIgnoreCase(".com");
        if (!ends_properly)
            ends_properly = (email.getText().toString().substring(email.getText().toString().length() - 3)).equalsIgnoreCase(".gr");
        return contains_at && ends_properly;
    }

    private String isPasswordValid() {
        if(password.getText().toString().length()<6)
            return "small";
        else if(!password.getText().toString().equals(password2.getText().toString()))
            return "Passwords must match";
        return "OK";
    }




    private void showProgress(final boolean show)
    {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }




    private class UserRegisterTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... params) {
            try
            {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out =  new ObjectOutputStream(socket.getOutputStream());
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                //write REGISTER
                out.writeObject("REGISTER");
                out.flush();

                //Write email
                out.writeObject(params[0]);
                out.flush();

                //Write password
                out.writeObject(params[1]);
                out.flush();

                //Write username
                out.writeObject(params[2]);
                out.flush();


                return (String)in.readObject();

            }catch(IOException | ClassNotFoundException ex){ex.printStackTrace(); return null;}



        }

        @Override
        protected void onPostExecute(final String success) {
            mAuthTask = null;
            showProgress(false);

            if(null==success)
                System.exit(0);

            if (success.equalsIgnoreCase("ok")) {
                AlertDialog.Builder alert = new AlertDialog.Builder(Register.this);
                alert.setTitle(getString(R.string.registration));
                alert.setMessage(getString(R.string.registration_msg));
                alert.setCancelable(false);
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finishAffinity();
                        finish();
                    }
                });
                alert.show();

            } else {
                if (success.equalsIgnoreCase("email"))
                {
                    email.setError("Email already in use!!");
                    email.requestFocus();
                }
                else if(success.equalsIgnoreCase("name"))
                {
                    username.setError("Username already in use!!");
                    username.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}
