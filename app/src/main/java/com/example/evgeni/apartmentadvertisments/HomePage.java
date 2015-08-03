package com.example.evgeni.apartmentadvertisments;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import ApartmentData.DataApartment;

/**
 * Created by evgeni on 4/11/2015.
 */

/**
 * The main Page Of App - The list OF Apartments That All User Can See
 */
public class HomePage extends ActionBarActivity {
    private ListView listView;
    private ParseUser currentUser;
    private String city;
    private String street;
    private LinearLayout myGallery;
    private Context context = this;
    private static boolean isNotificationOnce = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        ParseObject.registerSubclass(DataApartment.class);
        //Initialize For Parse Object
        Parse.initialize(this, "zDAftBiv6bLs4eMSWHy5mDIv2inREormFXNSJlhZ", "aOUskiEi8jvpynbajRFPfMtKQFudlvB6Ejh85YiG");
        listView = (ListView)findViewById(R.id.listViewMainPage);
        //Parse User To Check If He logged in
        currentUser = ParseUser.getCurrentUser();

        if(currentUser != null && isNotificationOnce)
        {
            startNotifications();
            isNotificationOnce = false;
        }
        showDataByCategory();
    }

    /**
     * Filter What To Show To User
     */
    private void showDataByCategory(){
        Bundle extraInfo = getIntent().getExtras();
        if(extraInfo != null) {
            boolean userData = extraInfo.getBoolean("userAdvertisments");
            boolean searchPlace = extraInfo.getBoolean("SearchPlace");
            if(searchPlace)
            {
                getData(false, searchPlace, extraInfo.getString("City"), extraInfo.getString("Street"), extraInfo.getString("HouseNum"));
            }
            else if(userData) {
                getData(userData, false, "", "", "");
            }
            extraInfo.clear();
        }
        else
        {
            getData(false, false, "", "", "");
        }
    }


    /**
     * Get Data From PArse Data Base
     * @param isUserData
     * @param isSearchPlace
     * @param city
     * @param street
     * @param houseNum
     */
    private void getData(boolean isUserData, boolean isSearchPlace, String city, String street, String houseNum) {

        ParseQuery<ParseObject> query = ParseQuery.getQuery("DataApartment");
        if(currentUser != null && isUserData)
        {
            query.whereEqualTo("User", currentUser);
            selectLongPress();
        }
        if(isSearchPlace)
        {
            query.whereEqualTo("City", city);
            query.whereEqualTo("Street", street);
            //query.whereEqualTo("HouseNumber", houseNum);
        }
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, com.parse.ParseException e) {

                if (e == null) {
                    selectSingleApartment();
                    ListAdapter adapter = new CustomAdapter(getBaseContext(), parseObjects);
                    listView.setAdapter(adapter);

                } else {
                    // something went wrong
                    String a = e.getMessage();
                }
            }
        });
    }

    /**
     * startNotifications
     */
    private void startNotifications()
    {
        Calendar calendar = Calendar.getInstance();
        Intent myIntent = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                1000 * 60 * 1, pendingIntent);
    }

    /**
     * Create Dialog to Show Details By pressing in main menu apartment
     */
    private void selectSingleApartment() {
        final Dialog dialogApartmentDetails = new Dialog(this);
        dialogApartmentDetails.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogApartmentDetails.setContentView(R.layout.show_data);
        dialogApartmentDetails.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final TextView city = (TextView) dialogApartmentDetails.findViewById(R.id.textViewShowDataCity);
                final TextView street = (TextView) dialogApartmentDetails.findViewById(R.id.textViewShowDataStreet);
                final TextView houseNum = (TextView) dialogApartmentDetails.findViewById(R.id.textViewShowDataHouseNum);
                final TextView apartmentNum = (TextView) dialogApartmentDetails.findViewById(R.id.textViewShowDataApartmentNum);
                final TextView owner = (TextView) dialogApartmentDetails.findViewById(R.id.textViewShowDataOwner);
                final TextView price = (TextView) dialogApartmentDetails.findViewById(R.id.textViewShowDataPrice);
                final RatingBar rate = (RatingBar) dialogApartmentDetails.findViewById(R.id.ratingBarShowDataRate);
                final TextView startDateRent = (TextView) dialogApartmentDetails.findViewById(R.id.textViewShowDataStartDateRent);
                final TextView endDateRent = (TextView) dialogApartmentDetails.findViewById(R.id.textViewShowDataEndDateRent);
                final TextView description = (TextView) dialogApartmentDetails.findViewById(R.id.textViewShowDataDesripction);
                myGallery = (LinearLayout)dialogApartmentDetails.findViewById(R.id.mygallery);
                myGallery.removeAllViews();
                myGallery.refreshDrawableState();
                ParseObject currentParseObject = (ParseObject) parent.getItemAtPosition(position);
                ParseQuery<ParseObject> query = ParseQuery.getQuery("DataApartment");
                query.getInBackground(currentParseObject.getObjectId(), new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {
                            city.setText(object.getString("City"));
                            street.setText(object.getString("Street"));
                            houseNum.setText(object.getString("HouseNumber"));
                            apartmentNum.setText(object.getString("ApartmentNumber"));
                            owner.setText(object.getString("Owner"));
                            price.setText(object.getString("Price"));
                            rate.setRating(Float.parseFloat(object.getString("Rate")));
                            startDateRent.setText(object.getString("FromDate"));
                            endDateRent.setText(object.getString("UntillDate"));
                            description.setText(object.getString("Description"));
                            ArrayList<ParseFile> imageList  = null;
                            imageList =( ArrayList<ParseFile>)object.get("Photo");
                            if(imageList !=null) {
                                for (ParseFile temp : imageList) {
                                    addImageToDialog(temp);
                                }
                            }
                        } else {
                           Log.d("ParseException",e.getMessage());
                        }
                    }
                });
                dialogApartmentDetails.show();



            }
        });

    }

    /**
     * Adding image ti ImageView In Gallery
     * @param parseFileImage - image in PArse DataBase
     */
    private void addImageToDialog(ParseFile parseFileImage)
    {
        LayoutImageViewDialogCreatorAsyncTask taskAddViewLayout = new LayoutImageViewDialogCreatorAsyncTask(getApplicationContext());
        try {
            //Start AsyncTask For Creating ImageView
            taskAddViewLayout.execute(Uri.parse(parseFileImage.getUrl()));
            ImageView imageViewTemp = (ImageView)taskAddViewLayout.get();
            Picasso.with(getApplicationContext()).load(parseFileImage.getUrl()).resize(220,220).into(imageViewTemp);
            myGallery.addView(imageViewTemp);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    /**
     * Getting Option To Update OR Delete to User His Adv. Apartment
     */
    private void selectLongPress() {
            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int pos, long id) {
                    final ParseObject currentParseObject = (ParseObject) arg0.getItemAtPosition(pos);
                    final CharSequence[] options = {"Update", "Delete"};
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Choose");

                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Update")) {
                                final Dialog dialogApartmentUpdate = new Dialog(context);
                                dialogApartmentUpdate.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialogApartmentUpdate.setContentView(R.layout.apartment_data_activity);
                                dialogApartmentUpdate.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
                                Intent intent = new Intent(context, AddNewApartment.class);
                                intent.putExtra("id",currentParseObject.getObjectId());
                                startActivity(intent);
                            } else if (options[item].equals("Delete")) {
                                ParseQuery<ParseObject> query = ParseQuery.getQuery("DataApartment");
                                query.getInBackground(currentParseObject.getObjectId(), new GetCallback<ParseObject>() {
                                    public void done(ParseObject object, ParseException e) {
                                        if (e == null) {
                                            object.deleteInBackground(new DeleteCallback() {
                                                @Override
                                                public void done(ParseException e) {
                                                    finish();
                                                    startActivity(new Intent(HomePage.this, HomePage.class));
                                                    Toast.makeText(HomePage.this, "Delete succeeded", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }


    /**
     * Option Menu to show to user
     * @param menu
     * @return
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if(currentUser != null) {
            menu.add(0, 1, Menu.NONE, "show my advertisments");
            menu.add(0, 2, Menu.NONE, "add new advertisment");
            menu.add(0, 6, Menu.NONE, "log out");
        }
        else
        {
            menu.add(0, 5, Menu.NONE, "login");
        }
        menu.add(0, 3, Menu.NONE, "show all");
        menu.add(0, 4, Menu.NONE, "search");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home_page, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id)
        {
            case 5:

                Intent intent = new Intent(this, Login_activity.class);
                startActivity(intent);

                break;

            case 4:

                cleanIntentExtra("userAdvertisments", "SearchPlace", "City", "Street");
                searchPlace();

                break;

            case 1:

                Intent thisIntent = getIntent();
                cleanIntentExtra("userAdvertisments", "SearchPlace", "City", "Street");
                thisIntent.putExtra("userAdvertisments", true);
                finish();
                startActivity(thisIntent);
                break;


            case 2:

                Intent intentAddNewAdvertisment = new Intent(this, AddNewApartment.class);
                startActivity(intentAddNewAdvertisment);
                break;

            case 3:
                Intent intentShowAll = getIntent();
                cleanIntentExtra("userAdvertisments", "SearchPlace", "City", "Street");
                finish();

                startActivity(intentShowAll);
                break;

            case 6:

                currentUser.logOut();

                Intent myIntent = new Intent(getBaseContext(),MyBroadcastReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getBaseContext(),0, myIntent, 0);
                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                alarmManager.cancel(pendingIntent);
                Intent intentLogOut = getIntent();
                cleanIntentExtra("userAdvertisments", "SearchPlace", "City", "Street");
                finish();

                startActivity(intentLogOut);
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void cleanIntentExtra(String userAdver, String search, String city, String street)
    {
        getIntent().removeExtra(userAdver);
        getIntent().removeExtra(search);
        getIntent().removeExtra(city);
        getIntent().removeExtra(street);

    }

    /**
     * Google APi MAps
     */
    private void searchPlace()
    {
        final Dialog searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.location_layout);
        WebView webView = (WebView)searchDialog.findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/index.html");
        webView.getSettings().setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new WebAppInterface(getBaseContext()), "Android");

        searchDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);

        searchDialog.show();

        Button buttonSelectedLocation = (Button) searchDialog.findViewById(R.id.buttonSelectedLocation);
        buttonSelectedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAddress()) {
                    searchDialog.dismiss();
                    Intent mainIntent = getIntent();
                    mainIntent.putExtra("SearchPlace", true);
                    mainIntent.putExtra("City", city);
                    mainIntent.putExtra("Street", street);
                    finish();
                    startActivity(mainIntent);
                }
            }
        });
    }

    private boolean checkAddress()
    {
        boolean flag = true;
        if((city == null) || (street == null) )
        {
            Toast.makeText(this, "must choose address", Toast.LENGTH_SHORT).show();
            flag = false;
        }

        return flag;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("isNotificationOnce",isNotificationOnce);
    }

    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        isNotificationOnce = savedInstanceState.getBoolean("isNotificationOnce");
    }

    /**
     * JavaScript Google Maps
     */
    public class WebAppInterface {
        Context mContext;

        WebAppInterface(Context c) {
            mContext = c;
        }

        @JavascriptInterface
        public void showLocation(String lat, String lng, String place) {

            Geocoder geocoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> listAddressFromCoordinates = geocoder.getFromLocation(Double.parseDouble(lat), Double.parseDouble(lng), 1);
                Address address = listAddressFromCoordinates.get(0);

                city = address.getLocality();
                street = address.getThoroughfare();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}