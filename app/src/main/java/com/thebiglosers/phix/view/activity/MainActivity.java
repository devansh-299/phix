package com.thebiglosers.phix.view.activity;


import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        preferences.edit().putString("UserUniqueName", getUniqueUserName()).commit();

        final Fragment homeFragment = new HomeFragment();
        final Fragment personalFragment = new PersonalFragment();
        final Fragment groupFragment = new GroupFragment();

        replaceFragment(homeFragment, true, R.id.frame_layout);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_recents:
                    replaceFragment(homeFragment, true, R.id.frame_layout);
                    return true;
                case R.id.action_favorites:
                    replaceFragment(personalFragment, true, R.id.frame_layout);
                    return true;
                case R.id.action_nearby:
                    replaceFragment(groupFragment, true, R.id.frame_layout);
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

    public void replaceFragment(Fragment fragment, boolean addToBackStack, int containerId) {
        invalidateOptionsMenu();
        String backStateName = fragment.getClass().getName();
        boolean fragmentPopped = getSupportFragmentManager().popBackStackImmediate(backStateName,
                0);

        if (!fragmentPopped && getSupportFragmentManager().findFragmentByTag(backStateName) ==
                null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.enter_from_right,
                    R.anim.exit_to_left,
                    R.anim.enter_from_left,
                    R.anim.exit_to_right);
            transaction.replace(containerId, fragment, backStateName);
            if (addToBackStack) {
                transaction.addToBackStack(backStateName);
            }
            transaction.commit();
        }
    }
}
