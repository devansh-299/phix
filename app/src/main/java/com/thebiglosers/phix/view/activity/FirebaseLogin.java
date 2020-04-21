package com.thebiglosers.phix.view.activity;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import butterknife.ButterKnife;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.User;
import com.thebiglosers.phix.viewmodel.LoginViewModel;


public class FirebaseLogin extends AppCompatActivity {

    public static int SIGN_IN_REQUEST_CODE = 10;
    SharedPreferences preferences;
    FirebaseUser user;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        viewModel = ViewModelProviders.of(this).get(LoginViewModel.class);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            Toast.makeText(this,
                    getString(R.string.welcome) + FirebaseAuth.getInstance()
                            .getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 10:
                if(requestCode == SIGN_IN_REQUEST_CODE) {
                    if(resultCode == RESULT_OK) {
                        Toast.makeText(this,
                                R.string.message_signin_successful, Toast.LENGTH_LONG).show();

                        user = FirebaseAuth.getInstance().getCurrentUser();
                        Handler handler=new Handler();
                        Runnable r = new Runnable() {
                            public void run() {
                                // introducing delay of 5 secs
                                getUpi(user);
                            }
                        };
                        handler.postDelayed(r, 5000);

                    } else {
                        Toast.makeText(this, R.string.message_signin_failed,
                                Toast.LENGTH_LONG).show();
                        finishAffinity();
                    }
                }
        }
    }

    private void getUpi(FirebaseUser user) {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setCancelable(false);

        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_set_upi_id, null);
        dialogBuilder.setView(dialogView);

        final EditText etUpiId = dialogView.findViewById(R.id.et_upi_id);
        final EditText etMobileNumber = dialogView.findViewById(R.id.et_login_mobile);

        dialogBuilder.setPositiveButton(getString(R.string.done), (dialog, whichButton) -> {

            if (etUpiId.getText().toString().matches("") || etMobileNumber.getText()
            .toString().matches("")) {
                Toast.makeText(
                        this,
                        R.string.please_enter_details,
                        Toast.LENGTH_SHORT).show();
                getUpi(user);
            } else {

                User user1 = new User(user.getDisplayName(), user.getPhotoUrl().toString(),
                        user.getEmail(), etUpiId.getText().toString(),
                       etMobileNumber.getText().toString());

                // for saving user data for first time
                saveCurrentUser(user1);

                Log.e("LoginInUser", user.getDisplayName()
                        + user.getEmail()
                        + user.getPhotoUrl().toString()
                        + etUpiId.getText().toString()
                        + etMobileNumber.getText().toString());

                viewModel.loginUser(this, user1);
                observerViewModel();
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void observerViewModel() {
        viewModel.loginSuccessful.observe(this, loggedIn -> {
            if (loggedIn != null && loggedIn == true) {
                startMainActivity();
            } else if (loggedIn != null && loggedIn == false) {
                loginFailed();
            }
        });
    }

    private void loginFailed() {

        new AlertDialog.Builder(this)
                .setTitle(R.string.registration_failed)
                .setMessage(R.string.retry_exit)
                .setPositiveButton(R.string.retry, (dialog, which) -> {
                    getUpi(user);
                })
                .setNegativeButton(R.string.exit, ((dialogInterface, i) -> {
                    moveTaskToBack(true);
                    Process.killProcess(Process.myPid());
                    System.exit(1);
                }))
                .setIcon(R.drawable.error_image)
                .show();
    }

    private void startMainActivity() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void saveCurrentUser (User currentUser) {
        Gson gson = new Gson();
        preferences.edit().putString("current_user", gson.toJson(currentUser)).commit();
    }
}
