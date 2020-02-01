package com.thebiglosers.phix.view.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.User;
import com.thebiglosers.phix.server.ApiClient;
import com.thebiglosers.phix.server.UserApi;

import java.util.ArrayList;

public class FirebaseLogin extends AppCompatActivity {

    public static int SIGN_IN_REQUEST_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);


        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            Toast.makeText(this,
                    "Welcome " + FirebaseAuth.getInstance()
                            .getCurrentUser().getDisplayName(), Toast.LENGTH_LONG).show();

            // passing user data
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("userName", user.getDisplayName());
            intent.putExtra("userImage",user.getPhotoUrl().toString() );
            intent.putExtra("userEmail",user.getEmail());

            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(
                            RecognizerIntent.EXTRA_RESULTS);
                }
                break;
            case 10:
                if(requestCode == SIGN_IN_REQUEST_CODE) {
                    if(resultCode == RESULT_OK) {
                        Toast.makeText(this,
                                "Successfully signed in. Welcome!",
                                Toast.LENGTH_LONG)
                                .show();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.putExtra("userName", user.getDisplayName());
                        intent.putExtra("userImage",user.getPhotoUrl().toString() );
                        intent.putExtra("userEmail",user.getEmail());

                        // saving user to database + getting upi
                        getUpi(user,intent);

                    } else {
                        Toast.makeText(this,
                                "We couldn't sign you in. Please try again later.",
                                Toast.LENGTH_LONG).show();
                        finishAffinity();
                    }
                }
        }
    }

    private void getUpi(FirebaseUser user, Intent i) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.alert_set_upi_id, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.et_upi_id);

        dialogBuilder.setPositiveButton("Done", (dialog, whichButton) -> {
            if (edt.getText().toString().isEmpty()) {
                edt.setError("Cannot leave blank");
            } else {
                User user1 = new User(user.getDisplayName(), user.getPhotoUrl().toString(),
                        user.getEmail(), edt.getText().toString());

                UserApi userApi = ApiClient.getClient().create(UserApi.class);
                Toast.makeText(this,user.getDisplayName()+
                        user.getEmail()+user.getPhotoUrl().toString()+edt.getText().toString(),Toast.LENGTH_SHORT).show();
                Log.e("dasdad", user.getDisplayName()+
                        user.getEmail()+user.getPhotoUrl().toString()+edt.getText().toString());
                Call<User> call = userApi.saveUser(user1);
                call.enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(retrofit2.Call<User> call, Response<User> response) {

                        int statusCode = response.code();

                        Log.e("PASS", "Status Code: " + statusCode);

                    }

                    @Override
                    public void onFailure(retrofit2.Call<User> call, Throwable t) {
                        Log.e("FAIL", "Error: " + t.toString());
                    }

                });

                startActivity(i);
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }
}
