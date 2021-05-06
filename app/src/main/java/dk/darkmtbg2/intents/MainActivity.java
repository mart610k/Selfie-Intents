package dk.darkmtbg2.intents;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStorageDirectory;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    File currentPhoto;
    int REQUEST_IMAGE_CAPTURE = 0;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"Intents");

        if(!storageDir.isDirectory()){
            storageDir.mkdirs();
        }
        System.out.println("FILE PATH:" + storageDir);

        currentPhoto = new File(
            storageDir,
            imageFileName + ".jpg"
                 );

        return currentPhoto;
    }

    public void dispatchTakePictureIntent(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                System.out.println("photoFile:" + photoFile);

                //Uri photoURI = Uri.fromFile(photoFile);
                //Uri photoURI = FileProvider.getUriForFile(this, "dk.darkmtbg2.intents" , photoFile);


                Uri photoURI =
                        FileProvider.getUriForFile(this,
                        "dk.darkmtbg2.intents",
                        photoFile);

                takePictureIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data != null){
            System.out.println(data.getData());
        }

        galleryAddPic();
    }

    private void galleryAddPic() {

        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        System.out.println(currentPhoto);
        Uri contentUri = Uri.fromFile(currentPhoto);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        ((ImageView)this.findViewById(R.id.imageview)).setImageBitmap(BitmapFactory.decodeFile(currentPhoto.getAbsolutePath()));
    }

}