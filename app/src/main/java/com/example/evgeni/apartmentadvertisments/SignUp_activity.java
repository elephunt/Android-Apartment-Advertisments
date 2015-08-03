package com.example.evgeni.apartmentadvertisments;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by evgeni on 4/7/2015.
 */

/**
 * Activity To allow To user To Sign Up
 */
public class SignUp_activity  extends ActionBarActivity {

    private EditText name;
    private EditText password;
    private EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_activity);
        Parse.initialize(this, "zDAftBiv6bLs4eMSWHy5mDIv2inREormFXNSJlhZ", "aOUskiEi8jvpynbajRFPfMtKQFudlvB6Ejh85YiG");
         initializeView();

    }

    private void initializeView(){
        name = (EditText) findViewById(R.id.editTextSignInName);
        password = (EditText) findViewById(R.id.editTexSignInPass);
        email = (EditText) findViewById(R.id.editTextSignInEmail);
    }

    /**
     * Pressign Button to Sign Up
     * @param view
     */
    public void signInPageButtonClicked(View view)
    {
        if(checkIsEmpty(name.getText().toString()))
        {
            showToast("name can not be empty");
        }
        if(checkIsEmpty(password.getText().toString()))
        {
            showToast("password can not be empty");
        }
        if(checkIsEmpty(email.getText().toString()))
        {
            showToast("email can not be empty");
        }
        else
        {
            //Check that email is validate
            ParseUser user = new ParseUser();
            user.setUsername(name.getText().toString());
            user.setPassword(password.getText().toString());
            user.setEmail(email.getText().toString());

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        // Hooray! Let them use the app now.
                        Intent intent = new Intent(SignUp_activity.this, HomePage.class);
                        startActivity(intent);
                    } else {
                        // Sign up didn't succeed. Look at the ParseException
                        // to figure out what went wrong
                        showToast(e.getMessage());
                    }
                }
            });
        }
    }

    private void showToast(String stringToShow)
    {
        Toast toast = Toast.makeText(this, stringToShow, Toast.LENGTH_SHORT);
        toast.show();
    }

    private boolean checkIsEmpty(String text)
    {
        boolean flagEmpty = false;
        if(text.isEmpty())
        {
            flagEmpty = true;
        }

        return flagEmpty;
    }

}
