package de.hni_develop.triptracker;

import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.location.Location;
import android.view.View;
import android.widget.Toast;
import android.util.Log;
import android.content.Context;
import android.content.Intent;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;


import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;

import de.hni_develop.triptracker.TripTracker;


public class MainActivity extends ActionBarActivity implements
        ConnectionCallbacks, OnConnectionFailedListener {

    //Provides the entry point to Google Play services.
    protected GoogleApiClient mGoogleApiClient;
    protected LocationManager mLocationManager;
    protected ToggleButton mResToggleGpsOnOff;

    protected LocationListener mLocationListener;


    private TripTracker tripTracker;

    private static String TAG_LAST_LOCATION = "LAST-LOCATION";


    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            double latitude = location.getLatitude();
            Log.i("LOCATION", String.valueOf(latitude));
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initializeGoogleApiClient();

        initializeGpsSettings();

        intializeResources();

        //initialize TripTracker
        tripTracker = new TripTracker();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //check if GPS is on
        isGpsOn();

        //connect to API on app start
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //disconnect from API
        if(mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /***************************
     * RESOURCES
     ***************************/
    public void intializeResources() {
        mResToggleGpsOnOff = (ToggleButton) findViewById(R.id.toggle_gpsOnOff);
    }


    /***************************
     * GPS SETTINGS
     ***************************/
    public void initializeGpsSettings() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    }

    public boolean isGpsOn() {
        boolean gpsState = false;

        //get GPS state
        gpsState = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        //update toggle button
        mResToggleGpsOnOff.setChecked(gpsState);

        return gpsState;
    }

    public void toggleGpsOnOff() {
        //request settings
        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
    }


    /***************************
     * GPS LOCATION
     ***************************/
    public Location getCurrentLocation() {
        Criteria providerCriteria = new Criteria();
        String providerSelection = mLocationManager.getBestProvider(providerCriteria, true);

        Looper locationLooper = Looper.myLooper();


        Toast.makeText(this, providerSelection, Toast.LENGTH_LONG).show();
        Log.i("LOCATION", providerSelection);

        Location currentLocation = mLocationManager.getLastKnownLocation(providerSelection);
        mLocationManager.requestSingleUpdate(providerSelection, locationListener, locationLooper);


        return currentLocation;
    }





    /***************************
     * GOOGLE API
     ***************************/
    protected void initializeGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    public void onConnected(Bundle bundle) {
        //get last location and update model
        updateGpsPosition();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG_LAST_LOCATION, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG_LAST_LOCATION, "Connection suspended");
        mGoogleApiClient.connect();
    }


    /***************************
     * LOCATION
     ***************************/
    public Location getLastLocation() {
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.

        //Represents a geographical location
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null) {
            //mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
            //mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
            Toast.makeText(this, String.valueOf(mLastLocation.getLatitude()), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, R.string.no_location_detected, Toast.LENGTH_LONG).show();
        }

        //return last location
        return mLastLocation;
    }


    /***************************
     * CAMERA
     ***************************/
    public void takePhoto() {
        Toast.makeText(this, "TAKE PHOTO", Toast.LENGTH_LONG).show();
        //if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            //send intent to take photo with camera
            dispatchTakePictureIntent();

        //Toast.makeText(this, "TAKE PHOTO", Toast.LENGTH_LONG).show();
        //}
    }

    private void dispatchTakePictureIntent() {
        final int REQUEST_TAKE_PHOTO = 1;

        //define type of intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            //create the file where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Error create photo file", Toast.LENGTH_LONG).show();
            }
            //continue only if the file was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String fileId = tripTracker.getCurrentWaypointId();
        String fileName = "TT_" + fileId;

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                fileName,       /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        //set path of photo in Waypoint (and path for use with ACTION_VIEW intents)
        tripTracker.setCurrentWaypointPhotoPath("file:" + image.getAbsolutePath());

        //return created image
        return image;
    }



    /***************************
     * ACTIONS - forward to TripTracker to handle them
     ***************************/
    public void actionToggleGpsOnOff(View eventSource) {
        toggleGpsOnOff();
    }

    public void actionUpdateGpsPosition(View eventSource) {
        updateGpsPosition();
        Log.i("LOCATION", "BUTTON");
        getLastLocation();
        getCurrentLocation();
    }

    public void updateGpsPosition() {
        //request last location from API
        tripTracker.actionUpdateGpsPosition(getLastLocation());
    }

    public void actionTrackWaypoint() {

    }

    public void actionTakePhoto(View eventSource) {
        Log.i("PHOTO", "BUTTON");
        takePhoto();
    }

}
