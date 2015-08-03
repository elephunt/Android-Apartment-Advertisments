package ApartmentData;

import android.util.Log;
import android.widget.Toast;

import com.example.evgeni.apartmentadvertisments.DataApartmentException;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by RomaMarg on 02.04.2015.
 */

/**
 *Class that hold Data About Apartment..
 */
@ParseClassName("DataApartment")
public class DataApartment extends ParseObject{

    private String apartmentNumber;
    private String descriptionApartment;
    private String priceApartment;
    private String rateApartment;
    private String city;
    private String street;
    private String houseNum;
    private String theOwner;
    //Date The Person Lived From
    private String fromDate;
    //Date The Person lived To
    private String utillDate;
    private ArrayList<ParseFile> photosImages;
    private ParseUser user;
    private String latitude;
    private String longitude;

    public DataApartment()
    {

    }

    /**
     *Constructor for data apartment
     * @param user - For Parse Site
     * @param city
     * @param street
     * @param houseNum
     * @param apartmentNumber
     * @param theOwner
     * @param priceApartment
     * @param rateApartment
     * @param fromDate
     * @param utillDate
     * @param descriptionApartment
     * @param latitude
     * @param longitude
     * @param images - list of Parse Files
     */
    public DataApartment(ParseUser user, String city, String street, String houseNum, String apartmentNumber, String theOwner, String priceApartment, String rateApartment, String fromDate, String utillDate,
                         String descriptionApartment, String latitude, String longitude,ArrayList<ParseFile> images){//, ParseFile photo) {

            setUser(user);
            setCity(city);
            setStreet(street);
            setHouseNum(houseNum);
            setApartmentNumber(apartmentNumber);
            setTheOwner(theOwner);
            setPriceApartment(priceApartment);
            setRateApartment(rateApartment);
            setFromDate(fromDate);
            setTillDate(utillDate);
            setDescriptionApartment(descriptionApartment);
            setLatitude(latitude);
            setLongitude(longitude);
            setPhoto(images);


    }

    /**
     * Setting apartment number and upload to parse
     * @param apartmentNumber
     */
    public void setApartmentNumber(String apartmentNumber) {
        this.apartmentNumber = apartmentNumber;
        put("ApartmentNumber", apartmentNumber);
    }

    public void setDescriptionApartment(String descriptionApartment) {
        this.descriptionApartment = descriptionApartment;
        put("Description", descriptionApartment);
    }

    public void setPriceApartment(String priceApartment) {
        this.priceApartment = priceApartment;
        put("Price", priceApartment);
    }

    public void setRateApartment(String rateApartment) {
        this.rateApartment = rateApartment;
        put("Rate", rateApartment);
    }

    public void setCity(String city) {
            this.city = city;
            put("City", city);
    }

    public void setStreet(String street) {
        this.street = street;
        put("Street", street);
    }

    public void setHouseNum(String houseNum) {
        this.houseNum = houseNum;
        put("HouseNumber", houseNum);
    }

    public void setTheOwner(String theOwner) {
        this.theOwner = theOwner;
        put("Owner", theOwner);
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
        put("FromDate", fromDate);
    }

    public void setTillDate(String utillDate) {
        this.utillDate = utillDate;
        put("UntillDate", utillDate);
    }

    /**
     * Uploading Images To Parse by Upload Image By Image
     * @param photos
     */
    public void setPhoto(ArrayList<ParseFile> photos) {
        this.photosImages = photos;

        if (this.photosImages != null) {
            {
                try {
                    for (ParseFile tempFile : photos) {
                        tempFile.save();
                    }
                    addAll("Photo", photos);
                } catch (ParseException e) {
                    Log.d("ParseException",e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    public void setUser(ParseUser user) {
        this.user = user;
        put("User", user);
    }

    public void setLatitude(String latitude)
    {
        if(latitude!=null) {
            this.latitude = latitude;
            put("Latitude", latitude);
        }
    }

    public void setLongitude(String longitude)
    {
        if(longitude!=null) {
            this.longitude = longitude;
            put("Longitude", longitude);
        }
    }
}
