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

import com.thebiglosers.phix.R;
import com.thebiglosers.phix.view.fragment.PersonalFragment;
import com.thebiglosers.phix.view.fragment.GroupFragment;
import com.thebiglosers.phix.view.fragment.HomeFragment;

public class MainActivity extends AppCompatActivity {


    String userName;
    String userImage;
    String uniqueUserName;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams
                .FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("com.thebiglosers.phix", MODE_PRIVATE);

        if (getIntent().getStringExtra("userName")!= null) {
            setUserName(getIntent().getStringExtra("userName"));
        }
        if (getIntent().getStringExtra("userImage")!= null) {
            setUserImageString(getIntent().getStringExtra("userImage"));
        }
        if (getIntent().getStringExtra("userEmail")!=null) {
            setUniqueUserName(getIntent().getStringExtra("userEmail"));
        }

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

    private void setUniqueUserName(String userEmail) {
        String []arr = userEmail.split("@",2);
        Log.e("dasdsadsadasd",arr[0]);
        uniqueUserName = arr[0];
        preferences.edit().putString("UserUniqueName",uniqueUserName).commit();
    }

    public String getUniqueUserName() { return uniqueUserName;
    }

    private void setUserName(String currUserName) {
        userName = currUserName;
    }

    public String getUserName() {
        return userName;
    }

    private void setUserImageString(String currUserImage) {
        userImage = currUserImage;
    }

    public String getUserImageString () {
        return userImage;
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
