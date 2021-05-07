package dk.darkmtbg2.intents.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dk.darkmtbg2.intents.service.interfaces.FileService;

public class FileServiceImp implements FileService {

    private File latestImageGenerated;

    public FileServiceImp(){

    }


    /**
     * Creates an image and returns the created file.
     * @return the created file on the file system.
     * @throws IOException
     */
    public File createImageFile() throws IOException {
        File currentPhoto;
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Intents");

        if(!storageDir.isDirectory()){
            storageDir.mkdirs();
        }

        currentPhoto = new File(
                storageDir,
                imageFileName + ".jpg"
        );
        latestImageGenerated = currentPhoto;
        return currentPhoto;
    }


    public File getLatestImageGenerated(){
        return  latestImageGenerated;
    }


    /**
     * Saves the image into the file system where the gallery can access
     * @param context the context to handle the request
     * @param file the file to handle.
     * @return
     */
    public boolean galleryAddPic(Context context, File file) {
        if(file != null){
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = getFileUriLocation(context,file);
            //Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            context.sendBroadcast(mediaScanIntent);
            return true;
        }
        return false;
    }

    /**
     * Gets the uri path for the file passed in.
     * @param context context for handling the request
     * @param file the file to get the Uri to
     * @return the Uri from the file.
     */
    public Uri getFileUriLocation(Context context, File file)
    {
        return FileProvider.getUriForFile(context, "dk.darkmtbg2.intents", file);
    }
}
