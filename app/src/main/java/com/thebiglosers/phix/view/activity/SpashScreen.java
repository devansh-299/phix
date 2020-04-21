package com.thebiglosers.phix.view.activity;


import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import com.thebiglosers.phix.R;


public class SpashScreen extends AppCompatActivity {

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        preferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);

        Intent intent;

        if (preferences.getBoolean(getString(R.string.check_first),true)) {
            intent = new Intent(this, IntroductionActivity.class);
            preferences.edit().putBoolean(getString(R.string.check_first), false).commit();
        } else {
            intent = new Intent(this, FirebaseLogin.class);
        }
        startActivity(intent);
        finish();
    }
}
