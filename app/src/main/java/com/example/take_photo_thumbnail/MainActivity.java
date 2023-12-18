package com.example.take_photo_thumbnail;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Bundle extras = data.getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");

                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageBitmap(imageBitmap);

                    }
                }
            });

    ActivityResultLauncher<Intent> someActivityResultLauncher2 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // There are no request codes
                        Intent data = result.getData();
                        Uri uri = data.getData();
                        ImageView imageView = findViewById(R.id.imageView);
                        imageView.setImageURI(uri);
                    }
                }
            });

    ActivityResultLauncher<Intent> someActivityResultLauncher3 = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File f = new File(currentPhotoPath);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    MainActivity.this.sendBroadcast(mediaScanIntent);
                    ImageView imageView = findViewById(R.id.imageView);
                    imageView.setImageURI(contentUri);
                }
            });
    Button btnSelect;
    Button btnGalery;
    Button btnFullSize;
    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }



    public static int RC_PHOTO_PICKER = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btnSelect = findViewById(R.id.button);
        btnGalery = findViewById(R.id.button2);
        btnFullSize = findViewById(R.id.fullSize);
        btnSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                //Launch activity to get result
                someActivityResultLauncher.launch(intent);
            }
        });
        btnGalery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create Intent
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                //Launch activity to get result
                someActivityResultLauncher2.launch(intent);
            }
        });
       btnFullSize.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
                   Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                   // Ensure that there's a camera activity to handle the intent
                   //if (takePictureIntent.resolveActivity(getPackageManager()) != null)
                   {
                       // Create the File where the photo should go
                       File photoFile = null;
                       try {
                           photoFile = createImageFile();
                       } catch (IOException ex) {
                           // Error occurred while creating the File
                       }
                       // Continue only if the File was successfully created
                       if (photoFile != null) {
                           Uri photoURI = FileProvider.getUriForFile(MainActivity.this,
                                   "com.example.take_photo_thumbnail.fileprovider",
                                   photoFile);
                           takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                           someActivityResultLauncher3.launch(takePictureIntent);
                       }
                   }
               }

       });
    }
}