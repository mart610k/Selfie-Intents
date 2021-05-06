package dk.darkmtbg2.intents;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import dk.darkmtbg2.intents.service.FileService;

import static android.os.Environment.getExternalStorageDirectory;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {

    final int REQUEST_IMAGE_CAPTURE = 0;
    final int PERMISSION_REQUEST_CODE = 1337;
    Snackbar imageWasSavedCorrectly;
    Snackbar imageWasNotTaken;
    Snackbar ioExceptionOccurred;
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }

        imageWasSavedCorrectly = Snackbar.make(findViewById(R.id.mainActivity), R.string.imageSavedToGallery, 5000);
        imageWasNotTaken = Snackbar.make(findViewById(R.id.mainActivity),R.string.imageNotToken,5000);
        imageWasNotTaken = Snackbar.make(findViewById(R.id.mainActivity),R.string.ioExeceptionOccured,5000);
    }

    /**
     * Returns after the method "requestPermissions" are called, returns the amount of permissions that were granted. if one of the permissions are denied then closes the application
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if(grantResults.length > 0) {
                    //Checks if all permissions required are granted
                    for (int i : grantResults) {
                        if (i != PackageManager.PERMISSION_GRANTED) {
                            missingRights();
                        }
                    }
                }
                return;
        }
    }

    /**
     * Called if required rights are missing, which shows a dialog box to the user, then exists the application on click.
     */
    public void missingRights(){
        builder = new AlertDialog.Builder(this);

        builder.setMessage("This application requires both camera and storage access")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        Toast.makeText(getApplicationContext(),"missing rights to application, closing",
                                Toast.LENGTH_SHORT).show();
                    }
                });
        AlertDialog alert = builder.create();
        alert.setTitle("Missing Rights");
        alert.show();
    }


    /**
     * triggers the selfie camera in the intent request
     * @param view the caller
     */
    public void selfieButtonCamera(View view){
        dispatchTakePictureIntent(1);
    }

    /**
     * triggers the front camera in the intent request
     * @param view the caller
     */
    public void frontButtonCamera(View view){
        dispatchTakePictureIntent(0);
    }

    /**
     * Triggers the intent to take an image from the camera through a camera app
     * @param cameraID the camera ID to start the camera on.
     */
    public void dispatchTakePictureIntent(int cameraID) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = FileService.createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ioExceptionOccurred.show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileService.getFileUriLocation(this,photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", cameraID);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE:
                //resultcode 0 means that the image was not taken
                switch (resultCode){
                    case -1:
                        File file = FileService.getLatestImageGenerated();
                        if (FileService.galleryAddPic(this,file)){
                            
                            ((ImageView)this.findViewById(R.id.imageview)).setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));

                            imageWasSavedCorrectly.show();
                        }
                        break;
                    //resultcode 0 means that the image was not taken
                    case 0:
                    default:
                        imageWasNotTaken.show();
                        break;
                }
                break;
            default:
                break;
        }
    }

    

}