package dk.darkmtbg2.intents.service;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileService {

    private static File latestImageGenerated;

    public static File createImageFile() throws IOException {
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

    public static File getLastestImageGenerated(){
        return  latestImageGenerated;
    }
}
