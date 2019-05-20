package com.icsd.municipapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.widget.ProfilePictureView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Settings extends AppCompatActivity implements View.OnClickListener
{
    TextInputEditText ProfileName;
    TextInputEditText et_dd;
    TextInputEditText et_mm;
    TextInputEditText et_yyyy;
    LinearLayout bday_ll;
    TextInputEditText email;
    private final int SELECT_PHOTO = 100;
    ImageView image_iv;
    ImageView image_im;
    TextView password_tv;

    Bitmap user_image;
    FloatingActionButton fab_ok;
    ProfilePictureView profilePictureView;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.profile_settings);
        setTitle(getString(R.string.settings_title));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        runOnUiThread(new Runnable() {
            @Override
            public void run()
            {
                profilePictureView = (ProfilePictureView)findViewById(R.id.ProfilePic);
                ProfileName = (TextInputEditText)findViewById(R.id.ProfileName);
                bday_ll = (LinearLayout) findViewById(R.id.bday_ll);
                et_dd = (TextInputEditText) findViewById(R.id.et_dd);
                et_mm = (TextInputEditText) findViewById(R.id.et_mm);
                et_yyyy = (TextInputEditText) findViewById(R.id.et_yyyy);
                email = (TextInputEditText) findViewById(R.id.email);
                image_im=(ImageView) findViewById(R.id.image_im);
                password_tv=(TextView)findViewById(R.id.password_tv);

                ProfileName.clearFocus();
            }});

        if(!((vars)Settings.this.getApplicationContext()).getLogin_type().equals("facebook"))
        {

            image_im.setVisibility(View.VISIBLE);
            profilePictureView.setVisibility(View.GONE);
            password_tv.setVisibility(View.VISIBLE);
            user_image=null;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    email.setText((String)((vars)Settings.this.getApplicationContext()).getUserInfo(1));
                    if(((vars)Settings.this.getApplicationContext()).getUserInfo(2)!=null)
                        ProfileName.setText((String)((vars)Settings.this.getApplicationContext()).getUserInfo(2));

                    ProfileName.clearFocus();
                    ProfileName.setFocusable(false);
                    ProfileName.setFocusableInTouchMode(false);
                    ProfileName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater li = LayoutInflater.from(Settings.this);
                            View promptsView = li.inflate(R.layout.change_username, null);
                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Settings.this);
                            alertDialogBuilder.setView(promptsView);
                            final TextInputEditText ProfileName = (TextInputEditText) promptsView.findViewById(R.id.ProfileName);
                            final TextInputLayout ProfileNameTil=(TextInputLayout)promptsView.findViewById(R.id.ProfileNameTil);
                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("OK",new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog,int id) {
                                            String user_text = (ProfileName.getText()).toString();
                                            ProgressDialog prog_dialog = new ProgressDialog(Settings.this);
                                            prog_dialog.setTitle(getString(R.string.uploading));
                                            prog_dialog.setMessage(getString(R.string.sending_inf));
                                            prog_dialog.setCanceledOnTouchOutside(false);
                                            prog_dialog.setCancelable(false);
                                            prog_dialog.show();
                                            Object[] params = new Object[3];
                                            params[0]="name";
                                            params[1]=user_text;
                                            params[2]=ProfileNameTil;
                                            new Task(dialog,prog_dialog).execute(params);
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    dialog.dismiss();
                                                }

                                            }

                                    );
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                    });
                    bday_ll.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            LayoutInflater li = LayoutInflater.from(Settings.this);
                            View promptsView = li.inflate(R.layout.change_birthday, null);

                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Settings.this);
                            alertDialogBuilder.setView(promptsView);

                            final TextInputEditText dd = (TextInputEditText) promptsView.findViewById(R.id.et_dd);
                            final TextInputEditText mm = (TextInputEditText) promptsView.findViewById(R.id.et_mm);
                            final TextInputEditText yy = (TextInputEditText) promptsView.findViewById(R.id.et_yyyy);
                            final TextInputLayout bdayTil=(TextInputLayout)promptsView.findViewById(R.id.bdayTil);

                            dd.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                    if (s.length() == 2)
                                        et_mm.requestFocus();
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }
                            });
                            mm.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                    if (s.length() == 2)
                                        et_yyyy.requestFocus();
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });
                            yy.addTextChangedListener(new TextWatcher() {
                                public void afterTextChanged(Editable s) {
                                    if (s.length() == 4)
                                        et_yyyy.clearFocus();
                                }

                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                }
                            });

                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("OK",new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog,int id) {
                                            String bday = (mm.getText()).toString()+"/"+(dd.getText()).toString()+"/"+(yy.getText()).toString();
                                            ProgressDialog prog_dialog = new ProgressDialog(Settings.this);
                                            prog_dialog.setTitle(getString(R.string.uploading));
                                            prog_dialog.setMessage(getString(R.string.sending_inf));                                            prog_dialog.setCanceledOnTouchOutside(false);
                                            prog_dialog.setCancelable(false);
                                            prog_dialog.show();
                                            Object[] params = new Object[3];
                                            params[0]="birthday";
                                            params[1]=bday;
                                            params[2]=bdayTil;
                                            new Task(dialog,prog_dialog).execute(params);
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    dialog.dismiss();
                                                }

                                            }

                                    );
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                    });

                    email.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater li = LayoutInflater.from(Settings.this);
                            View promptsView = li.inflate(R.layout.change_email, null);

                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Settings.this);
                            alertDialogBuilder.setView(promptsView);

                            final TextInputEditText email = (TextInputEditText) promptsView.findViewById(R.id.email);
                            final TextInputEditText password = (TextInputEditText) promptsView.findViewById(R.id.password);
                            final TextInputLayout passwordTil=(TextInputLayout)promptsView.findViewById(R.id.passwordTil);
                            final TextInputLayout emailTil=(TextInputLayout)promptsView.findViewById(R.id.emailTil);

                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("OK",new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog,int id) {
                                            ProgressDialog prog_dialog = new ProgressDialog(Settings.this);
                                            prog_dialog.setTitle(getString(R.string.uploading));
                                            prog_dialog.setMessage(getString(R.string.sending_inf));                                            prog_dialog.setCanceledOnTouchOutside(false);
                                            prog_dialog.setCancelable(false);
                                            prog_dialog.show();
                                            Object[] params = new Object[5];
                                            params[0]="email";
                                            params[1]=(password.getText()).toString();
                                            params[2]=(email.getText()).toString();
                                            params[3]=passwordTil;
                                            params[4]=emailTil;
                                            new Task(dialog,prog_dialog).execute(params);
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    dialog.dismiss();
                                                }

                                            }

                                    );
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                    });


                    password_tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater li = LayoutInflater.from(Settings.this);
                            View promptsView = li.inflate(R.layout.change_password,null);

                            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Settings.this);
                            alertDialogBuilder.setView(promptsView);

                            final EditText password_old=(EditText)promptsView.findViewById(R.id.password_old);
                            final TextInputLayout password_old_til=(TextInputLayout)promptsView.findViewById(R.id.password_old_til);
                            final EditText password=(EditText)promptsView.findViewById(R.id.password);
                            final TextInputLayout password_til=(TextInputLayout)promptsView.findViewById(R.id.password_til);
                            final EditText password_re=(EditText)promptsView.findViewById(R.id.password_re);
                            final TextInputLayout password_re_til=(TextInputLayout)promptsView.findViewById(R.id.password_re_til);

                            password.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if(s.length()<4 && s.length()>0)
                                        password_til.setError(getResources().getString(R.string.password_short));
                                    else if(s.length()>=4)
                                        password_til.setError(null);

                                }
                            });
                            password_re.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    if(password.getText().length()>=4 && !String.valueOf(s).equals(String.valueOf(password.getText())))
                                        password_re_til.setError(getResources().getString(R.string.password_match_err));
                                    else if(password.getText().length()>=4 && String.valueOf(s).equals(String.valueOf(password.getText())))
                                        password_re_til.setError(null);
                                }
                            });

                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setPositiveButton("OK",new DialogInterface.OnClickListener()
                                    {
                                        public void onClick(DialogInterface dialog,int id) {
                                            if((password.getText()).toString().equals((password_re.getText()).toString()))
                                            {
                                                ProgressDialog prog_dialog = new ProgressDialog(Settings.this);
                                                prog_dialog.setTitle(getString(R.string.uploading));
                                                prog_dialog.setMessage(getString(R.string.sending_inf));                                                prog_dialog.setCanceledOnTouchOutside(false);
                                                prog_dialog.setCancelable(false);
                                                prog_dialog.show();
                                                Object[] params = new Object[4];

                                                params[0]="password";
                                                params[1]=(password_old.getText()).toString();
                                                params[2]=(password.getText()).toString();
                                                params[3]=password_old_til;
                                                new Task(dialog,prog_dialog).execute(params);
                                            }
                                        }
                                    })
                                    .setNegativeButton(getString(R.string.cancel),
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog,int id) {
                                                    dialog.dismiss();
                                                }

                                            }

                                    );
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();

                        }
                    });



                    if(((vars)Settings.this.getApplicationContext()).getUser_image_temp()!=null){
                        image_im.setImageBitmap(((vars) Settings.this.getApplicationContext()).getUser_image_temp());

                    }
                    else if(((vars)Settings.this.getApplicationContext()).getUserInfo(4)!=null)
                        image_im.setImageBitmap((Bitmap)((vars)Settings.this.getApplicationContext()).getUserInfo(4));
                    if(((vars)Settings.this.getApplicationContext()).getUserInfo(3)!=null)
                    {
                        String[] s= ((String)((vars)Settings.this.getApplicationContext()).getUserInfo(3)).split("/");
                        if(s.length==1)
                            s=((String)((vars)Settings.this.getApplicationContext()).getUserInfo(3)).split("-");
                        et_dd.setText(s[0]);
                        et_mm.setText(s[1]);
                        et_yyyy.setText(s[2]);
                    }
                }});


            image_im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                    photoPickerIntent.setType("image/*");
                    startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                }
            });




        }
        else
        {
            image_im.setVisibility(View.GONE);
            password_tv.setVisibility(View.GONE);
            profilePictureView.setVisibility(View.VISIBLE);
            profilePictureView.setProfileId(((vars)this.getApplicationContext()).getFacebookInfo(1));
            email.setText((((vars)this.getApplicationContext()).getFacebookInfo(3)!=null?((vars)this.getApplicationContext()).getFacebookInfo(3):""));
            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    email.setError(getResources().getString(R.string.settings_facebook_error));
                }
            });

            if(((vars)this.getApplicationContext()).getFacebookInfo(2)!=null) {
                ProfileName.setText(((vars) this.getApplicationContext()).getFacebookInfo(2));
                ProfileName.setFocusable(false);
                ProfileName.setClickable(true);
                ProfileName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ProfileName.setError(getResources().getString(R.string.settings_facebook_error));
                    }
                });
            }
            et_dd.setClickable(false);
            et_dd.setFocusable(false);
            et_mm.setClickable(false);
            et_mm.setFocusable(false);
            et_yyyy.setClickable(false);
            et_yyyy.setFocusable(false);
            if(((vars)this.getApplicationContext()).getFacebookInfo(4)!=null)
            {
                String[] s= (((vars)this.getApplicationContext()).getFacebookInfo(4)).split("/");
                et_dd.setText(s[1]);
                et_mm.setText(s[0]);
                et_yyyy.setText(s[2]);

                et_dd.setFocusable(false);
                et_dd.setClickable(true);
                et_mm.setFocusable(false);
                et_mm.setClickable(true);
                et_yyyy.setFocusable(false);
                et_yyyy.setClickable(true);

                et_dd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_dd.setError(getResources().getString(R.string.settings_facebook_error));
                    }
                });

                et_mm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_mm.setError(getResources().getString(R.string.settings_facebook_error));
                    }
                });

                et_yyyy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        et_yyyy.setError(getResources().getString(R.string.settings_facebook_error));
                    }
                });
            }
        }

    }




    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.et_dd || v.getId()==R.id.et_mm|| v.getId()==R.id.et_yyyy)
        {
            LayoutInflater li = LayoutInflater.from(Settings.this);
            View promptsView = li.inflate(R.layout.change_birthday, null);

            final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Settings.this);
            alertDialogBuilder.setView(promptsView);

            final TextInputEditText dd = (TextInputEditText) promptsView.findViewById(R.id.et_dd);
            final TextInputEditText mm = (TextInputEditText) promptsView.findViewById(R.id.et_mm);
            final TextInputEditText yy = (TextInputEditText) promptsView.findViewById(R.id.et_yyyy);
            final TextInputLayout bdayTil=(TextInputLayout)promptsView.findViewById(R.id.bdayTil);

            dd.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (s.length() == 2)
                        mm.requestFocus();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });
            mm.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (s.length() == 2)
                        yy.requestFocus();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });
            yy.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    if (s.length() == 4)
                        yy.clearFocus();
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog,int id) {
                            String bday = (mm.getText()).toString()+"/"+(dd.getText()).toString()+"/"+(yy.getText()).toString();
                            ProgressDialog prog_dialog = new ProgressDialog(Settings.this);
                            prog_dialog.setTitle(getString(R.string.uploading));
                            prog_dialog.setMessage(getString(R.string.sending_inf));
                            prog_dialog.setCanceledOnTouchOutside(false);
                            prog_dialog.setCancelable(false);
                            prog_dialog.show();
                            Object[] params = new Object[3];
                            params[0]="birthday";
                            params[1]=bday;
                            params[2]=bdayTil;
                            new Task(dialog,prog_dialog).execute(params);
                        }
                    })
                    .setNegativeButton(getString(R.string.cancel),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.dismiss();
                                }

                            }

                    );
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)  {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO: {
                try {

                    if (resultCode == RESULT_OK)
                    {
                        setContentView(R.layout.profile_image);

                        fab_ok=(FloatingActionButton)findViewById(R.id.fab_ok);

                        Uri selectedImage = imageReturnedIntent.getData();
                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);

                        image_iv = (ImageView) findViewById(R.id.image);
                        int maxWidth=Math.round((new Canvas()).getMaximumBitmapWidth()/8);
                        maxWidth--;
                        int maxHeight=Math.round((new Canvas()).getMaximumBitmapHeight()/8);
                        maxHeight--;
                        Bitmap image=(BitmapFactory.decodeStream(imageStream));

                        Bitmap imageScaled;

                        float ratio = Math.min((float)maxWidth / image.getWidth(), (float)maxHeight / image.getHeight());
                        if(ratio<=1)
                        {

                            imageScaled=Bitmap.createScaledBitmap(image, Math.round( ratio * image.getWidth()),
                                    Math.round(ratio * image.getHeight()), false);
                            int resolution;
                            do
                            {
                                resolution=imageScaled.getWidth()*imageScaled.getHeight();
                                if(resolution>1000000)
                                    imageScaled=Bitmap.createScaledBitmap(imageScaled, (int) Math.round( imageScaled.getWidth()/1.25),
                                            (int) Math.round((imageScaled.getHeight()/1.25)), false);
                                resolution=imageScaled.getWidth()*imageScaled.getHeight();
                            }while(resolution>1000000);


                            ExifInterface exif = null;
                            try {
                                exif = new ExifInterface(getRealPathFromURI(this,selectedImage));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            @SuppressWarnings("ConstantConditions")
                            String orientationString = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                            int orientation = orientationString != null ? Integer.parseInt(orientationString) : ExifInterface.ORIENTATION_NORMAL;
                            int rotateangle = 0;
                            if(orientation == ExifInterface.ORIENTATION_ROTATE_90)
                                rotateangle = 90;
                            if(orientation == ExifInterface.ORIENTATION_ROTATE_180)
                                rotateangle = 180;
                            if(orientation == ExifInterface.ORIENTATION_ROTATE_270)
                                rotateangle = 270;

                            Matrix matrix = new Matrix();
                            matrix.preRotate(rotateangle);
                            image_iv.setImageBitmap(Bitmap.createBitmap(imageScaled, 0, 0, imageScaled.getWidth(),imageScaled.getHeight(), matrix, true));
                            user_image=(Bitmap.createBitmap(imageScaled, 0, 0, imageScaled.getWidth(),imageScaled.getHeight(), matrix, true));
                            ((vars)Settings.this.getApplicationContext()).setUser_image_temp(user_image);
                        }
                        else {
                            int resolution;
                            do
                            {
                                resolution=image.getWidth()*image.getHeight();
                                if(resolution>1000000)
                                    image=Bitmap.createScaledBitmap(image, (int) Math.round( image.getWidth()/1.25),
                                            (int) Math.round((image.getHeight()/1.25)), false);
                                resolution=image.getWidth()*image.getHeight();
                            }while(resolution>1000000);

                            ExifInterface exif = null;
                            try {
                                exif = new ExifInterface(getRealPathFromURI(this,selectedImage));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            @SuppressWarnings("ConstantConditions")
                            String orientstring = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
                            int orientation = orientstring != null ? Integer.parseInt(orientstring) : ExifInterface.ORIENTATION_NORMAL;
                            int rotateangle = 0;
                            if(orientation == ExifInterface.ORIENTATION_ROTATE_90)
                                rotateangle = 90;
                            if(orientation == ExifInterface.ORIENTATION_ROTATE_180)
                                rotateangle = 180;
                            if(orientation == ExifInterface.ORIENTATION_ROTATE_270)
                                rotateangle = 270;

                            Matrix matrix = new Matrix();
                            matrix.preRotate(rotateangle);
                            image_iv.setImageBitmap(Bitmap.createBitmap(image, 0, 0, image.getWidth(),image.getHeight(), matrix, true));
                            user_image=Bitmap.createBitmap(image, 0, 0, image.getWidth(),image.getHeight(), matrix, true);
                            ((vars)Settings.this.getApplicationContext()).setUser_image_temp(user_image);
                        }

                        fab_ok.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ProgressDialog prog_dialog = new ProgressDialog(Settings.this);
                                prog_dialog.setTitle(getString(R.string.uploading));
                                prog_dialog.setMessage(getString(R.string.sending_inf));
                                prog_dialog.setCanceledOnTouchOutside(false);
                                prog_dialog.setCancelable(false);
                                prog_dialog.show();
                                Object[] params=new Object[2];
                                params[0]="image";
                                params[1]=user_image;
                                new Task(null,prog_dialog).execute(params);
                            }
                        });
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }




    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            @SuppressWarnings("ConstantConditions")
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }




    private class Task extends AsyncTask<Object, Void, Object[]> {
        ProgressDialog prog_dialog;
        final DialogInterface dialog_inter;
        Task(final DialogInterface dialog_inter,ProgressDialog prog_dialog)
        {
            this.prog_dialog=prog_dialog;
            this.dialog_inter=dialog_inter;
        }

        @Override
        protected Object[] doInBackground(Object... params) {
            try {

                Socket socket = new Socket();
                socket.connect(new InetSocketAddress("83.212.111.82", 4444),10000);
                //Set outputStream
                ObjectOutputStream out =  new ObjectOutputStream(socket.getOutputStream());
                //Set inputStream
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                out.writeObject("RECONNECT");
                out.flush();
                out.writeObject(((vars) Settings.this.getApplicationContext()).getReconnectString());
                out.flush();

                String connected=(String)in.readObject();
                if("reconnected".equals(connected)) {
                    out.writeObject("SET_INFO");
                    out.flush();

                    out.writeObject(params[0]);
                    out.flush();
                    if (params[0].equals("password") || params[0].equals("image") || params[0].equals("email")) {
                        if (params[0].equals("image")) {
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            ((Bitmap) params[1]).compress(Bitmap.CompressFormat.JPEG, 100, stream);
                            byte[] byteArray = stream.toByteArray();
                            out.writeObject(byteArray);
                            out.flush();
                        } else {
                            out.writeObject(params[1]);
                            out.flush();
                            out.writeObject(params[2]);
                            out.flush();
                        }
                    } else {
                        out.writeObject(params[1]);
                        out.flush();
                    }

                    prog_dialog.dismiss();
                    Object[] result = new Object[params.length + 1];
                    System.arraycopy(params, 0, result, 1, params.length);
                    result[0] = in.readObject();
                    return result;
                }
                else
                    return null;

            } catch (IOException |ClassNotFoundException ex) {
                ex.printStackTrace();
                prog_dialog.dismiss();
                return new Object[0];
            }

        }

        @Override
        protected void onPostExecute(final Object[] result)
        {
            if(result==null){
                AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
                alert.setTitle(getString(R.string.reconnect));
                alert.setMessage(getString(R.string.reconnect_msg));
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                        startActivity(new Intent(Settings.this,Login.class));
                        cancel(true);
                        finish();
                    }
                });
            }
            else {
                AlertDialog.Builder alert = new AlertDialog.Builder(Settings.this);
                alert.setTitle("Upload");

                if (result[1].equals("name")) {
                    if (result[0].equals("name_ok")) {
                        alert.setIcon(R.drawable.done);
                        alert.setMessage(getString(R.string.name_ok));
                        ((vars) Settings.this.getApplicationContext()).setUserInfo(null, (String) result[2], null, null);
                    } else {
                        alert.setIcon(R.drawable.report_comment);
                        alert.setMessage(getString(R.string.name_error));
                        //((TextInputLayout) result[3]).setError(getString(R.string.name_error));
                    }
                } else if (result[1].equals("birthday")) {
                    if (result[0].equals("bday_ok")) {
                        alert.setIcon(R.drawable.done);
                        alert.setMessage(getString(R.string.bday_ok));
                        ((vars) Settings.this.getApplicationContext()).setUserInfo(null, null, (String) result[2], null);
                    } else {
                        alert.setIcon(R.drawable.report_comment);
                        alert.setMessage(getString(R.string.bday_error));
                        //((TextInputLayout) result[3]).setError(getString(R.string.bday_error));
                    }
                } else if (result[1].equals("email")) {
                    if (result[0].equals("mail_ok")) {
                        alert.setIcon(R.drawable.done);
                        alert.setMessage(getString(R.string.mail_ok));
                        ((vars) Settings.this.getApplicationContext()).setUserInfo((String) result[2], null, null, null);
                    } else if (result[0].equals("wrong_password")) {
                        alert.setIcon(R.drawable.report_comment);
                        alert.setMessage(getString(R.string.old_pass_wrong));
                        //((TextInputLayout) result[4]).setError(getString(R.string.old_pass_wrong));
                    } else {
                        alert.setIcon(R.drawable.report_comment);
                        alert.setMessage(getString(R.string.mail_error));
                        //((TextInputLayout) result[5]).setError(getString(R.string.mail_error));
                    }
                } else if (result[1].equals("password")) {
                    if (result[0].equals("password_ok")) {
                        alert.setIcon(R.drawable.done);
                        alert.setMessage(getString(R.string.password_ok));
                    } else {
                        alert.setIcon(R.drawable.report_comment);
                        alert.setMessage(getString(R.string.old_pass_wrong));
                        //((TextInputLayout) result[4]).setError(getString(R.string.old_pass_wrong));
                    }
                } else {
                    if (result[0].equals("image_ok")) {
                        alert.setIcon(R.drawable.done);
                        alert.setMessage(getString(R.string.image_ok));
                        ((vars) Settings.this.getApplicationContext()).setUserInfo(null, null, null, (Bitmap) result[2]);
                    } else {
                        alert.setIcon(R.drawable.report_comment);
                        alert.setMessage(getString(R.string.image_error));
                    }
                    image_iv.setImageBitmap(null);
                }
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (result[0].equals("name_ok") || result[0].equals("bday_ok") || result[0].equals("mail_ok") || result[0].equals("password_ok") || result[0].equals("image_ok")) {
                            if (null != dialog_inter)
                                dialog_inter.dismiss();
                            Intent intent = new Intent(Settings.this, Settings.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("settings", "profile");
                            startActivity(intent);
                            Settings.this.finish();
                        }
                    }
                });
                alert.show();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(Settings.this,MainScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        Settings.this.finish();
    }
}
