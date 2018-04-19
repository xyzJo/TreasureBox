package com.osproj.joanaibarra.treasurebox;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FoldersView extends AppCompatActivity {
    public static final int IMAGE_GALLERY_REQUEST = 20;
    private ImageView imgPicture;

    //TODO: Commit REMOVE TODOS completed
    //TODO: Make this new button 'functionality' correspond to funny pictures ImageButton
    //TODO: Delete test RequestPermissions Button
    //TODO: use last two stackOv tabs to create openPhotos() func upon permissions_granted - openPhotos() will do so with Intents(?)

    private static int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder_view);

        ImageButton buttonRequest = findViewById(R.id.picfolder);
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(FoldersView.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    openGallery(v);
                } else {
                    requestStoragePermission();
                }
            }
        });

        //get reference to the ImageView that holds image that the user will see
        imgPicture = findViewById(R.id.imgPicture);
    }

    private void requestStoragePermission() {
        //checks if we should show user dialog that explains why permission is needed
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permission Needed")
                    .setMessage("This permission is needed to access your gallery/files")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(FoldersView.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        //Otherwise, just request the permission
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == STORAGE_PERMISSION_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted, Click the folder 1 more time to enter your Gallery", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void openGallery(View v) {
        //invoke the image gallery using an implicit Intent
        Intent filePicker = new Intent(Intent.ACTION_PICK);

        //where do we want to find the data
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        //finally get a URI representation - what Android wants to deal with.
        Uri data = Uri.parse(pictureDirectoryPath);

        //set data and type, we've created the data now we need to tell where to look for media and what type to look for
        //get all image types
        filePicker.setDataAndType(data, "image/*");

        startActivityForResult(filePicker, IMAGE_GALLERY_REQUEST);
    }


    /*
    * requestCode = constant IMAGE_GALLERY_REQUEST, a way to determine which activity we're hearing back from
    * resultCode is going to tell us if the activity that we called executed OK or if the user exited out of it
    * Intent data is going to tell be any data that that result returns
    * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {       //everything processed successfully

            if(requestCode == IMAGE_GALLERY_REQUEST) {      //hearing back from image gallery
                //URI means Universal Image Indicator
                Uri imageUri = data.getData();
                //declare a stream to read the image data from SD card
                InputStream inputStream;

                //any time your streaming for data there is a chance it might fail, thus try catch block is needed
                try {
                    //we got an input stream
                    inputStream = getContentResolver().openInputStream(imageUri);
                    
                    //get a bitmap from the stream
                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    //show the image to the user
                    imgPicture.setImageBitmap(image);


                }  catch (FileNotFoundException e) {
                    e.printStackTrace();
                    //show message to user that img is unavailable
                    Toast.makeText(this, "Unable to open image", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
