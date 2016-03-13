package com.hackathon.tourguard;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        final MarkerOptions marker = new MarkerOptions().position(mLatLng).title("current location");
        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 12));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                String title = mFocusView.equals((EditText) findViewById(R.id.start)) ?
                        "Start" : "To";
                marker.position(latLng).title(title);
                mMap.addMarker(marker);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                // TODO: Settings
            default:
                return super.onOptionsItemSelected(item);
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
        mMap.clear();
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        final MarkerOptions marker = new MarkerOptions().position(mLatLng).title("current location");
        mMap.addMarker(marker);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 12));
    }

    public void invisible(View view) {
        Log.d(TAG, "button invisible");
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_warning_black_24dp)
                        .setContentTitle("安心上路通知！ TourGuard!")
                        .setContentText("附近有道路施工！ Road Construction Near By!")
                        .setAutoCancel(true);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, Detail.class)
                .putExtra(Detail.KEY_HEAD, "道路施工")
                .putExtra(Detail.KEY_DISCRIPTION, "忠孝東路施工中，預計完工日期 2016/3/14。")
                .putExtra(Detail.KEY_SOURCE, "資料來源：交通部");

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MapsActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(49, mBuilder.build());

    }

    public void pickTime(View view) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public void nearby(View view) {
        startActivity(new Intent(this, NearbySituation.class));
    }

    public void startGuard(View view) {
        startService(new Intent(this, NotificationIntentService.class)
                .setAction(NotificationIntentService.ACTION_FOO));
        finish();
    }
}
