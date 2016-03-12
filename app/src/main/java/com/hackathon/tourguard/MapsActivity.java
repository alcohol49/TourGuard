package com.hackathon.tourguard;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "MapsActivity";
    private static int sHourOfDay = -1;
    private static int sMinute = -1;

    private GoogleMap mMap;
    private LatLng mLatLng;
    private EditText mFocusView;

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the time chosen by the user
            Log.d(TAG, "get time: " + hourOfDay + " " + minute);
            sHourOfDay = hourOfDay;
            sMinute = minute;
            ((MapsActivity) getActivity()).refreshTime();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button invisible = (Button) findViewById(R.id.invisible);
        invisible.setBackgroundColor(Color.TRANSPARENT);
        invisible.setTextColor(Color.TRANSPARENT);

        EditText start = (EditText) findViewById(R.id.start);
        start.setInputType(InputType.TYPE_NULL);
        start.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mFocusView = (EditText) getCurrentFocus();
            }
        });

        EditText to = (EditText) findViewById(R.id.to);
        to.setInputType(InputType.TYPE_NULL);
        to.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                mFocusView = (EditText) getCurrentFocus();
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());


        mMap.addMarker(new MarkerOptions().position(mLatLng).title("current location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 12));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                refreshLocation(latLng);
            }
        });

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(mLatLng.latitude, mLatLng.longitude, 1);
            EditText start = (EditText) findViewById(R.id.start);
            start.setText(address.get(0).getAddressLine(0));
        } catch (IOException e) {
            Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshLocation(LatLng latLng) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            mFocusView.setText(address.get(0).getAddressLine(0));
        } catch (IOException e) {
            Toast.makeText(this, "IOException", Toast.LENGTH_SHORT).show();
        }
    }

    public void refreshTime() {
        Log.d(TAG, "refreshTime");
        TextView view = (TextView) findViewById(R.id.time);
        view.setText(sHourOfDay + ":" + sMinute);
    }

    public void current(View view) {
        Log.d(TAG, "button current");
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 12));
    }

    public void invisible(View view) {
        Log.d(TAG, "button invisible");
    }

    public void pickTime(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}
