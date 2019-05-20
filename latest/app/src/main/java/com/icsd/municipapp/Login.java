package com.icsd.municipapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Locale;

public class Login extends AppCompatActivity {

    CallbackManager callbackManager;
    ImageButton MunicipApp_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences pref = getSharedPreferences("LANGUAGE",MODE_PRIVATE);
        String language = pref.getString("LANGUAGE", null);
        if(language==null)
        {
            getSharedPreferences("LANGUAGE",MODE_PRIVATE)
                    .edit()
                    .putString("PREF_LANGUAGE", "el")
                    .apply();
            language="el";
        }
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        pref = getSharedPreferences("CREDENTIAL",MODE_PRIVATE);
        String username = pref.getString("EMAIL", null);
        String password = pref.getString("PASSWORD", null);

        if(username!=null||password!=null)
        {
            Intent intent = new Intent(Login.this,appLogin.class);
            intent.putExtra("username",username);
            intent.putExtra("password",password);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        }

        setContentView(R.layout.activity_login);
        setTitle(getString(R.string.login_title));

        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email","user_birthday"));
        MunicipApp_login = (ImageButton) findViewById(R.id.MunicipApp_login);
        MunicipApp_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), appLogin.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
            }
        });
        FacebookSdk.sdkInitialize(getApplicationContext());

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                loginResult.getRecentlyGrantedPermissions();
                getUserDetailsFromFB(loginResult.getAccessToken());
            }

            @Override
            public void onCancel()
            {
                System.out.println("Cancel!!");
            }

            @Override
            public void onError(FacebookException exception)
            {
                System.out.println("Error!!");
            }
        });



        if(isLoggedIn())
        {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                getUserDetailsFromFB(accessToken);
        }

    }


    public void getUserDetailsFromFB(AccessToken accessToken)
    {

        GraphRequest req=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback()
        {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response)
            {
                String id=null,name=null,mail=null,bday=null;
                try{
                    id=object.getString("id");
                    System.out.println("graph request error : "+object.getString("id"));
                    System.out.println("graph request error : "+object.getString("id"));
                    System.out.println("graph request error : "+object.getString("id"));
                    System.out.println("graph request error : "+object.getString("id"));
                    System.out.println("graph request error : "+object.getString("id"));
                    System.out.println("graph request error : "+object.getString("id"));
                    name=object.getString("name");
                    mail=object.getString("email");
                    bday=object.getString("birthday");
                    ((vars)Login.this.getApplicationContext()).setFacebookInfo(id,name,mail,bday);
                    Intent intent = new Intent(getApplicationContext(), Intro.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("loginType","facebook");
                    startActivity(intent);
                    finish();
                }catch (JSONException e)
                {
                    System.err.println("graph request error : "+e.getMessage());
                    System.err.println("graph request error : "+e.getMessage());
                    ((vars)Login.this.getApplicationContext()).setFacebookInfo(id,name,mail,bday);
                    Intent intent = new Intent(getApplicationContext(), Intro.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.putExtra("loginType","facebook");
                    startActivity(intent);
                    finish();
                }
                catch(Exception ex){
                    ex.printStackTrace();
                    final View rootView = Login.this.getWindow().getDecorView().findViewById(android.R.id.content);
                    Snackbar snackbar= Snackbar.make(rootView,getString(R.string.noNetwork),Snackbar.LENGTH_INDEFINITE).setAction("ΟΚ", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent=new Intent(Login.this,Login.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    });
                    snackbar.show();
                }


            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,birthday");
        req.setParameters(parameters);
        req.executeAsync();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }



    private boolean isLoggedIn() {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null;
    }


    @Override
    public void onBackPressed() {
        System.exit(0);
    }


}
