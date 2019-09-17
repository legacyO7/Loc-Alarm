package com.legacy07.lalarm;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.preference.PreferenceManager;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.legacy07.lalarm.gpsalarm.LocPicker;
import com.legacy07.lalarm.gyro.gyro_alarm;

import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQUEST_PERMISSIONS = 100;
    boolean boolean_permission;
    SharedPreferences mPref;
    SharedPreferences.Editor medit;
    Button locala,gyroalarm;
    Double latitude, longitude;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gyroalarm=findViewById(R.id.gyroalarm);
        geocoder = new Geocoder(this, Locale.getDefault());
        mPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        medit = mPref.edit();
        locala = findViewById(R.id.locationalarm);

        locala.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LocPicker.class);
                startActivity(intent);
                finish();
            }
        });

        gyroalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, gyro_alarm.class);
                startActivity(intent);
                finish();

            }
        });
    }
}