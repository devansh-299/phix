package com.thebiglosers.phix.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.thebiglosers.phix.server.ApiClient;
import com.thebiglosers.phix.server.UserApi;


public class FirebaseLogin extends AppCompatActivity {

    public static int SIGN_IN_REQUEST_CODE = 10;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);

        preferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);

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

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        getUpi(user);

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

        final EditText etUpiId = (EditText) dialogView.findViewById(R.id.et_upi_id);
        final EditText etMobileNumber = (EditText)dialogView.findViewById(R.id.et_login_mobile);

        dialogBuilder.setPositiveButton(getString(R.string.done), (dialog, whichButton) -> {

            if (etUpiId.getText().toString().equals("") || etMobileNumber.getText()
            .toString().equals("")) {
                // fix required
            } else {

                User user1 = new User(user.getDisplayName(), user.getPhotoUrl().toString(),
                        user.getEmail(), etUpiId.getText().toString(),
                       etMobileNumber.getText().toString());

                // for saving user data for first time
                saveCurrentUser(user1);

                UserApi userApi = ApiClient.getClient().create(UserApi.class);

                Log.e("LoginInUser", user.getDisplayName()
                        + user.getEmail()
                        + user.getPhotoUrl().toString()
                        + etUpiId.getText().toString()
                        + etMobileNumber.getText().toString());

                Call<User> call = userApi.saveUser(user1);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(retrofit2.Call<User> call, Response<User> response) {

                        int statusCode = response.code();

                        Log.e(getString(R.string.pass), getString(R.string.status_code)
                                + statusCode);
                        Log.e(getString(R.string.pass), "Body"
                                + response.body().toString());
                    }

                    @Override
                    public void onFailure(retrofit2.Call<User> call, Throwable t) {
                        Log.e(getString(R.string.fail), getString(R.string.error)
                                + t.toString());
                    }
                });

                startActivity(new Intent(this, MainActivity.class));
            }
        });

        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void saveCurrentUser (User currentUser) {
        Gson gson = new Gson();
        preferences.edit().putString("current_user", gson.toJson(currentUser)).commit();
    }
}
