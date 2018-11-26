package io.shin.sweetssearch.lib;

import android.Manifest;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;


import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;


import io.shin.sweetssearch.GeofenceReceiveService;

/**
 * Created by shin on 2018/01/09.
 */

public class GeofenceManager {
    private final static String TAG = "GeofenceManager";
    private GoogleApiClient client;
    private Context context;


    public GeofenceManager(GoogleApiClient client, Context context) {
        this.client = client;
        this.context = context;
    }

    public void add(GMap.Location location, ResultCallback callback) {
        Log.i(TAG, "add " + location.toString());

        String id = location.url.toString() + "\t" + location.name;
        Geofence fence = new Geofence.Builder()
                .setRequestId(id)
                .setCircularRegion(  // 500メートル以内
                        location.latitude, location.longitude, 500)
                .setExpirationDuration(  // 期限を指定できる
                        Geofence.NEVER_EXPIRE)
                .setTransitionTypes(
                        Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .addGeofence(fence)  //  Geofenceを一つ登録
                .build();

        // IntentServiceで受け取る
        Intent intent = new Intent(context, GeofenceReceiveService.class);

        PendingIntent pIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        LocationServices.GeofencingApi
                .addGeofences(client, request, pIntent)
                .setResultCallback(callback);
    }

    public void remove(GMap.Location location, ResultCallback callback){

    }

}
