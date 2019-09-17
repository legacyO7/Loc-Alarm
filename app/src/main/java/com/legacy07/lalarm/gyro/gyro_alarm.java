package com.legacy07.lalarm.gyro;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.legacy07.lalarm.MainActivity;
import com.legacy07.lalarm.R;
import com.legacy07.lalarm.services.GoogleService;
import com.legacy07.lalarm.services.gyroservice;

public class gyro_alarm extends Activity {

    private TextView view;
    Button start,stop;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyro_alarm);
        view = findViewById(R.id.textView);
        start = findViewById(R.id.start);
        stop=findViewById(R.id.stop);
        stop.setVisibility(View.INVISIBLE);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), gyroservice.class);
                    startService(intent);
                    Toast.makeText(getApplicationContext(), "Alarm Service started successfully", Toast.LENGTH_LONG).show();

                    view.setVisibility(View.INVISIBLE);
                    start.setVisibility(View.INVISIBLE);
                    stop.setVisibility(View.VISIBLE);
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopServiceIntent = new Intent(getApplicationContext(), gyroservice.class);
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


}