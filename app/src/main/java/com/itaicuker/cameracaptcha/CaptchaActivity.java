package com.itaicuker.cameracaptcha;

import android.app.Activity;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageAction;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageKnowledge;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageModuleAction;
import com.microsoft.azure.cognitiveservices.search.visualsearch.models.ImageObject;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
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

    final ClientAPI CLIENT_API = ClientAPI.retrofit.create(ClientAPI.class);    //my retrofit client object
    final int REQUEST_IMAGE_CAPTURE = 1;    //request code for result
    Picasso picasso;    //Async image loader lib object
    File photoFile; //saved image file

    //game vars
    String url; //the saved imgur url of client photo
    int count;  //number of (5 attempts and you get kicked from CAPTCHA)

    ArrayList<String> fourItems, urlPool;
    ImageButton[] btns;

    //view objects
    LinearLayout linearLayout;
    ProgressBar spinner;
    Button btnTakeImage;
    ImageButton btn1;
    ImageButton btn2;
    ImageButton btn3;
    ImageButton btn4;

    //endregion declarations

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captcha);

        picasso = new Picasso.Builder(this).build();    //init picasso obj
        //binding view objects:
        linearLayout = findViewById(R.id.linearLayout);
        spinner = findViewById(R.id.progressBar);
        btnTakeImage = findViewById(R.id.btnTakeImage);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);

        btns = new ImageButton[]{btn1, btn2, btn3, btn4};   //binding buttons to array of buttons.

        //setting on click
        for (int i = 0; i < 4; i++) {
            btns[i].setOnClickListener(this);
        }
        btnTakeImage.setOnClickListener(this);

        linearLayout.setVisibility(View.GONE);   //hiding ImageButtons (at start)
    }

    /**
     * deleting cache data on destroy
     */
    //region onDestroy
    @Override
    protected void onDestroy() {
        deleteCacheData();
        super.onDestroy();
    }

    /**
     * function to delete all files from getCacheDir()
     */
    public void deleteCacheData() {
        File cacheDir = this.getCacheDir();
        File[] files = cacheDir.listFiles();

        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    /**
     * function that sends boolean result back to MainActivity
     *
     * @param flag the boolean result
     */
    public void sendResult(boolean flag) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra("flag", flag);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    //endregion onDestroy

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK)    //if camera result is good
        {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());  //getting the bitmap of picture
            bitmap = resize(bitmap, 4000, 4000);  //resizing bitmap to less than max size that Bing API can use

            try {
                photoFile.delete(); //deleting file
                OutputStream os = new BufferedOutputStream(new FileOutputStream(photoFile));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, os);    //compressing bitmap to save it back to file.
                os.flush();
                os.close();
                bitmap.recycle();
            } catch (IOException e) {
                e.printStackTrace();
            }

            spinner.setVisibility(View.VISIBLE);    //showing loading spinner on UI
            uploadImage(photoFile); //starting upload function
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == btnTakeImage.getId()) {    //button to take picture
            linearLayout.setVisibility(View.GONE);
            dispatchTakePictureIntent();
        }
        //image buttons
        else if (v.getTag().toString().equals(url))  //if client correct
            sendResult(true);
        else {  //incorrect
            count++;
            if (count == 5) //if client failed too many times
                sendResult(false);
            next();
        }
    }

    /**
     * initializing CAPTCHA game. calling first round afterwards if no fail
     *
     * @param lst list of Bing result.
     */
    public void init(ArrayList<ImageObject> lst) {
        count = 0;  //resetting count

        urlPool = new ArrayList<>();
        if (lst.size() > 3) {   //if sufficient results
            for (int i = 0; i < lst.size(); i++)
                urlPool.add(lst.get(i).contentUrl());
            next();
        } else {   //if Bing API doesn't have enough pictures.
            new AlertDialog.Builder(this)
                    .setMessage("Image does not have sufficient amount of similar results, try to take another.")
                    .setTitle("Maybe try another picture :/")
                    .setPositiveButton("TAKE PICTURE", (dialog, which) -> dispatchTakePictureIntent());
        }
    }

    /**
     * function to re shuffle image buttons.
     * loads pictures from web using Picasso library
     */
    public void next() {
        //shuffles and add items:

        Collections.shuffle(urlPool);

        fourItems = new ArrayList<>();
        fourItems.add(url);
        for (int i = 0; i < 3; i++)
            fourItems.add(urlPool.get(i));
        Collections.shuffle(fourItems);
        Log.d("cuker", fourItems.toString());

        //showing loading spinner and hiding ImageButtons
        spinner.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.GONE);


        //loading images to ImageButton views
        for (int i = 0; i < 4; i++) {
            int finalI = i; //to use I inside inner class
            picasso.load(fourItems.get(i))
                    .into(btns[i], new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            if (finalI == 3)    ////Waiting for last image to show imageButtons
                            {
                                //hiding spinner and showing ImageButtons
                                spinner.setVisibility(View.GONE);
                                linearLayout.setVisibility(View.VISIBLE);
                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.e("Picasso", e.toString());
                        }
                    });
            btns[i].setTag(fourItems.get(i));   //changing tag so I can find clients image.
        }
    }

    //region API calls

    /**
     * API function to upload image to imgur API anonymously.
     *
     * @param photoFile the File object of image
     */
    public void uploadImage(File photoFile) {

        Call<ImgurResponse> call =  //Call object
                CLIENT_API.postImage(
                        MultipartBody.Part.createFormData(
                                "image",
                                photoFile.getName(),
                                RequestBody.create(photoFile, MediaType.parse("image/*"))));

        /* ↑ initializing the HTTP request using requirements of API ↑ */
        /* ↓ enqueuing the request Asynchronously and creating callback for it↓ */

        call.enqueue(new Callback<ImgurResponse>() {
            @Override
            public void onResponse(Call<ImgurResponse> call, @NotNull retrofit2.Response<ImgurResponse> response) {
                ImgurResponse tmp = response.body();
                if (response.isSuccessful()) {  //if http returns success status
                    url = tmp.getData().getLink();  //getting string url
                    reverseImageSearch(url);    //calling bing API function
                }
            }

            @Override
            public void onFailure(@NotNull Call<ImgurResponse> call, @NotNull Throwable t) {
                Log.d("Imgur API", "upload fail! =" + t.toString());
            }
        });
    }

    /**
     * API function to  to get bing API visual search results for.
     * getting result from callback via java objects
     *
     * @param link url of image
     */
    public void reverseImageSearch(String link)
    {
        Call<ImageKnowledge> call = null;

        try {
            call = CLIENT_API.getReverseImageSearch(
                    RequestBody.create(
                            ClientAPI.mapper.writeValueAsString(new BingRequest(link)), //creating object to parse to JSON string
                            MediaType.parse("application/json")));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //same as before, function is 2 parts.

        call.enqueue(new Callback<ImageKnowledge>() {
            @Override
            public void onResponse(@NotNull Call<ImageKnowledge> call, @NotNull Response<ImageKnowledge> response) {
                if (response.isSuccessful()) {  //if http returns success status
                    List<ImageObject> lst = null;   //creating list
                    for (ImageAction tmp : response.body().tags().get(0).actions()) {   //iterating JSON object to get required data.
                        if (tmp.actionType().equals("VisualSearch")) {
                            lst = ((ImageModuleAction) tmp).data().value();
                            break;
                        }
                    }
                    init((ArrayList<ImageObject>) lst); //starting CAPTCHA
                }
            }

            @Override
            public void onFailure(@NotNull Call<ImageKnowledge> call, @NotNull Throwable t) {

            }
        });
    }

    //endregion API calls

    //region image functions

    /**
     * calling intent to take picture from phones camera and saving it in cache directory of app
     */
    private void dispatchTakePictureIntent() {
        linearLayout.setVisibility(View.GONE);   //hiding ImageButtons.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //creating the intent with wanted action

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
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.itaicuker.cameracaptcha.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * @return empty File object to save image on it.
     * @throws IOException because using storage
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String imageFileName = "JPEG_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_";
        File storageDir = getCacheDir();
        return File.createTempFile(
                imageFileName,    /* prefix */
                ".jpg",     /* suffix */
                storageDir        /* directory */
        );
    }

    /**
     * resize image to max values
     *
     * @param image     the bitmap to resize
     * @param maxWidth  max width in pixels
     * @param maxHeight max height in pixels
     * @return Bitmap object of resized bitmap
     */
    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        //image dimensions
        int width = image.getWidth();
        int height = image.getHeight();

        //calculating ratios
        float ratioBitmap = (float) width / (float) height;
        float ratioMax = (float) maxWidth / (float) maxHeight;

        int finalWidth = maxWidth;
        int finalHeight = maxHeight;

        //checking which dimension to change to reach dimension goal
        if (ratioMax > ratioBitmap) {
            finalWidth = (int) ((float) maxHeight * ratioBitmap);
        } else {
            finalHeight = (int) ((float) maxWidth / ratioBitmap);
        }
        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);    //scaling image
        return image;
    }
    //endregion image functions
}