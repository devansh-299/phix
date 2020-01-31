package com.thebiglosers.phix.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.internal.InternalTokenProvider;
import com.thebiglosers.phix.R;

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

//                    EditText e1 = (EditText)findViewById(R.id.editext_mymessage);
//                    e1.setText(result.get(0));

                }
                break;
            case 10:
                if(requestCode == SIGN_IN_REQUEST_CODE) {
                    if(resultCode == RESULT_OK) {
                        Toast.makeText(this,
                                "Successfully signed in. Welcome!",
                                Toast.LENGTH_LONG)
                                .show();
//                        Intent i = new Intent(this, MainActivity.class);
//                        i.putExtra("userName",res)
                        startActivity(new Intent(this, MainActivity.class));
                    } else {
                        Toast.makeText(this,
                                "We couldn't sign you in. Please try again later.",
                                Toast.LENGTH_LONG).show();
                        finishAffinity();
                    }
                }

        }
    }
}
