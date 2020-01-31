package com.thebiglosers.phix.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.thebiglosers.phix.view.activity.MainActivity;

public class SpashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, FirebaseLogin.class);
        startActivity(intent);
        finish();
    }
}
