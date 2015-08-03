package com.example.evgeni.apartmentadvertisments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.ProgressCallback;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.PasswordAuthentication;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import ApartmentData.DataApartment;

/**
 * Class For Creating New Apartment Data To Add
 */
public class AddNewApartment extends ActionBarActivity {

    private EditText editTextNameOfOwner;
    private EditText editTextPrice;
    private RatingBar ratingBarRating ;
    private EditText editTextReview;
    private static final int REQUEST_CAMERA = 0;
    private static final int REQUEST_GALLERY = 1;
    private static final int SAVE_PHOTO_FROM_CAMERA = 1;
    private Calendar calendarStartDateRent = Calendar.getInstance();
    private Calendar calendarEndDateRent = Calendar.getInstance();
    private DateFormat dateFormat = DateFormat.getDateInstance();
    private TextView textViewStartDateRent;
    private TextView textViewEndDateRent;
    private static final int REQEUST_CAMERA = 1;
    private static final int REQEUST_GALLARY = 2;
    private Button buttonAddPhoto;
    private NumberPicker numberPicker;
    private LinearLayout myGallery;
    private int apartmentNumberSelected = 1;
    private float rate;
    private ImageView imageView;
    private TextView textViewLocation;
    private String city = "";
    private String street = "";
    private String houseNum = "";
    private boolean flagDoubleData = false;
    private Bundle extras;
    private String cityCurrentUpdate;
    private String streetCurrentUpdate;
    private String houseNumberCurrentUpdate;
    private String latitude;
    private String longitude;
    private PictureFolderLogic imagePathHolderAndAmount;
    protected ProgressDialog proDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.apartment_data_activity);
        /**
         * Before work you must initialize parse object
         */
        ParseObject.registerSubclass(DataApartment.class);
        Parse.initialize(this, "zDAftBiv6bLs4eMSWHy5mDIv2inREormFXNSJlhZ", "aOUskiEi8jvpynbajRFPfMtKQFudlvB6Ejh85YiG");
        initializeViewComponents();
        /**
         * Logic for Picture Gallery Manage
         */
        imagePathHolderAndAmount = PictureFolderLogic.getInstance();
        imagePathHolderAndAmount.ResetForNewActivity();
        extras = getIntent().getExtras();
        if (extras != null) {
          getParseObject();
        }
        if(!hasCamera()){
            buttonAddPhoto.setEnabled(false);
        }
        addNumberApartmentListener();
        addListenerOnRatingBar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(!editTextPrice.getText().toString().isEmpty()) {
            outState.putString("Price",editTextPrice.getText().toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String price =  (String)savedInstanceState.get("Price");
        if(!price.isEmpty() && price != null){
             editTextPrice.setText(price);
        }
    }


    private void getParseObject(){
        Button updateButton;
        final  Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                stopLoading();}};
        final String objectId = extras.getString("id");
        final ParseQuery<ParseObject> query = ParseQuery.getQuery("DataApartment");
        startLoading();
        Thread gettingParseObjectThread = new Thread(){
            @Override
            public void run() {
                super.run();
                query.getInBackground(objectId, new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e)
                            {
                                if (e == null)
                                {
                                    cityCurrentUpdate = city = (String)object.get("City");
                                    streetCurrentUpdate = street = object.getString("Street");
                                    houseNumberCurrentUpdate = houseNum = object.getString("HouseNumber");
                                    textViewLocation.setText(city + " " + street + " " + houseNum);
                                    numberPicker.setMinValue(1);
                                    numberPicker.setMaxValue(500);
                                      numberPicker.setValue(Integer.parseInt(object.getString("ApartmentNumber")));
                                    apartmentNumberSelected = numberPicker.getValue();
                                     editTextNameOfOwner.setText(object.getString("Owner"));
                                    editTextPrice.setText(object.getString("Price"));
                                    rate = Float.parseFloat(object.getString("Rate"));
                                    ratingBarRating.setRating(rate);
                                    textViewStartDateRent.setText(object.getString("FromDate"));
                                    textViewEndDateRent.setText(object.getString("UntillDate"));
                                    editTextReview.setText(object.getString("description"));
                                    downloadImageFromObject(object);
                                    handler.sendEmptyMessage(1);
                                }
                            }}
                );
            }
        };
        gettingParseObjectThread.start();
        updateButton = (Button)findViewById(R.id.buttonAddApartment);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread deletingThread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        query.getInBackground(objectId,new GetCallback<ParseObject>() {
                            public void done(ParseObject object, ParseException e)
                            {
                                object.deleteInBackground();
                            }
                        });
                    }
                };
                deletingThread.start();
                saveApartmentData();
            }
        });
    }


    /**
     * Download image From PArse DataBase
     * @param object
     */
    private void downloadImageFromObject(ParseObject object){
        ArrayList<ParseFile> imageList = ( ArrayList<ParseFile>)object.get("Photo");
        for(ParseFile temp : imageList){
            temp.getDataInBackground(new GetDataCallback() {
            public void done(byte[] data, ParseException e) {
                if (e == null) {
                        imagePathHolderAndAmount.WriteImageFromByte(data);
                         callImageViewCreation(imagePathHolderAndAmount.getUriLastPicture());
                } else {
                    // something went wrong
                }
            }
        });
        }
    }

    /**
     * Initialize View Components For View
     */
    private void initializeViewComponents()
    {
        editTextNameOfOwner = (EditText)findViewById(R.id.editTextNameOfOwner);
        editTextPrice = (EditText)findViewById(R.id.editTextPrice);
        ratingBarRating = (RatingBar)findViewById(R.id.ratingBar);
        editTextReview = (EditText)findViewById(R.id.editTextReview);
        textViewStartDateRent = (TextView) findViewById(R.id.textViewStartDateRent);
        textViewEndDateRent = (TextView) findViewById(R.id.textViewEndDateRent);
        myGallery = (LinearLayout)findViewById(R.id.mygallery);
        buttonAddPhoto = (Button) findViewById(R.id.buttonTakePhoto);
        numberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(500);
        textViewLocation = (TextView) findViewById(R.id.textViewLocation);
    }

    /**
     *Change number apartment in wheel
     */
    private void addNumberApartmentListener() {
        numberPicker.setWrapSelectorWheel(false);
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                apartmentNumberSelected = newVal;
            }
        });
    }

    private void addListenerOnRatingBar() {

        ratingBarRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                rate = rating;
            }
        });
    }

    /**
     * upload Data To Parse DataBase
     */
    private void saveApartmentData()
    {

        /**
         * Check data that legit
         */
             if (!checkDoubleData() && checkAddress() && checkDates() && !checkPrice() && !checkOwner()) {

                     Log.d("BeforeProgress","BeforeProgress");
                     final  Handler handler = new Handler(){
                         @Override
                         public void handleMessage(Message msg) {
                             super.handleMessage(msg);
                             stopLoading();
                             startActivity(new Intent(AddNewApartment.this, HomePage.class));
                         }
                     };
                     startLoading();
                     Thread thread = new Thread() {
                         public void run() {
                             try {
                                 DataApartment data = new DataApartment(ParseUser.getCurrentUser(), city, street, houseNum, Integer.toString(apartmentNumberSelected), editTextNameOfOwner.getText().toString(),
                                         editTextPrice.getText().toString(), Float.toString(rate), textViewStartDateRent.getText().toString(), textViewEndDateRent.getText().toString(),
                                         editTextReview.getText().toString(), latitude, longitude, parseAllImages());
                                 if (city == null || street == null || houseNum == null) {
                                     throw new DataApartmentException("Address is empty");
                                 }
                                 data.saveEventually(new SaveCallback() {
                                     @Override
                                     public void done(ParseException e) {
                                         handler.sendEmptyMessage(1);
                                     }
                                 });
                                 }
                                 catch(DataApartmentException e)
                                 {
                                   //  Toast.makeText(this, "Address is empty", Toast.LENGTH_SHORT).show();
                                 }
                         }

                     };
                     thread.start();
                     Log.d("CreatingData", "Good");

                 }
                 }

    private boolean checkPrice()
    {
        boolean flagEmptyPrice = false;
        if(editTextPrice.getText().toString().isEmpty())
        {
            flagEmptyPrice = true;
            Toast.makeText(this, "price can not be empty", Toast.LENGTH_SHORT).show();
        }

        return flagEmptyPrice;
    }

    private boolean checkOwner()
    {
        boolean flagEmptyOwner = false;
        if(editTextNameOfOwner.getText().toString().isEmpty())
        {
            flagEmptyOwner = true;
            Toast.makeText(this, "owner can not be empty", Toast.LENGTH_SHORT).show();
        }

        return flagEmptyOwner;
    }

    private boolean checkDates()
    {
        boolean flagCheckDates = true;
        if(textViewStartDateRent.getText().toString().isEmpty() || textViewEndDateRent.getText().toString().isEmpty())
        {
            flagCheckDates = false;
            Toast.makeText(this, "Dates can not bee empty", Toast.LENGTH_SHORT).show();
        }

        return flagCheckDates;
    }

    DatePickerDialog.OnDateSetListener listenerStartDateRent = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendarStartDateRent.set(Calendar.YEAR, year);
            calendarStartDateRent.set(Calendar.MONTH, monthOfYear);
            calendarStartDateRent.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            textViewStartDateRent.setText(dateFormat.format(calendarStartDateRent.getTime()));
        }
    };

    DatePickerDialog.OnDateSetListener listenerEndDateRent = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendarEndDateRent.set(Calendar.YEAR, year);
            calendarEndDateRent.set(Calendar.MONTH, monthOfYear);
            calendarEndDateRent.set(Calendar.DAY_OF_MONTH, dayOfMonth);


            if(checkDateRent()) {
                textViewEndDateRent.setText(dateFormat.format(calendarEndDateRent.getTime()));
            }
        }
    };



    private boolean checkDateRent()
    {
        boolean flagCorrectDateRent = true;
        if(calendarEndDateRent.getTime().before(calendarStartDateRent.getTime()))
        {
            flagCorrectDateRent = false;
            Toast toastWrongEndDateRent = Toast.makeText(this, "End date canot be before start date", Toast.LENGTH_SHORT);
            toastWrongEndDateRent.show();
        }

        return flagCorrectDateRent;
    }

    public void startDateRentButtonClicked(View view)
    {
        new DatePickerDialog(this, listenerStartDateRent, calendarStartDateRent.get(Calendar.YEAR), calendarStartDateRent.get(Calendar.MONTH), calendarStartDateRent.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void endDateRentButtonClicked(View view)
    {
        new DatePickerDialog(this, listenerEndDateRent, calendarEndDateRent.get(Calendar.YEAR), calendarEndDateRent.get(Calendar.MONTH), calendarEndDateRent.get(Calendar.DAY_OF_MONTH)).show();;
    }

    private boolean hasCamera()
    {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    }



//-----------------------------Photo Gallery--------------------------------------------------//

    /**
     *Button For Pressing Add Image From Camera And Gallery
     */
    public void launchCameraButtonClicked(View view)
    {
        //Check That Amount Of Pics Can Only be 4
        if(imagePathHolderAndAmount.CanAddMorePictures()) {
            launchCameraButtonClickedLogic();
        }
        //You out of 4 pics
        else
        {
            Toast.makeText(getApplicationContext(),"Cant Add More Pics",Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Dialog For Choosing Picture From Gallery or From Camera
     */
    private void launchCameraButtonClickedLogic()
    {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                //Take Picture From Camera
                if (options[item].equals("Take Photo"))
                {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    takePicture.putExtra(MediaStore.EXTRA_OUTPUT,imagePathHolderAndAmount.generateFileUri(SAVE_PHOTO_FROM_CAMERA));
                    startActivityForResult(takePicture,REQUEST_CAMERA);
                }
                //Take Picture From Gallery
                else if (options[item].equals("Choose from Gallery"))
                {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto ,REQUEST_GALLERY);
                }
                //Cancel from dialog
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    /**
     * Call ASyncTask to create imageView By passing Uri Of picture
     * @param imagePath
     */
    private void callImageViewCreation(Uri imagePath)
    {
        LayoutImageViewDialogCreatorAsyncTask taskAddViewLayout = new LayoutImageViewDialogCreatorAsyncTask(getApplicationContext());
        try {
            taskAddViewLayout.execute(imagePath);
            ImageView imageViewTemp = (ImageView)taskAddViewLayout.get();
            //Picasso Api for Managing Picture With ImageView
            if(imageViewTemp != null) {
                imageViewTemp.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        dialogMaker(v);
                        return false;
                    }
                });
                Picasso.with(getApplicationContext()).load(imagePath).resize(220, 220).into(imageViewTemp);
                myGallery.addView(imageViewTemp);
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    /**
     * From Activity After Calling Camera Or Gallery Activity
     * @param requestCode
     * @param resultCode
     * @param imageReturnedIntent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            //Picture From Camera
            case 0:
                if(resultCode == RESULT_OK){
                    //Because URI From Camera Create By ImageHolderPath
                    Uri pictureTaken = imagePathHolderAndAmount.getUriLastPicture();
                    callImageViewCreation(pictureTaken);
                }

                break;
            //Picture From Gallery
            case 1:
                if(resultCode == RESULT_OK){
                    //Adding Picture From Gallery By Returning URI From Gallery ,Store in ImageHolderPath And Start ASync
                    imagePathHolderAndAmount.AddPictureFromGallery(imageReturnedIntent.getData());
                    callImageViewCreation(imagePathHolderAndAmount.getUriLastPicture());
                }
                break;


        }
    }

    private void dialogMaker(final View V)
    {
        final ImageView myImageView = (ImageView)V;
        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewApartment.this);
        builder.setMessage("Do you want to delete this photo?\n");
        builder.setPositiveButton("Yes",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                myImageView.setVisibility(View.GONE);
                imagePathHolderAndAmount.DeleteUriOfPhoto(myImageView.getId());
            }
        });
        builder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(AddNewApartment.this, "Back", Toast.LENGTH_SHORT);
                dialog.dismiss();
            }
        });
        AlertDialog removePics = builder.create();
        removePics.show();
    }

    //-----------------------------Creating From Uri Parse Files--------------------------------//

    /**
     * Convert all uri images to Bytes List
     * @return List Of ParseFile
     */
    private ArrayList parseAllImages() {
        ArrayList<ParseFile> listParse = null;
        ParseFile parseFilePhoto = null;
        //Number in name of image
        int numberOfPic = 1;
        //Check that there is an images inside
        if (imagePathHolderAndAmount.IsThereIsAnImages() == true) {
            listParse = new ArrayList<ParseFile>();
            //Getting every Uri of image file
            for (Uri locationPics : imagePathHolderAndAmount.GetImagesLocation()) {
                try {
                    //Decoding to bytes
                    InputStream iStream = getContentResolver().openInputStream(locationPics);
                    byte[] inputData = getBytes(iStream);
                    //Put in ParseFile
                    parseFilePhoto = new ParseFile("Image" + numberOfPic++, inputData);
                    //Add to list of parseFiles
                    listParse.add(parseFilePhoto);
                } catch (FileNotFoundException NoFile) {

                } catch (IOException IoExc) {
                }
            }
        }
        return listParse;
    }

    private byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

//---------------------------------------------End Of Creating Uri to Parse Files-----------------------//
 //--------------------------------------------Photo Gallery--------------------------------------------//

    /**
     * Button To Add Apartment To DataBase
     * @param view
     */
    public void AddApartmentButtonClicked(View view)
    {
        saveApartmentData();
    }

    /**
     * Get Data From Google Map By JavaScript
     * @param view
     */
    public void locationButtonClicked(View view)
    {
        city = "";
        street = "";
        houseNum = "";
        textViewLocation.setText("");
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.location_layout);
        WebView webView = (WebView)dialog.findViewById(R.id.webView);
        webView.loadUrl("file:///android_asset/index.html");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WebAppInterface(this), "Android");
        dialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.FILL_PARENT);
        dialog.show();
        Button buttonSelectedLocation = (Button) dialog.findViewById(R.id.buttonSelectedLocation);
        buttonSelectedLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkAddress()) {
                    if(!checkDoubleData())
                    {
                        textViewLocation.setText(city + " " + street + " " + houseNum);
                        dialog.dismiss();
                    }
                }
            }
        });
    }

    /**
     *
     * @return
     */
    private boolean checkDoubleData() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("DataApartment");
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(currentUser != null)
        {
            if(city != null && street != null && houseNum != null) {
                if(city.equals(cityCurrentUpdate) && street.equals(streetCurrentUpdate) && houseNum.equals(houseNumberCurrentUpdate))
                {
                    return false;
                }
                else {
                    query.whereEqualTo("User", currentUser);
                    query.whereEqualTo("City", city);
                    query.whereEqualTo("Street", street);
                    query.whereEqualTo("HouseNumber", houseNum);
                }
            }
        }
        try {
            List<ParseObject> parseObjects = query.find();
            if(parseObjects.size() > 0)
            {
                flagDoubleData = true;
            }
            else
            {
                flagDoubleData = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(flagDoubleData) {
            Toast.makeText(AddNewApartment.this, "This location already existent", Toast.LENGTH_SHORT).show();
        }

        return flagDoubleData;
    }




    /**
     * Check That Address IS Legit
     * @return boolean
     */
    private boolean checkAddress()
    {
        boolean flag = true;
        if((city.isEmpty()) || (street.isEmpty()) || (houseNum.isEmpty()))
        {
            try {
                throw new FieldWrongException("Field Is Empty");
            }
            catch (FieldWrongException e){
                Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
            finally {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * Google Maps Interface JavaScript
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
                    String n = extractDigits(place);
                    latitude = Double.toString(address.getLatitude());
                    longitude = Double.toString(address.getLongitude());
                    city = address.getLocality();
                    street = address.getThoroughfare();
                    houseNum = n;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private String extractDigits(String src) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    /**
     * Open Dialog With Apartment Data To Show
     * @param view
     */
    public void descriptionDialogClicked(View view)
    {
        final Dialog dialogDescription = new Dialog(this);
        dialogDescription.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDescription.setContentView(R.layout.description_layout);
        dialogDescription.getWindow().setLayout(500, 500);
        dialogDescription.show();

        final EditText editTextDescription = (EditText) dialogDescription.findViewById(R.id.editTextDescription);
        Button buttonDescription = (Button) dialogDescription.findViewById(R.id.buttonDescription);
        buttonDescription.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                editTextReview.setText(editTextDescription.getText().toString());
                dialogDescription.dismiss();
            }
        });
    }

    //starts the progress bar
    private void startLoading() {
        proDialog = new ProgressDialog(this);
        proDialog.setMessage("loading...");
        proDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        proDialog.setCancelable(false);
        proDialog.show();
    }

    //closes the progress bar
    private void stopLoading() {
        proDialog.dismiss();
        proDialog = null;
    }


}


