package de.hni_develop.triptracker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by HENi on 18.02.15.
 */
public class Waypoint {
    private String id;
    private Date date;
    private String dateDate;
    private String dateTime;
    private double posLatitude;
    private double posLongitude;
    private String photoPath;

    public Waypoint() {
        //create id from timestamp
        //id = String.valueOf(System.currentTimeMillis());
        id = createId();

        //
    }

    public String createId() {
        //create unique id
        DateFormat formatId = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return formatId.format(new Date());
    }

    public String getId() {
        return id;
    }

    public void setPhotoPath(String filePath) {
        photoPath = filePath;
    }

    public void updateData() {
        //update object data before saving them

        //update date and time
        DateFormat formatDate = new SimpleDateFormat("yyyy-MM-dd");
        DateFormat formatTime = new SimpleDateFormat("HH:mm:ss");

        date = new Date();
        dateDate = formatDate.format(date);
        dateTime = formatTime.format(date);
    }

    public void updatePosition(double latitude, double longitude) {
        posLatitude = latitude;
        posLongitude = longitude;
    }

}
