package com.example.evgeni.apartmentadvertisments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by evgeni on 4/14/2015.
 */

/**
 * Asynch Task For Creating Image View For Picture Gallery
 */
class LayoutImageViewDialogCreatorAsyncTask extends AsyncTask<Uri, Void , View> {
    private Context applicationContext;
    PictureFolderLogic pic ;

    public LayoutImageViewDialogCreatorAsyncTask(Context myContext) {
        applicationContext = myContext;
        pic = PictureFolderLogic.getInstance();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }



    @Override
    protected View doInBackground(Uri... params) {
            final ImageView imageView = new ImageView(applicationContext);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(220, 220));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setId(params[0].hashCode());
            return imageView;
        }



    @Override
    protected void onPostExecute(View view) {
        super.onPostExecute(view);
    }
}