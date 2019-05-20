package com.icsd.municipapp;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener,GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, LocationListener {

    private GoogleMap map;
    SupportMapFragment mapFragment;
    Spinner map_type_sp;
    Button confirm_marker;
    PlaceAutocompleteFragment autocompleteFragment;
    Context view;
    Geocoder geocoder;
    AutocompleteFilter typeFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        setContentView(R.layout.activity_maps);

        //connecting to google client api
        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                    }
                })
                .build();
        mGoogleApiClient.connect();

        //find autocomplete fragment
        autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        //add filter in google search
        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_REGIONS|AutocompleteFilter.TYPE_FILTER_ADDRESS|AutocompleteFilter.TYPE_FILTER_CITIES)
                .setCountry("GR")
                .build();

        autocompleteFragment.setFilter(typeFilter);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                map.clear();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17);
                map.animateCamera(cameraUpdate);
                map.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getAddress().toString()));
                place.getLatLng();
                System.err.println("Place: "+place);
            }

            @Override
            public void onError(Status status) {
            }
        });


        ((vars)this.getApplication()).setMap_type(0);
        this.view = this;

        ((vars) this.getApplication()).setMap_type(1);

        confirm_marker = (Button) findViewById(R.id.confirm_marker);

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        map_type_sp = (Spinner) findViewById(R.id.map_type_sp);
        map_type_sp.setOnItemSelectedListener(this);


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.map_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        map_type_sp.setAdapter(adapter);



    }


    @Override
    public void onMapReady(final GoogleMap map) {
        this.map=map;
        this.map.setMapType(((vars)this.getApplication()).getMap_type());
        checkPermission("android.permission.ACCESS_FINE_LOCATION", 1, 1);

        this.map.setMyLocationEnabled(true);
        this.map.setTrafficEnabled(true);
        this.map.setIndoorEnabled(true);
        this.map.setBuildingsEnabled(true);
        this.map.getUiSettings().setScrollGesturesEnabled(true);
        this.map.getUiSettings().setCompassEnabled(true);
        this.map.getUiSettings().setMapToolbarEnabled(false);
        this.map.getUiSettings().setZoomControlsEnabled(false);

        this.map.setOnMapLongClickListener(this);
        this.map.setOnMapClickListener(this);
        this.map.setOnMarkerClickListener(this);
    }


    @Override
    public void onLocationChanged(Location location) {
        Log.e("onLocationChanged", "called");
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 17);
        map.animateCamera(cameraUpdate);
    }


    @Override
    public void onMapLongClick(LatLng latLng){
        map.clear();
        List<Address> addresses=null;
        geocoder = new Geocoder(view, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
        }catch(Exception ignored){}

        try {
            map.addMarker(new MarkerOptions().position(latLng).title(addresses.get(0).getAddressLine(0) + ", " + addresses.get(0).getLocality()));
        }catch(Exception ex){
            map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.no_inet)));
        }
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapClick(LatLng latLng){
        map.clear();
        confirm_marker.setVisibility(View.INVISIBLE);
    }


    @Override
    public boolean onMarkerClick(final Marker marker){
        marker.showInfoWindow();
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        confirm_marker.setVisibility(View.VISIBLE);
        confirm_marker.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                confirm_marker.setVisibility(View.INVISIBLE);
                ((vars)view.getApplicationContext()).setRepLatLng(new LatLng(marker.getPosition().latitude,marker.getPosition().longitude));
                startActivity(new Intent(view,PreviewReport.class));
            }
        });
        return true;
    }






    //Map styles
    @Override
    public void onItemSelected(AdapterView<?> parent, View view,int pos, long id){
        switch (parent.getItemAtPosition(pos).toString()) {
            case "Normal" :
                ((vars)this.getApplication()).setMap_type(1);
                try
                {
                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Hybrid" :
                ((vars)this.getApplication()).setMap_type(4);
                try
                {
                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Satellite" :
                ((vars)this.getApplication()).setMap_type(2);
                try
                {
                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "Terrain" :
                ((vars)this.getApplication()).setMap_type(3);
                try
                {
                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "None" :
                ((vars)this.getApplication()).setMap_type(0);
                try
                {
                    mapFragment = (SupportMapFragment) getSupportFragmentManager()
                            .findFragmentById(R.id.map);
                    mapFragment.getMapAsync(this);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {

    }


    @Override
    public void onBackPressed()
    {
        this.finish();
        super.onBackPressed();
    }


}
