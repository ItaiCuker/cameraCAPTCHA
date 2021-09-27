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
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageAction;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageKnowledge;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageModuleAction;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageObject;
import com.squareup.picasso.Picasso;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CaptchaActivity extends AppCompatActivity implements View.OnClickListener {
    //region declarations

    private static final ClientAPI CLIENT_API = ClientAPI.retrofit.create(ClientAPI.class);
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private File photoFile;

    ArrayList<ImageObject> imageObjects;
    ArrayList<String> fourItems;
    ImageButton[] btns;
    String url;

    //view objects
    TableLayout tableLayout;
    ProgressBar spinner;
    Button btnGetImage;
    Button btnGoToSimilar;
    ImageButton btn1;
    ImageButton btn2;
    ImageButton btn3;
    ImageButton btn4;

    //endregion declarations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);

        //binding view objects

        tableLayout = findViewById(R.id.tableLayout);
        spinner = findViewById(R.id.progressBar);
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


            spinner.setVisibility(View.VISIBLE);
            uploadImage(photoFile);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == btnGetImage.getId()) {
            dispatchTakePictureIntent();
        }

    }

    public void init(List<ImageObject> lst) {
        imageObjects = (ArrayList<ImageObject>) lst;
        spinner.setVisibility(View.GONE);

        if (imageObjects.size() > 3) {
            fourItems = new ArrayList<>();
            fourItems.add(url);
            for (int i = 0; i < 3; i++) {
                fourItems.add(imageObjects.get(i).contentUrl());
            }
            Log.d("cuker", fourItems.toString());
            tableLayout.setVisibility(View.VISIBLE);
            for (int i = 1; i < 4; i++) {
                Picasso.get()
                        .load(fourItems.get(i))
                        //.resize(btns[0].getLayoutParams().width, btns[0].getLayoutParams().height)
                        .into(btns[i]);

            }
        }
    }

    public void uploadImage(File photoFile) {
        Log.d("monkey", "in testExecute");
        Call<ImgurResponse> call =
                CLIENT_API.postImage(
                        MultipartBody.Part.createFormData(
                                "image",
                                photoFile.getName(),
                                RequestBody.create(photoFile, MediaType.parse("image/*"))));
        call.enqueue(new Callback<ImgurResponse>() {
            @Override
            public void onResponse(Call<ImgurResponse> call, retrofit2.Response<ImgurResponse> response) {
                ImgurResponse tmp = response.body();
                if (response.isSuccessful()) {
                    Log.d("Imgur API", "upload success! =" + tmp.getStatus());
                    url = tmp.getData().getLink();
                    reverseImageSearch(url);
                } else
                    Log.d("Imgur API", "upload no success! =" + tmp.getStatus());
            }

            @Override
            public void onFailure(Call<ImgurResponse> call, Throwable t) {
                Log.d("Imgur API", "upload fail! =" + t.toString());
            }
        });
    }

    public void reverseImageSearch(String link) {
        Call<ImageKnowledge> call =
                null;
        try {
            call = CLIENT_API.getReverseImageSearch(
                    RequestBody.create(
                            ClientAPI.mapper.writeValueAsString(new BingRequest(link)),
                            MediaType.parse("application/json")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        call.enqueue(new Callback<ImageKnowledge>() {
            @Override
            public void onResponse(Call<ImageKnowledge> call, Response<ImageKnowledge> response) {
                if (response.isSuccessful()) {
                    List<ImageObject> lst = null;
                    for (ImageAction tmp : response.body().tags().get(0).actions()) {
                        if (tmp.actionType().equals("VisualSearch")) {
                            lst = ((ImageModuleAction) tmp).data().value();
                            break;
                        }
                    }
                    init(lst);
                }
            }

            @Override
            public void onFailure(Call<ImageKnowledge> call, Throwable t) {

            }
        });
    }

    //region image procs

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Log.d("monkey", "in dispatch");
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
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