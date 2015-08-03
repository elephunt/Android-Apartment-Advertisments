package com.example.evgeni.apartmentadvertisments;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;
import java.util.List;

/**
 * Created by evgeni on 4/13/2015.
 */

/**
 * Service for showing new Adv.
 * Notify for new Adv.
 */
public class WaitForNotification extends Service {

    private NotificationManager mManager;
    private static int uniqueID = 1;

    @Override
    public IBinder onBind(Intent arg0)
    {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate()
    {

        // TODO Auto-generated method stub
        super.onCreate();


    }
    @Override
    public void onStart(Intent intent, int startId)
    {
        super.onStart(intent, startId);
        Parse.initialize(this, "zDAftBiv6bLs4eMSWHy5mDIv2inREormFXNSJlhZ", "aOUskiEi8jvpynbajRFPfMtKQFudlvB6Ejh85YiG");
        checkForNewApartmentsByDate();
    }

    /**
     * Notify About new Apartment by getting City HouseNumber and Street
     * @param city
     * @param street
     * @param houseNumber
     */
    private void notifyNewAdvertisement(String city, String street, String houseNumber)
    {
        mManager = (NotificationManager) this.getApplicationContext().getSystemService(this.getApplicationContext().NOTIFICATION_SERVICE);
        Intent intent1 = new Intent(this.getApplicationContext(), HomePage.class);

        Notification notification = new Notification(R.mipmap.ic_launcher,"new Advertisment", System.currentTimeMillis());
        intent1.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP| Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingNotificationIntent = PendingIntent.getActivity( this.getApplicationContext(),0, intent1,PendingIntent.FLAG_UPDATE_CURRENT);
        //notification.sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notification.setLatestEventInfo(this.getApplicationContext(), "AlarmManagerDemo", city + " " + street + " " + houseNumber, pendingNotificationIntent);

        mManager.notify(uniqueID, notification);
        //uniqueID++;
    }

    @Override
    public void onDestroy()
    {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    /**
     * Check if There's IS new Advertisement By Date
     */
    private void checkForNewApartmentsByDate() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DataApartment");

        //Get object From Parse By Date Creation Of object
        query.findInBackground(new FindCallback<ParseObject>() {
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        Date currentDate = new Date();
                       for (ParseObject currentParseObject : parseObjects) {

                           Log.d("CreationTime", currentDate.toString());
                           Log.d("CreationTime", currentParseObject.getCreatedAt().toString());

                           Date date = new Date(currentDate.getTime() - 60000);
                           if (currentParseObject.getCreatedAt().after(date)) {
                               notifyNewAdvertisement(currentParseObject.getString("City"), currentParseObject.getString("Street"),
                                       currentParseObject.getString("HouseNumber"));
                           }
                       }
                    } else {
                        Log.d("Notification Error",e.getMessage());
                    }
                }
            });
    }
}
