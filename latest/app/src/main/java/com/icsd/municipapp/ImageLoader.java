package com.icsd.municipapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class ImageLoader extends AppCompatActivity implements View.OnClickListener{

    private static final int SELECT_PHOTO = 100;
    ImageButton album_button;
    ImageButton camera_button;
    ImageButton image_done_button;
    TextView image_skip_tv,image_comment_tv;
    ImageView report_image_im;
    static String stage;
    Uri selectedImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_loader);

        stage="start";

        album_button = (ImageButton) findViewById(R.id.album_button);
        camera_button = (ImageButton) findViewById(R.id.camera_button);
        image_skip_tv = (TextView) findViewById(R.id.image_skip_tv);
        report_image_im = (ImageView) findViewById(R.id.report_image_im);

        album_button.setOnClickListener(this);
        camera_button.setOnClickListener(this);
        image_skip_tv.setOnClickListener(this);
    }



    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.album_button:
                stage="album";
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, SELECT_PHOTO);
                break;

            case R.id.camera_button:
                startActivity(new Intent(this,CameraActivity.class));
                break;

            case R.id.image_skip_tv:
                ((vars) this.getApplication()).setImage(null);
                startActivity(new Intent(this,MapsActivity.class));
                break;

            case R.id.image_done_button:
                try{
                    if(String.valueOf(image_comment_tv.getText()).equalsIgnoreCase("")||String.valueOf(image_comment_tv.getText()).equalsIgnoreCase(" "))
                        ((vars) this.getApplication()).setImage_comment(getString(R.string.no_description));
                    else
                        ((vars) this.getApplication()).setImage_comment(String.valueOf(image_comment_tv.getText()));
                    report_image_im.setImageBitmap(null);
                    startActivity(new Intent(this,MapsActivity.class));
                }catch(Exception ex){
                    ex.printStackTrace();
                }

                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent)  {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch(requestCode) {
            case SELECT_PHOTO: {
                try {
                    if (resultCode == RESULT_OK) {
                        stage="selected";
                        selectedImage = imageReturnedIntent.getData();

                        InputStream imageStream = getContentResolver().openInputStream(selectedImage);

                        Bitmap image=(BitmapFactory.decodeStream(imageStream));

                        setContentView(R.layout.image_report);
                        image_done_button = (FloatingActionButton) findViewById(R.id.image_done_button);

                        image_done_button.setOnClickListener(this);

                        report_image_im = (ImageView) findViewById(R.id.report_image_im);


                        int maxWidth=Math.round((new Canvas()).getMaximumBitmapWidth()/8);
                        maxWidth--;
                        int maxHeight=Math.round((new Canvas()).getMaximumBitmapHeight()/8);
                        maxHeight--;

                        Bitmap imageScaled;
                        float ratio = Math.min((float)maxWidth / image.getWidth(), (float)maxHeight / image.getHeight());
                        if(ratio<=1)
                        {
                            imageScaled=Bitmap.createScaledBitmap(image, Math.round((float) ratio * image.getWidth()),
                                    Math.round((float) ratio * image.getHeight()), false);
                            ExifInterface exif = null;
                            try {
                                exif = new ExifInterface(getRealPathFromURI(this,selectedImage));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
                            report_image_im.setImageBitmap(Bitmap.createBitmap(imageScaled, 0, 0, imageScaled.getWidth(),imageScaled.getHeight(), matrix, true));
                            ((vars)this.getApplicationContext()).setImage(Bitmap.createBitmap(imageScaled, 0, 0, imageScaled.getWidth(),imageScaled.getHeight(), matrix, true));
                            imageScaled.recycle();
                        }
                        else {
                            ExifInterface exif = null;
                            try {
                                exif = new ExifInterface(getRealPathFromURI(this,selectedImage));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
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
                            report_image_im.setImageBitmap(Bitmap.createBitmap(image, 0, 0, image.getWidth(),image.getHeight(), matrix, true));

                            ((vars)this.getApplicationContext()).setImage(Bitmap.createBitmap(image, 0, 0, image.getWidth(),image.getHeight(), matrix, true));
                        }

                        image_comment_tv = (TextView)findViewById(R.id.image_description);






                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void onBackPressed()
    {

        if(stage.equals("selected"))
        {
            stage="album";
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
        else
        {
            super.onBackPressed();
            this.finish();
        }

    }



    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }



}
