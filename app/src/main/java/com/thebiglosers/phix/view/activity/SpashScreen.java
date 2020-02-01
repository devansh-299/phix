package com.thebiglosers.phix.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class SpashScreen extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences("com.thebiglosers.phix", MODE_PRIVATE);

        Intent intent;

        if (preferences.getBoolean("check_first_time",true)) {
            intent = new Intent(this, IntroductionActivity.class);
            preferences.edit().putBoolean("check_first_time", false).commit();
        } else {
            intent = new Intent(this, FirebaseLogin.class);
        }
        startActivity(intent);
        finish();
    }
}
