package com.icsd.municipapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.transition.Scene;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class SelectReport extends AppCompatActivity implements View.OnClickListener{

    TextInputLayout cat_til;
    TextInputLayout type_til;
    TextInputLayout description_til;
    EditText cat_et;
    EditText type_et;
    EditText description_et;
    TextView cat_tv;
    TextView type_tv;
    TextView description_tv;
    ImageButton proceed;
    String[] categories;
    String[] types;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_report);

        cat_tv = (TextView) findViewById(R.id.cat_tv) ;
        cat_et = (EditText) findViewById(R.id.cat_et);
        cat_til = (TextInputLayout) findViewById(R.id.cat_til);
        type_tv = (TextView) findViewById(R.id.type_tv) ;
        type_et = (EditText) findViewById(R.id.type_et);
        type_til = (TextInputLayout) findViewById(R.id.type_til) ;
        description_tv = (TextView) findViewById(R.id.description_tv) ;
        description_et = (EditText) findViewById(R.id.description_et);
        description_til = (TextInputLayout) findViewById(R.id.description_til) ;
        proceed = (ImageButton) findViewById(R.id.proceed);

        if(TextUtils.isEmpty(String.valueOf(cat_et.getText().toString())))
        {
            proceed.setVisibility(View.INVISIBLE);
            type_tv.setVisibility(View.INVISIBLE);
            type_et.setVisibility(View.INVISIBLE);
            type_til.setVisibility(View.INVISIBLE);
            description_tv.setVisibility(View.INVISIBLE);
            description_et.setVisibility(View.INVISIBLE);
            description_til.setVisibility(View.INVISIBLE);
        }
        if(TextUtils.isEmpty(String.valueOf(type_et.getText().toString()))) {
            proceed.setVisibility(View.INVISIBLE);
            description_til.setVisibility(View.INVISIBLE);

        }



        cat_et.setOnClickListener(this);
        cat_til.setOnClickListener(this);
        type_et.setOnClickListener(this);
        type_til.setOnClickListener(this);
        proceed.setOnClickListener(this);

        // Creating adapter for spinner
        categories = new String[0];
        categories=(this.getResources().getStringArray(R.array.categories));

        types = new String[0];
        types=(this.getResources().getStringArray(R.array.map_types));
    }



    @Override
    public void onClick(View v)
    {
        if(v.getId()==R.id.cat_til || v.getId()==R.id.cat_et)
            DialogView("category");
        else if (v.getId()==R.id.type_til || v.getId()==R.id.type_et)
            DialogView("type");
        else if(v.getId()==R.id.proceed)
        {
            if(String.valueOf(description_et.getText()).equals("")||String.valueOf(description_et.getText()).equals(" "))
                ((vars)this.getApplicationContext()).setDescription(getString(R.string.no_description));
            else
                ((vars)this.getApplicationContext()).setDescription(String.valueOf(description_et.getText()));
            ((vars)this.getApplicationContext()).setReport_cat(String.valueOf(cat_et.getText()));
            ((vars)this.getApplicationContext()).setReport_type(String.valueOf(type_et.getText()));
            startActivity(new Intent(this,ImageLoader.class));
        }

    }

    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this,MainScreen.class));
        this.finish();
    }



    private void DialogView(String flag) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(SelectReport.this);

        if(flag.equals("category"))
        {
            builder.setTitle(getResources().getString(R.string.report_category));
            builder.setSingleChoiceItems(categories, -1,
                    new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int item)
                        {

                            if(type_tv.getVisibility()==View.INVISIBLE)
                            {
                                type_tv.setVisibility(View.VISIBLE);
                                type_et.setVisibility(View.VISIBLE);
                                type_til.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                if(!String.valueOf(cat_et.getText()).equals(categories[item]))
                                {
                                    type_et.setText("");
                                    description_tv.setVisibility(View.INVISIBLE);
                                    description_et.setVisibility(View.INVISIBLE);
                                    description_til.setVisibility(View.INVISIBLE);
                                    proceed.setVisibility(View.INVISIBLE);

                                }
                            }
                            switch(item)
                            {
                                case 0:
                                    types=(SelectReport.this.getResources().getStringArray(R.array.General));
                                    break;
                                case 1:
                                    types=(SelectReport.this.getResources().getStringArray(R.array.Road_transport_Lighting));
                                    break;
                                case 2:
                                    types=(SelectReport.this.getResources().getStringArray(R.array.Environmental_Issues_Cleanliness));
                                    break;
                                case 3:
                                    types=(SelectReport.this.getResources().getStringArray(R.array.Works));
                                    break;
                                case 4:
                                    types=(SelectReport.this.getResources().getStringArray(R.array.Maintenance));
                                    break;
                            }
                            cat_et.setText(categories[item]);
                            dialog.dismiss();
                        }
                    });
        }
        else
        {

            builder.setTitle(getResources().getString(R.string.report_category));
            builder.setSingleChoiceItems(types, -1,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int item) {

                            type_et.setText(types[item]);
                            description_tv.setVisibility(View.VISIBLE);
                            description_et.setVisibility(View.VISIBLE);
                            description_til.setVisibility(View.VISIBLE);
                            proceed.setVisibility(View.VISIBLE);
                            dialog.dismiss();
                        }
                    });
        }
        AlertDialog alert = builder.create();
        alert.show();
    }
}
