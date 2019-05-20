package com.icsd.municipapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

public class change_locality extends AppCompatActivity {

    PlaceAutocompleteFragment place_autocomplete_fragment;
    AutocompleteFilter typeFilter;
    String locality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_locality);

        locality="";
        place_autocomplete_fragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //add filter in google search
        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .setCountry("GR")
                .build();

        place_autocomplete_fragment.setFilter(typeFilter);
        place_autocomplete_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place)
            {
                locality=String.valueOf(place.getName());
                Intent intent=new Intent(change_locality.this,ReviewReports.class);
                intent.putExtra("locality",locality);
                startActivity(intent);
                change_locality.this.finish();
            }

            @Override
            public void onError(Status status) {
            }
        });


    }
}
