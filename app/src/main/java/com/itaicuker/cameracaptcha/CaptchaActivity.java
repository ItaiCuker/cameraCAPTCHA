package com.itaicuker.cameracaptcha;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CaptchaActivity extends AppCompatActivity implements View.OnClickListener {
    //region declarations

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private File photoFile;

    ImageButton[] btns;

    //view objects
    Button btnGetImage;
    Button btnGoToSimilar;
    ImageButton btn1;
    ImageButton btn2;
    ImageButton btn3;
    ImageButton btn4;

    //endregion declarations

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);

        //binding view objects
        btnGetImage = findViewById(R.id.btnGetImage);
        btnGoToSimilar = findViewById(R.id.btnGoToSimilar);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        //binding buttons to array of buttons.
        btns = new ImageButton[]{btn1, btn2, btn3, btn4};

        //setting on click
        btnGetImage.setOnClickListener(this);
        btnGoToSimilar.setOnClickListener(this);
    }

    /**
     * deleting cache data on destroy
     */
    //region onDestroy

    @Override
    protected void onDestroy()
    {
        deleteCacheData();
        super.onDestroy();
    }

    public void deleteCacheData()
    {
        File cacheDir = this.getCacheDir();
        File[] files = cacheDir.listFiles();

        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    //endregion onDestroy

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)
        {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            bitmap = resize(bitmap, 4000, 4000);
            try {
                photoFile.delete();
                OutputStream os = new BufferedOutputStream(new FileOutputStream(photoFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.flush();
                os.close();
                bitmap.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }
            CaptchaClient.uploadImage(photoFile);
        }
    }

    @Override
    public void onClick(View v)
    {
        int id = v.getId();

        if (id == btnGetImage.getId())
        {
            dispatchTakePictureIntent();
        }

    }

    //region image procs

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("monkey", "in dispatch");
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        {
            // Create the File where the photo should go
            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Toast.makeText(this, "Err: camera unavailable!", Toast.LENGTH_SHORT);
            }
            // Continue only if the File was successfully created
            if (photoFile != null)
            {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.itaicuker.cameracaptcha.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException
    {
        // Create an image file name
        String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
        File storageDir = getCacheDir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return image;
    }

    /**
     * resize image to max values
     * @param image
     * @param maxWidth
     * @param maxHeight
     * @return resized bitmap
     */
    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight)
    {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }
    //endregion image procs
}