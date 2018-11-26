package io.shin.sweetssearch;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;

import io.shin.sweetssearch.lib.GeofenceManager;
import io.shin.sweetssearch.lib.GMap;

/**
 * Created by shin on 2018/01/09.
 */

public class RegisterActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = "RegisterActivity";

    private GoogleApiClient mGoogleApiClient;

    private TextView mTextView;
    private Button mButtonAddGeofence, mButtonRemoveGeofence;
    private GMap.Location location;
    private GeofenceManager geofenceManager;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mIntent = this.getIntent();
        if (mIntent == null || !mIntent.getAction().equals(Intent.ACTION_SEND)) {
            Log.e(TAG, "invalid Intent");
            Toast.makeText(this, "invalid Intent", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mTextView = (TextView) findViewById(R.id.textView);
        mButtonAddGeofence = (Button) findViewById(R.id.buttonAddGefence);
        mButtonRemoveGeofence = (Button) findViewById(R.id.buttonRemoveGefence);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();

        geofenceManager = new GeofenceManager(mGoogleApiClient, this);
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");

        GMap.getLocation(mIntent, new GMap.LocationCallback() {
            @Override
            public void onSuccess(GMap.Location location) {
                Log.i("location", location.toString());
                mTextView.setText(location.toString());
                RegisterActivity.this.location = location;
            }

            @Override
            public void onError(Exception ex) {
                Log.e(TAG, ex.getMessage());
            }
        });

        // geoFenceを登録する
        mButtonAddGeofence.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (location == null || geofenceManager == null) return;
                geofenceManager.add(location, new ResultCallback() {
                    @Override
                    public void onResult(@NonNull Result result) {
                        Toast.makeText(RegisterActivity.this, result.getStatus().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG ,"GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection failed: " + connectionResult.toString());
    }
}
