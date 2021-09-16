package com.itaicuker.cameracaptcha;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaptchaActivity extends AppCompatActivity implements View.OnClickListener
{
    //region declarations

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final ImgurAPI imgurAPI = ImgurAPI.retrofit.create(ImgurAPI.class);
    private String deleteHash;
    private File photoFile;

    //view objects
    Button btnGetImage;
    Button btnURL;
    Button btnDelete;
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
        btnURL = findViewById(R.id.btnURL);
        btnDelete =  findViewById(R.id.btnDelete);
        btn1 =  findViewById(R.id.btn1);
        btn2 =  findViewById(R.id.btn2);
        btn3 =  findViewById(R.id.btn3);
        btn4 =  findViewById(R.id.btn4);

        //setting on click
        btnGetImage.setOnClickListener(this);
        btnURL.setOnClickListener(this);
        btnDelete.setOnClickListener(this);


    }

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
        else if (!btnURL.getTag().toString().equals("1"))
        {
            if (id == btnURL.getId())
            {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(btnURL.getTag().toString()));
                startActivity(intent);
            }
            else if (id == btnDelete.getId())
            {
                Call<ResponseBody> call = imgurAPI.deleteImage(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), deleteHash));
                call.enqueue(new Callback<ResponseBody>()
                {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
                    {
                        if (response.isSuccessful())
                            Log.d("Imgur API", "delete success! =" + response.code());
                        else
                            Log.d("Imgur API", "delete no success! =" + response.code());
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t)
                    {
                        Log.d("Imgur API", "delete failed");
                    }
                });
            }
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
                                RequestBody.create(MediaType.parse("image/*"), photoFile)));
        call.enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                ImageResponse tmp = response.body();
                if (response.isSuccessful())
                {

                    Log.d("Imgur API", "upload success! =" + tmp.getStatus());
                    btnURL.setTag(tmp.getData().getLink());
                    deleteHash = tmp.getData().getDeleteHash();
                }
                else
                {
                    Log.d("Imgur API", "upload no success! =" + tmp.getStatus());
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t)
            {
                Log.d("Imgur API", "failed! =\n" + t.getMessage());
            }
        });
    }

    //region image procs

    private void dispatchTakePictureIntent()
    {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

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