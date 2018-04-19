package com.osproj.joanaibarra.treasurebox;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.Toast;
import java.io.File;

public class FoldersView extends AppCompatActivity {
    public static final int IMAGE_GALLERY_REQUEST = 20;

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
                    //TODO: remove this toast and just openGallery()
                    Toast.makeText(FoldersView.this, "You have already granted this permission", Toast.LENGTH_SHORT).show();
                    openGallery(v);
                } else {
                    requestStoragePermission();
                }
            }
        });
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
                //TODO: MAYBE get rid of this toast?
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                //view not recognized? Have to click again to enter?
                //openGallery();
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
}
