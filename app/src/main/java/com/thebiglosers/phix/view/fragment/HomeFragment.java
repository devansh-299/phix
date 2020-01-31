package com.thebiglosers.phix.view.fragment;


import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebiglosers.phix.R;
import com.thebiglosers.phix.view.activity.MainActivity;

public class HomeFragment extends Fragment {

    @BindView(R.id.iv_user_image)
    ImageView ivUserImage;

    @BindView(R.id.tv_user_name)
    TextView tvUserName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this,view);
        setUserData();
        return view;
    }

    private void setUserData() {
//        Uri imageUri = Uri.parse(((MainActivity) getActivity()).getUserImageString());
//        ivUserImage.setImageURI(imageUri);
        tvUserName.setText(((MainActivity) getActivity()).getUserName());
    }
}
