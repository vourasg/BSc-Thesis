package com.icsd.municipapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

public class CameraActivity extends AppCompatActivity implements View.OnClickListener{



    ImageView report_image_im;
    ImageButton rotate_img;
    FloatingActionButton image_done_button;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_report);


        image_done_button = (FloatingActionButton) findViewById(R.id.image_done_button);
        report_image_im = (ImageView) findViewById(R.id.report_image_im);

        rotate_img.setOnClickListener(this);
        image_done_button.setOnClickListener(this);

        dispatchTakePictureIntent();

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, 1);
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            ((vars)this.getApplication()).setImage(imageBitmap);
            report_image_im.setImageBitmap(imageBitmap);
        }
    }



    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.image_done_button:
                try{
                    Matrix matrix = new Matrix();
                    matrix.preRotate(report_image_im.getRotation());
                    Bitmap rotated = Bitmap.createBitmap(((vars) this.getApplication()).getImage(), 0, 0, ((vars) this.getApplication()).getImage().getWidth(), ((vars) this.getApplication()).getImage().getHeight(), matrix, true);
                    ((vars) this.getApplication()).setImage(rotated);
                    startActivity(new Intent(this,MapsActivity.class));
                }catch(Exception ex){ex.printStackTrace();}

                break;
        }
    }

}




