package com.thebiglosers.phix.view.activity;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.User;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.et_profile_email)
    EditText etEmail;

    @BindView(R.id.et_mobile_number)
    EditText etMobileNumber;

    @BindView(R.id.et_profile_upi_id)
    EditText etUpiId;

    @BindView(R.id.tv_profile_name)
    TextView tvUserName;

    @BindView(R.id.iv_profile_image)
    ImageView ivProfile;

    SharedPreferences preferences;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);
        setUpView();
    }

    private void setUpView() {
        Gson gson = new Gson();
        currentUser = gson.fromJson(preferences.getString("current_user", ""),
                User.class);
        etEmail.setText(currentUser.getEmail());
        etEmail.setEnabled(false);
        etMobileNumber.setText(currentUser.getMobileNumber());
        etMobileNumber.setEnabled(false);
        etUpiId.setText(currentUser.getUpiString());
        etUpiId.setEnabled(false);
        tvUserName.setText(currentUser.getFullName());

        Glide.with(this)
                .load(currentUser.getImageString())
                .centerCrop()
                .circleCrop()
                .into(ivProfile);
    }

    @OnClick(R.id.btn_back)
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
