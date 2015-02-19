package de.hni_develop.triptracker;

import android.location.Location;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hni_develop.triptracker.Waypoint;

/**
 * Created by HENi on 18.02.15.
 */
public class TripTracker {

    private Waypoint waypoint;


    public TripTracker() {
        //create Waypoint instance
        initializeWaypoint();
    }

    //INITIALIZATION
    public void initializeWaypoint() {
        waypoint = new Waypoint();
    }

    public String getCurrentWaypointId() {
        return waypoint.getId();
    }

    public void setCurrentWaypointPhotoPath(String filePath) {
        waypoint.setPhotoPath(filePath);
    };


    //ACTIONS
    public void actionToggleGpsOnOff() {

    }

    public void actionUpdateGpsPosition(Location mLastLocation) {
        //append last location to Waypoint
        if(mLastLocation != null) {
            waypoint.updatePosition(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
    }

    public void actionTrackWaypoint() {

    }

    public void actionTakePhoto() {

    }

}