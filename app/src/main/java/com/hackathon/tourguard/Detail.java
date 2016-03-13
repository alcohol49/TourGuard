package com.hackathon.tourguard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Detail extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    public static final String KEY_HEAD = "head";
    public static final String KEY_DISCRIPTION = "discription";
    public static final String KEY_SOURCE = "source";
    public static final String KEY_LAT = "lat";
    public static final String KEY_LNG = "lng";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detial);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        String strHead = getIntent().getExtras().getString(KEY_HEAD);
        String strDiscription = getIntent().getExtras().getString(KEY_DISCRIPTION);
        String strSource = getIntent().getExtras().getString(KEY_SOURCE);

        TextView head = (TextView) findViewById(R.id.head_line);
        head.setText(strHead);
        TextView context = (TextView) findViewById(R.id.context);
        context.setText(strDiscription);
        TextView source = (TextView) findViewById(R.id.source);
        source.setText(strSource);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng taipei101 = new LatLng(25.0335, 121.5641);
        mMap = googleMap;
        mMap.addMarker(new MarkerOptions().position(taipei101));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(taipei101, 12));
    }
}
