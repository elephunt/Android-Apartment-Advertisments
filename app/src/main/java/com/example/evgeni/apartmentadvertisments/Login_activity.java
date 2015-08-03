package com.example.evgeni.apartmentadvertisments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import ApartmentData.DataApartment;

/**
 * Created by evgeni on 4/7/2015.
 */

/**
 * Page OF Login User Or Sign Up by Facebook Or regular Way
 */
public class Login_activity extends ActionBarActivity {

    final List<String> permissions = Arrays.asList("public_profile", "email");
    private EditText name;
    private EditText password;
    private Button fbButtonRegular = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        Parse.initialize(this, "zDAftBiv6bLs4eMSWHy5mDIv2inREormFXNSJlhZ", "aOUskiEi8jvpynbajRFPfMtKQFudlvB6Ejh85YiG");
        ParseFacebookUtils.initialize(getBaseContext());
        setContentView(R.layout.activity_login_activity);
         initializeView();

    }

    private void initializeView(){
        ParseObject.registerSubclass(DataApartment.class);
        name = (EditText)findViewById(R.id.editTextLoginName);
        password = (EditText)findViewById(R.id.editTextLoginPass);
        fbButtonRegular = (Button)findViewById(R.id.buttonFacebookRegular);
        registerFacebookButtonCallBack();
        ParseUser currentUser = ParseUser.getCurrentUser();
        if ((currentUser != null) && ParseFacebookUtils.isLinked(currentUser)) {
            //Finish if he already login
            Log.d("FacebookLoginIn","AlreadyLoggedIn");
            // Go to the user info activity
            // showUserDetailsActivity();
        }
    }



    //-----------------------Facebook ----------------------------------------------------------//
    private void registerFacebookButtonCallBack()
    {
        fbButtonRegular.setOnClickListener(new View.OnClickListener(){
            //Parse Facebook Login OnClickListener
            @Override
            public void onClick(View v) {
                   ParseFacebookUtils.logInWithReadPermissionsInBackground(Login_activity.this, permissions, new LogInCallback() {
                       @Override
                       public void done(final ParseUser parseUser, ParseException e) {
                           if(e == null) {
                               doneFromParseFacebookLogin(parseUser, e);
                           }
                           else
                           {
                               Log.d("FacebookException","Facebook Wrong Or No Data");
                           }

                   }});
               }
            });
    }

    /**
     * Facebook Parse User Callback
     * @param parseUser
     * @param e
     */
    private void doneFromParseFacebookLogin(final ParseUser parseUser, ParseException e) {
        if (e != null) {
            Log.d("FacebookParseException", e.toString());
        } else {
            if (parseUser == null) {
                Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                //  ParseUser.logOut();
            } else if (parseUser.isNew()) {
                logInParseFacebookAccount(parseUser);
            } else {
                logInParseFacebookAccount(parseUser);
            }

        }
    }


    /**
     * Success Facebook User Login
     * @param fbUser
     */
    private void logInParseFacebookAccount(final ParseUser fbUser) {
        if (!ParseFacebookUtils.isLinked(fbUser)) {
            ParseFacebookUtils.linkWithReadPermissionsInBackground(fbUser, Login_activity.this, permissions, new SaveCallback() {
                @Override
                public void done(ParseException ex) {
                    if (ParseFacebookUtils.isLinked(fbUser)) {
                        Log.d("MyApp", "Woohoo, user logged in with Facebook!");

                    }
                }
            });
        }
        startHomePageActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }


    //Calls only in case of success login facebook

    /**
     * Start HomePage After Login
     */
    private void startHomePageActivity()
    {
        Intent intent = new Intent(Login_activity.this, HomePage.class);
        startActivity(intent);
    }

    //-----------------------------End Facebook-------------------------------------------------//


    public void signUpButtonClicked(View view)
    {
        Intent intent = new Intent(this, SignUp_activity.class);
        startActivity(intent);
    }

    /**
     * Pressing Login Button
     * @param view - Event of Button
     */
    public void LoginButtonClicked(View view)
    {
        if(name.getText().toString().isEmpty() || password.getText().toString().isEmpty())
        {
            try {
                throw new FieldWrongException("Field Is Empty");
            }
            catch (FieldWrongException e){
                Toast.makeText(this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        ParseUser.logInInBackground(name.getText().toString(), password.getText().toString(), new LogInCallback() {
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    // Hooray! The user is logged in.
                    Intent intent = new Intent(Login_activity.this, HomePage.class);
                    startActivity(intent);
                } else {
                    // Signup failed. Look at the ParseException to see what happened.

                    showUserLoginFailed();
                }
            }
        });
    }

    private void showUserLoginFailed()
    {
        Toast.makeText(this, "User doesn't existent", Toast.LENGTH_SHORT).show();
    }
}
