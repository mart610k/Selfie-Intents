package dk.darkmtbg2.intents.service.interfaces;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public interface FileService {

    /**
     * Creates an image and returns the created file.
     * @return the created file on the file system.
     * @throws IOException
     */
    File createImageFile() throws IOException;

    File getLatestImageGenerated();

    /**
     * Saves the image into the file system where the gallery can access
     * @param context the context to handle the request
     * @param file the file to handle.
     * @return
     */
    boolean galleryAddPic(Context context, File file);

    /**
     * Gets the uri path for the file passed in.
     * @param context context for handling the request
     * @param file the file to get the Uri to
     * @return the Uri from the file.
     */
    Uri getFileUriLocation(Context context, File file);
}
