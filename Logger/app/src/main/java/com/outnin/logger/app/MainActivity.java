package com.outnin.logger.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements LocationListener {

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);

        boolean enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!enabled) {
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(intent);
        }

        // add PhoneStateListener
        MyPhoneStateListener myPhoneStateListener = new MyPhoneStateListener();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(myPhoneStateListener,PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);

        populateView();
    }

    private void populateView() {
        // Location Spinner
        Spinner locationSpinner = (Spinner) findViewById(R.id.location);
        ArrayAdapter<CharSequence> locationAdapter = ArrayAdapter.createFromResource(this,
                R.array.location_array, android.R.layout.simple_spinner_item);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        locationSpinner.setAdapter(locationAdapter);

        // GPS Provider
        TextView gpsProvider = (TextView) findViewById(R.id.gpsProvider);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            gpsProvider.setText(getResources().getString(R.string.gps_provider_enabled));
        } else {
            gpsProvider.setText(getResources().getString(R.string.gps_provider_disabled));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProviderEnabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            TextView gpsProvider = (TextView) findViewById(R.id.gpsProvider);
            gpsProvider.setText(getResources().getString(R.string.gps_provider_enabled));
        }
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equals(LocationManager.GPS_PROVIDER)) {
            TextView gpsProvider = (TextView) findViewById(R.id.gpsProvider);
            gpsProvider.setText(getResources().getString(R.string.gps_provider_disabled));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        /*if (provider.equals(LocationManager.GPS_PROVIDER)) {
            TextView gpsStatus = (TextView) findViewById(R.id.gpsStatus);
            if (status == LocationProvider.AVAILABLE) {
                gpsStatus.setText(getResources().getString(R.string.gps_status_available));
            } else if (status == LocationProvider.OUT_OF_SERVICE) {
                gpsStatus.setText(getResources().getString(R.string.gps_status_out_of_service));
            } else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE) {
                gpsStatus.setText(getResources().getString(R.string.gps_status_temporarily_unavailable));
            } else {
                gpsStatus.setText(getResources().getString(R.string.unknown));
            }
        }*/
    }

    @Override
    public void onLocationChanged(Location location) {
        float accuracy = location.getAccuracy();
        double lat = location.getLatitude();
        double lon = location.getLongitude();

        String coordinates = Double.toString(lat) + "," + Double.toString(lon);
        TextView gpsCoordinates = (TextView) findViewById(R.id.gpsCoordinates);
        gpsCoordinates.setText(coordinates);

        TextView gpsAccuracy = (TextView) findViewById(R.id.gpsAccuracy);
        gpsAccuracy.setText(Float.toString(accuracy));
    }

    private class MyPhoneStateListener extends PhoneStateListener {
        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength){
            super.onSignalStrengthsChanged(signalStrength);
            int gsmSignalStrengthIntValue = signalStrength.getGsmSignalStrength();
            int gsmSignalStrengthDbmValue = 2*gsmSignalStrengthIntValue - 113; // Based on TS 27.007, section 8.5
            String gsmSignalStrength = Integer.toString(gsmSignalStrengthDbmValue);
            TextView cellSignalStrength = (TextView) findViewById(R.id.cellSignalStrength);
            cellSignalStrength.setText(gsmSignalStrength + " dBm");
        }

        @Override
        public void onServiceStateChanged(ServiceState serviceState) {
            super.onServiceStateChanged(serviceState);
        }
    }
}
