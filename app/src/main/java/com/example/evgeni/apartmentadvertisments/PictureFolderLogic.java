package com.example.evgeni.apartmentadvertisments;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by RomaMarg on 21.04.2015.
 */

/**
 * Class For Managing Picture By Taking Gallery OR camera  (Singelton)
 */
public class PictureFolderLogic {
    File directory;
    private final String NameOfFolder = "ApartmentPics";
    private final String TAG = "Photo";
    private ArrayList<Uri> addressOfImages;
    private boolean canAddMorePictures;
    private int  addPicture = 0;
    private static PictureFolderLogic instance = null;

    /**
     * Start New Activity Reset All data
     */
    public void ResetForNewActivity()
    {
        if(addressOfImages !=null)
        {
            if(addressOfImages.size() > 0)
            {
                addressOfImages.clear();
            }
            addPicture = 0;
        }
    }

    public void addedPicture(){
            addPicture++;
    }

    public  void removedPicture(){
        addPicture--;
    }

    private PictureFolderLogic()
    {
        createDirectory();
        addressOfImages  = new ArrayList<Uri>();
        canAddMorePictures = true;

    }

    /**
     * Check If There Images Been Taken
     * @return
     */
    public boolean IsThereIsAnImages()
    {
        boolean thereIs = false;
        if(addressOfImages != null)
        {
            if(addressOfImages.size() > 0)
            {
                thereIs = true;
            }
        }

        return thereIs;
    }

    /**
     * Get All Images Location In Phone
     * @return
     */
    public ArrayList<Uri> GetImagesLocation()
    {
       if(IsThereIsAnImages())
       {
           return addressOfImages;
       }
        else
       {
           return null;
       }
    }


    /**
     * Getting Instance OF Class
     * @return
     */
    public static synchronized PictureFolderLogic getInstance()
    {
        if(instance == null)
        {
            instance = new PictureFolderLogic();
        }
        return instance;
    }


    /**
     * Adding Picture By Getting Adrress From Gallery
     * @param GalleryImage
     */
    public void AddPictureFromGallery(Uri GalleryImage)
    {
        if(CanAddMorePictures())
        {
            addressOfImages.add(GalleryImage);
        }
    }


    public void WriteImageFromByte(byte[] image){
        try {
            File file = new File(directory.getPath() + "/" + "photo_"
                    + System.currentTimeMillis() + ".jpg");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(image);
            fos.close();
            addressOfImages.add(Uri.fromFile(file));
        }
        catch (FileNotFoundException e){

        }
        catch (IOException Iox){

        }
    }


    /**
     * Remove photo From User Gallery
     * @param id
     */
    public void DeleteUriOfPhoto(int id)
    {
     Uri toRemove = null;
     boolean succeedToFind = false;
           for(Uri temp : addressOfImages )
           {
               if(temp.hashCode() == id)
               {
                  toRemove = temp;
                   succeedToFind = true;
                   break;
               }
           }
        if(succeedToFind == true)
        {
            addressOfImages.remove(toRemove);
        }

    }

    /**
     * Upload Size is 4 Pictures
     * @return
     */
    public boolean CanAddMorePictures()
    {

        if(addressOfImages.size() + addPicture >= 4)
        {
            canAddMorePictures = false;
        }
        else
        {
            canAddMorePictures = true;
        }
        return  canAddMorePictures;
    }


    //Load A Last Picture In UI Taken By Camera
    public Uri getUriLastPicture()
    {
        return addressOfImages.get(addressOfImages.size()-1);
    }

    /**
     * Creating Dir Folder In Phone If Not Exist
     */
    private void createDirectory() {
        directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                NameOfFolder);
        if (!directory.exists())
            directory.mkdirs();
    }

    /**
     * By getting number type can create a kinf of photo(jpeg,png,..)
     * @param type
     * @return
     */
    public Uri generateFileUri(int type) {
        File file = null;
        switch (type) {
            case 1:
                file = new File(directory.getPath() + "/" + "photo_"
                        + System.currentTimeMillis() + ".jpg");
                addressOfImages.add(Uri.fromFile(file));
                break;
            default:
                break;
        }
        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }


}
