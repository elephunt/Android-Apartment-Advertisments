package com.example.evgeni.apartmentadvertisments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseFile;
import com.parse.ParseObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by evgeni on 4/6/2015.
*/

/**
 * Adapter For Showing Adv.Apartments By List
 */
public class CustomAdapter extends ArrayAdapter<ParseObject> {

private Context myContext;
    public CustomAdapter(Context context, List<ParseObject> resource) {
        super(context, R.layout.custom_apartment_show, resource);
        myContext = context;
    }


    /**
     * Return View Of Apartment By Position Of List
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            View myView = layoutInflater.inflate(R.layout.custom_apartment_show, parent, false);
            TextView textViewAddress = (TextView) myView.findViewById(R.id.textViewAddressCustom);
            TextView textViewPrice = (TextView) myView.findViewById(R.id.textViewPriceCustom);
            RatingBar ratingBar = (RatingBar) myView.findViewById(R.id.ratingBarCustom);
            ImageView imageView = (ImageView) myView.findViewById(R.id.imageViewCustom);
            TextView textViewDate = (TextView) myView.findViewById(R.id.textViewCreatedDate);
            String city = getItem(position).getString("City");
            String street = getItem(position).getString("Street");
            String houseNum = getItem(position).getString("HouseNumber");
            textViewAddress.setText(city + " " + street + " " + houseNum);
            textViewPrice.setText(getItem(position).getString("Price") + "שח");
            ratingBar.setRating(Float.parseFloat(getItem(position).getString("Rate")));
            Date createdDate = getItem(position).getCreatedAt();
            textViewDate.setText(createdDate.toString());
            fetchImages(position,imageView);
            return myView;
    }

    /**
     * Get The images and To adv. Is there is any images.
     * @param position
     * @param imageView
     */
    private void fetchImages(int position, ImageView imageView)
    {
        List<ParseFile> list = getItem(position).getList("Photo");
        if(list != null)
        {
            Picasso.with(myContext).load(list.get(0).getUrl()).resize(50, 50)
                    .centerCrop().into(imageView);
        }
    }

}