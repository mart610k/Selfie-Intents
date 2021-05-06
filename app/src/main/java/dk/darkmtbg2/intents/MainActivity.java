package dk.darkmtbg2.intents;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[] {Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
        }

        imageWasSavedCorrectly = Snackbar.make(findViewById(R.id.mainActivity), R.string.imageSavedToGallery, 5000);
        imageWasNotTaken = Snackbar.make(findViewById(R.id.mainActivity),R.string.imageNotToken,5000);
    }

    /**
     * Returns after the method "requestPermissions" are called, returns the amount of permissions that were granted before closing.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                }  else {
                    this.finish();
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    public void selfieButtonCamera(View view){
        dispatchTakePictureIntent(1);
    }

    public void frontButtonCamera(View view){
        dispatchTakePictureIntent(0);
    }

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

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {

                Uri photoURI =
                        FileProvider.getUriForFile(this,
                        "dk.darkmtbg2.intents",
                        photoFile);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                takePictureIntent.putExtra("android.intent.extras.CAMERA_FACING", cameraID);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("I AM HERE????");
        switch (requestCode){
            case REQUEST_IMAGE_CAPTURE:
                //resultcode 0 means that the image was not taken
                switch (resultCode){
                    case -1:
                        galleryAddPic();
                        break;
                    //resultcode 0 means that the image was not taken
                    case 0:
                    default:
                        imageWasNotTaken.show();
                        break;
                }
        }
    }


    private void galleryAddPic() {
        File file = FileService.getLastestImageGenerated();
        if(file != null){
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            System.out.println(file);
            Uri contentUri = Uri.fromFile(file);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
            ((ImageView)this.findViewById(R.id.imageview)).setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));

            imageWasSavedCorrectly.show();
        }
    }
}