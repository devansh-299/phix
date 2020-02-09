package com.thebiglosers.phix.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.User;
import com.thebiglosers.phix.view.fragment.PersonalFragment;
import com.thebiglosers.phix.view.fragment.GroupFragment;
import com.thebiglosers.phix.view.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {


    User currentUser;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams
                .FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        currentUser = getCurrentUser();
        preferences.edit().putString("UserUniqueName", getUniqueUserName());

        final Fragment homeFragment = new HomeFragment();
        final Fragment personalFragment = new PersonalFragment();
        final Fragment groupFragment = new GroupFragment();
        final FragmentManager fm = getSupportFragmentManager();
        final Fragment[] active = {homeFragment};

        fm.beginTransaction().add(R.id.frame_layout, groupFragment, "3")
                .hide(groupFragment).commit();
        fm.beginTransaction().add(R.id.frame_layout, personalFragment, "2")
                .hide(personalFragment).commit();
        fm.beginTransaction().add(R.id.frame_layout,homeFragment, "1").commit();

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_recents:
                    fm.beginTransaction().hide(active[0]).show(homeFragment).commit();
                    active[0] = homeFragment;
                    return true;
                case R.id.action_favorites:
                    fm.beginTransaction().hide(active[0]).show(personalFragment).commit();
                    active[0] = personalFragment;
                    return true;
                case R.id.action_nearby:

                    fm.beginTransaction().hide(active[0]).show(groupFragment).commit();
                    active[0] =groupFragment;
                    return true;
            }
            return false;
        });

    }


    public String getUniqueUserName() {
        String []arr = currentUser.getEmail().split("@",2);
        return arr[0];
    }

    public User getCurrentUser() {
        Gson gson = new Gson();
        return gson.fromJson(preferences.getString("current_user", ""), User.class);
    }


    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
