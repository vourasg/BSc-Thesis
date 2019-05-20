package com.icsd.municipapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.support.design.widget.TextInputLayout;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 * A login screen that offers login via email/password.
 */
public class appLogin extends AppCompatActivity {



    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private EditText mEmailView;
    private TextInputLayout email_til;
    private EditText mPasswordView;
    private TextInputLayout password_til;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login);
        setTitle(getString(R.string.title_activity_app_login));

        // Set up the login form.
        mEmailView = (EditText) findViewById(R.id.email);
        email_til = (TextInputLayout) findViewById(R.id.email_til);
        mPasswordView = (EditText) findViewById(R.id.password);
        password_til = (TextInputLayout) findViewById(R.id.password_til);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        if(null!=getIntent().getExtras()) {
            mEmailView.setText(getIntent().getExtras().getString("username"));
            mPasswordView.setText(getIntent().getExtras().getString("password"));
            showProgress(true);
            new UserLoginTask().execute("login", getIntent().getExtras().getString("username"), getIntent().getExtras().getString("password"));
        }
    }









    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin()
    {
        if (mAuthTask != null)
            return;


        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        boolean register = false;
        View focusView = null;

        if(TextUtils.isEmpty(password))
            register=true;

        // Check for a valid password, if the user entered one.
        if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(register)
        {
            showProgress(true);
            new UserLoginTask().execute("register",email);
        }
        else if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            new UserLoginTask().execute("login",email,password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 6;
    }



    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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





    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<String, Void, Object[]>
    {

        String action,email,pass;

        @Override
        protected Object[] doInBackground(String... params)
        {
            action= params[0];
            email=params[1];
            pass=params[2];
            try
            {
                //Set socket
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ((vars)appLogin.this.getApplicationContext()).setWrite(out);
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ((vars)appLogin.this.getApplicationContext()).setRead(in);


                if(params[0].equals("register"))
                {
                    Intent intent = new Intent(getApplicationContext(), Register.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("email", params[1]);
                    startActivity(intent);
                    finish();
                    return null;
                }
                else if(params[0].equals("login"))
                {
                    try
                    {
                        ((vars)appLogin.this.getApplicationContext()).setUserInfo(params[1],null,null,null);
                        //write VALIDATE
                        ((vars) appLogin.this.getApplicationContext()).Write().writeObject("VALIDATE");
                        ((vars) appLogin.this.getApplicationContext()).Write().flush();

                        //Write email
                        ((vars) appLogin.this.getApplicationContext()).Write().writeObject(params[1]);
                        ((vars) appLogin.this.getApplicationContext()).Write().flush();

                        //Write password
                        ((vars) appLogin.this.getApplicationContext()).Write().writeObject(params[2]);
                        ((vars) appLogin.this.getApplicationContext()).Write().flush();


                        Object[] return_objects=new Object[3];
                        return_objects[0]=((vars) appLogin.this.getApplicationContext()).Read().readObject();
                        return_objects[1]=params[1];
                        return_objects[2]=params[2];
                        return return_objects;

                    }catch(IOException |ClassNotFoundException ex){ex.printStackTrace(); return null;}

                }
            }
            catch (ConnectException |SocketTimeoutException e){
                System.err.println(e.getLocalizedMessage());
                this.cancel(true);
                return null;
            }
            catch(IOException ex){ex.printStackTrace();}
            return null;
        }

        @Override
        protected void onPostExecute(final Object[] objects) {
            mAuthTask = null;
            showProgress(false);

            if (objects!=null)
            {
                if ((boolean) objects[0])
                {
                    getSharedPreferences("CREDENTIAL", MODE_PRIVATE)
                            .edit()
                            .putString("EMAIL", (String) objects[1])
                            .putString("PASSWORD", (String) objects[2])
                            .apply();

                    Intent intent = new Intent(appLogin.this, Intro.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("loginType", "app");
                    startActivity(intent);
                    finish();
                    finishAffinity();
                }
                else
                {
                    mPasswordView.setError(getString(R.string.error_incorrect_password));
                    mPasswordView.requestFocus();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
            final View rootView = appLogin.this.getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar snackbar= Snackbar.make(rootView,getString(R.string.noNetwork),Snackbar.LENGTH_INDEFINITE).setAction("ΟΚ", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new UserLoginTask().execute(action,email,pass);
                }
            });
            snackbar.show();


        }
    }
}

