package com.legacy07.lalarm;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.legacy07.lalarm.gpsalarm.SetAlarm;


public class settings extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener {

    TextView textlocation;
    EditText setalarmdistance;
    Spinner setalarmmode;
    CheckBox ison;
    Button apply;
    SharedPreferences sharedPreferences;
    Double latitude, longitude;
    String location, selectedmode = "RingTone";
    String[] modes = {"RingTone", "AlarmTone", "NotificationTone"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textlocation = findViewById(R.id.alarmlocation);
        setalarmdistance = findViewById(R.id.alarmdistance);
        setalarmdistance.setHint("in KM");
        setalarmmode = findViewById(R.id.alarmmode);
        ison = findViewById(R.id.setalarmbox);
        apply = findViewById(R.id.apply);

        setalarmmode.setOnItemSelectedListener((AdapterView.OnItemSelectedListener) this);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, modes);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setalarmmode.setAdapter(aa);

        sharedPreferences = getSharedPreferences("l-alarm", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        latitude = Double.parseDouble(sharedPreferences.getString("latitude", "0.0"));
        longitude = Double.parseDouble(sharedPreferences.getString("longitude", "0.0"));
        location = sharedPreferences.getString("location", "unknown");
        textlocation.setText(location);

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        ison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
                    buildAlertMessageNoGps();
                }
            }
        });


        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!setalarmdistance.getText().toString().trim().equals("")) {
                    editor.putString("ringdistance", setalarmdistance.getText().toString().trim());
                    editor.putString("ringmode", selectedmode);

                    if (ison.isChecked()) {

                        editor.putString("isset", "1");
                        editor.apply();
                        Intent intent = new Intent(getApplicationContext(), SetAlarm.class);
                        startActivity(intent);
                    } else {
                        editor.putString("isset", "0");
                        Toast.makeText(getApplicationContext(), "Alarm is not Set !", Toast.LENGTH_LONG).show();
                    }


                }
                else
                    Toast.makeText(getApplicationContext(),"Please fill the field", Toast.LENGTH_LONG).show();

            }
        });


    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id) {

        selectedmode = modes[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

        selectedmode = "RingTone";

    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this,  R.style.MyDialogTheme);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        ison.setChecked(false);
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

}
