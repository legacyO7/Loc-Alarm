package com.legacy07.lalarm;

import android.*;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.preference.PreferenceManager;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SetAlarm extends Activity {
    Button btn_stop;
    private static final int REQUEST_PERMISSIONS = 100;
    SharedPreferences mPref, sharedPreferences;
    SharedPreferences.Editor medit, spedit;
    TextView setalarmdisplay;
    Double latitude, longitude, targetlatitude, targetlongitude;
    Geocoder geocoder;
    ProgressBar pd;
    int ison;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        btn_stop = findViewById(R.id.button);
        setalarmdisplay = findViewById(R.id.setalarm);
        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();
        pd = findViewById(R.id.pd);
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {

                        medit.putString("service", "service").commit();

                        sharedPreferences = getSharedPreferences("l-alarm", MODE_PRIVATE);
                        targetlatitude = Double.parseDouble(sharedPreferences.getString("latitude", "0.0"));
                        targetlongitude = Double.parseDouble(sharedPreferences.getString("longitude", "0.0"));
                        ison = Integer.parseInt(sharedPreferences.getString("isset", "0.0"));

                        if (ison == 1) {
                            Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                            Toast.makeText(getApplicationContext(),"Alarm service started successfully", Toast.LENGTH_LONG).show();
                            startService(intent);

                        } else {
                            setalarmdisplay.setText("Alarm isnt set. Unable to start service \n:(");
                        }


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {

                        openSettings();

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopServiceIntent = new Intent(getApplicationContext(), GoogleService.class);
                stopService(stopServiceIntent);
                Toast.makeText(getApplicationContext(), "Alarm Stopped", Toast.LENGTH_LONG).show();
                Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager) getApplication().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        });
    }

    private void openSettings() {
        Intent intent = new Intent();
        intent.setAction(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package",
                BuildConfig.APPLICATION_ID, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String cityName = null;


            latitude = Double.valueOf(intent.getStringExtra("latutide"));
            longitude = Double.valueOf(intent.getStringExtra("longitude"));

            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(targetlatitude, targetlongitude, 1);
                cityName = addresses.get(0).getLocality();


            } catch (IOException e) {
                e.printStackTrace();
            }
            Location startPoint = new Location("start");
            startPoint.setLatitude(latitude);
            startPoint.setLongitude(longitude);

            Location endPoint = new Location("end");
            endPoint.setLatitude(targetlatitude);
            endPoint.setLongitude(targetlongitude);

            double distance = startPoint.distanceTo(endPoint);
            String[] separated = cityName.split(",");
            pd.setVisibility(View.INVISIBLE);
            setalarmdisplay.setText(separated[0] + " is approximately " + String.format("%.3f", distance / 1000) + "km away");
           /* if (distance < 1000 || distance < 500 || distance < 100) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            }*/
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(GoogleService.str_receiver));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onBackPressed() {

        finish();
        super.onBackPressed();
    }
}