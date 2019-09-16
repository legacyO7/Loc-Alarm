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
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SetAlarm extends Activity {
    Button btn_stop;
    int destroyed=0;
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission = true;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Bundle b;
    TextView setalarmdisplay;
    Double latitude, longitude, targetlatitude, targetlongitude;
    Geocoder geocoder;
    ProgressBar pd;
    private int requestCode;
    private String[] permissions;
    private int[] grantResults;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_alarm);
        btn_stop = findViewById(R.id.button);
        setalarmdisplay = findViewById(R.id.setalarm);
        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();
        pd=findViewById(R.id.pd);
        b = new Bundle();
        b = getIntent().getExtras();

        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.INTERNET,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            medit.putString("service", "service").commit();
                            assert b != null;
                            {
                                Log.d("bundldlde", b.getDouble("lat") + " " + b.getDouble("lon"));
                                targetlongitude = b.getDouble("lon");
                                targetlatitude = b.getDouble("lat");

                            }

                            Intent intent = new Intent(getApplicationContext(), GoogleService.class);
                            intent.putExtra("targlat",String.valueOf(targetlatitude));
                            intent.putExtra("targlong",String.valueOf(targetlongitude));
                            if(destroyed==0)
                            startService(intent);
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }

                })
                .onSameThread()
                .check();

        assert b != null;
        {
            Log.d("bundldlde", b.getDouble("lat") + " " + b.getDouble("lon"));
            targetlongitude = b.getDouble("lon");
            targetlatitude = b.getDouble("lat");

        }

        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopServiceIntent = new Intent(getApplicationContext(), GoogleService.class);
                stopService(stopServiceIntent);
                Toast.makeText(getApplicationContext(), "Alarm Stopped", Toast.LENGTH_LONG).show();
                Intent mStartActivity = new Intent(getApplicationContext(), MainActivity.class);
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                AlarmManager mgr = (AlarmManager)getApplication().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
                System.exit(0);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        this.requestCode = requestCode;
        this.permissions = permissions;
        this.grantResults = grantResults;
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
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
            setalarmdisplay.setText(separated[0] + " is approximately " +String.format("%.3f", distance/1000) + "km away");
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