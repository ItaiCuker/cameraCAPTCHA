package com.itaicuker.cameracaptcha;


import android.content.Intent;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;

public class CaptchaActivity extends AppCompatActivity implements View.OnClickListener
{
    //region declarations

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final ImgurAPI imgurAPI = ImgurAPI.retrofit.create(ImgurAPI.class);
    private File photoFile;

    //view objects
    Button btnGetImage;
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
        btn1 =  findViewById(R.id.btn1);
        btn2 =  findViewById(R.id.btn2);
        btn3 =  findViewById(R.id.btn3);
        btn4 =  findViewById(R.id.btn4);

        //setting on click
        btnGetImage.setOnClickListener(this);
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
            testExecute(photoFile);
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


    private void testExecute(File photoFile)
    {
        Log.d("monkey", "in testExecute");
        Call<ImageResponse> call =
                imgurAPI.postImage(
                        MultipartBody.Part.createFormData(
                                "image",
                                photoFile.getName(),
                                RequestBody.create(photoFile, MediaType.parse("image/*"))));
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, retrofit2.Response<ImageResponse> response)
            {
                ImageResponse tmp = response.body();
                if (response.isSuccessful())
                    Log.d("Imgur API", "upload success! =" + tmp.getStatus());
                else
                    Log.d("Imgur API", "upload no success! =" + tmp.getStatus());
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t)
            {
                Log.d("Imgur API", "upload fail! =" + t.toString());
            }
        });
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

    //endregion image procs
}