package com.example.evgeni.apartmentadvertisments;

import android.provider.ContactsContract;

import ApartmentData.DataApartment;

/**
 * Created by RomaMarg on 29.05.2015.
 */

/**
 * Exception DataApartment fot Wrong Data In Apartment Data
 */
public class DataApartmentException extends  Exception {

    private String message;

    public DataApartmentException (String message){
        super(message);
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}
