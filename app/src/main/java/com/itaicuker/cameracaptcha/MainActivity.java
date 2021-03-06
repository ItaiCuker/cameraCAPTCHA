package com.itaicuker.cameracaptcha;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    static final int CAPTCHA_REQUEST_CODE = 88;

    //XML Views
    Button btnCaptcha;
    TextView tvCaptcha;

    boolean flag;   //captcha result

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //bind objects to xml views
        btnCaptcha = findViewById(R.id.btnCaptcha);
        tvCaptcha = findViewById(R.id.tvCaptcha);

        //set on click
        btnCaptcha.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        //getting results from Activity:
        super.onActivityResult(requestCode, resultCode, data);

        //if CAPTCHA didn't crash:
        if (resultCode == RESULT_OK)
        {
            //getting CAPTCHA results from Activity:
            flag = data.getExtras().getBoolean("flag");
            //setting Text:
            String text = flag ? "You are not a bot!!!" : "You are a bot :(";
            tvCaptcha.setText(text);
        }
        else if (resultCode == RESULT_CANCELED)
            Toast.makeText(this, "FAIL: try again", Toast.LENGTH_LONG)
                    .show();
    }

    @Override
    public void onClick(View v)
    {
        if (v.getId() == R.id.btnCaptcha)
        {
            //initialising and calling activity:
            Intent i = new Intent(this , CaptchaActivity.class);
            startActivityForResult(i , CAPTCHA_REQUEST_CODE);
        }
    }
}