package com.example.monitorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class InitialActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_initial);

    }

    public void OpenMainActivity(View view) {

        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);


        //this block is disabled in the full version

//        //1695667774386 2023-09-25 21:50
//        //1727203774386 +1 year
//        //this version is going to expire within 1 year
//        System.out.println(System.currentTimeMillis());
//        if (System.currentTimeMillis() > 1727203774386L) {
//            //immediately exit from the app
//            // Is the user running Lollipop or above?
//            if (Build.VERSION.SDK_INT >= 21) {
//                // If yes, run the fancy new function to end the app and
//                //  remove it from the task list.
//                finishAndRemoveTask();
//            } else {
//                // If not, then just end the app without removing it from
//                //  the task list.
//                finish();
//            }
//        }


    }
}