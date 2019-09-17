/***
 Copyright (c) 2012 CommonsWare, LLC
 Licensed under the Apache License, Version 2.0 (the "License"); you may not
 use this file except in compliance with the License. You may obtain a copy
 of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 by applicable law or agreed to in writing, software distributed under the
 License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 OF ANY KIND, either express or implied. See the License for the specific
 language governing permissions and limitations under the License.

 Covered in detail in the book _The Busy Coder's Guide to Android Development_
 https://commonsware.com/Android
 */

package com.legacy07.lalarm;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocPicker extends AbstractMapActivity implements
        OnMapReadyCallback, OnInfoWindowClickListener,
        OnMarkerDragListener {
    private boolean needsInit = false;
    Double finallat = 0.0, finallong = 0.0;
    public static final String MyPREFERENCES = "l-alarm";
    SharedPreferences sharedPreferences;
    Geocoder geocoder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        geocoder = new Geocoder(this, Locale.getDefault());
        sharedPreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        String testlat = sharedPreferences.getString("latitude", null);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (testlat == null) {
            editor.putString("latitude", "9.9527952");
            editor.putString("longitude", "76.3084329");
            editor.putString("ringdistance", "2");
            editor.putString("ringmode", "0");
            editor.putString("isset", "0");
            editor.putString("location", "unknown");
            finallat = 9.9527952;
            finallong = 76.3084329;
            editor.apply();
        } else {
            finallat = Double.valueOf(sharedPreferences.getString("latitude", "empty"));
            finallong = Double.valueOf(sharedPreferences.getString("longitude", "empty"));
        }

        if (readyToGo()) {
            setContentView(R.layout.activity_locpicker);

            MapFragment mapFrag =
                    (MapFragment) getFragmentManager().findFragmentById(R.id.map);

            if (savedInstanceState == null) {
                needsInit = true;
            }

            mapFrag.setRetainInstance(true);
            mapFrag.getMapAsync(this);
        }
    }


    @Override
    public void onMapReady(GoogleMap map) {
        if (needsInit) {

            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(finallat, finallong), 12.0f));
            addMarker(map, finallat, finallong,
                    R.string.majestic,
                    R.string.majestic_snippet);

        }
        map.setOnInfoWindowClickListener(this);
        map.setOnMarkerDragListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Toast.makeText(this, marker.getTitle(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        LatLng position;
        position = marker.getPosition();

        Log.d(getClass().getSimpleName(), String.format("Drag from %f:%f",
                position.latitude,
                position.longitude));

        finallat = position.latitude;
        finallong = position.longitude;
    /*Toast.makeText(LocPicker.this,String.format("Drag from %f:%f",
            position.latitude,
            position.longitude),Toast.LENGTH_LONG).show();*/
    }

    @Override
    public void onMarkerDrag(Marker marker) {

        LatLng position;
        position = marker.getPosition();

        finallat = position.latitude;
        finallong = position.longitude;

  /*  Log.d(getClass().getSimpleName(),
          String.format("Dragging to %f:%f", position.latitude,
                        position.longitude));*/

    /*Toast.makeText(MainActivity.this,String.format("Dragging to %f:%f",
            position.latitude,
            position.longitude),Toast.LENGTH_LONG).show();*/


    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder box = new AlertDialog.Builder(this);
        box.setTitle("Use the dropped location ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        Intent intent = new Intent(getApplicationContext(), settings.class);
                        Log.d("aaaa", finallat + " " + finallong);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("latitude", String.valueOf(finallat));
                        editor.putString("longitude", String.valueOf(finallong));
                        List<Address> addresses = null;
                        try {
                            String cityName;
                            addresses = geocoder.getFromLocation(finallat, finallong, 1);
                            cityName = addresses.get(0).getLocality();
                            String[] separated = cityName.split(",");
                            editor.putString("location","To : "+ separated[0]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        editor.apply();
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setNeutralButton("back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .create().show();

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

        LatLng position;
        position = marker.getPosition();

        Log.d(getClass().getSimpleName(), String.format("Dragged to %f:%f",
                position.latitude,
                position.longitude));

        finallat = position.latitude;
        finallong = position.longitude;
/*

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());


        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(position.latitude, position.longitude, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();

            Toast.makeText(LocPicker.this, "Address: " +
                    address + " " + city, Toast.LENGTH_LONG).show();

            //addMarker();

        } catch (IOException e) {
            e.printStackTrace();
        }*/

    }

    private void addMarker(GoogleMap map, double lat, double lon,
                           int title, int snippet) {
        map.addMarker(new MarkerOptions().position(new LatLng(lat, lon))
                .title(getString(title))
                .snippet(getString(snippet))
                .draggable(true));


    }
}
