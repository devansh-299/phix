package com.thebiglosers.phix.view.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.view.activity.MainActivity;
import com.thebiglosers.phix.view.activity.PaymentActivity;

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
        Glide
                .with(getContext())
                .load(((MainActivity) getActivity()).getUserImageString())
                .centerCrop()
                .circleCrop()
                .into(ivUserImage);
        tvUserName.setText(((MainActivity) getActivity()).getUserName());
    }

    @OnClick(R.id.timepass)
    public void yoPay() {
        startActivity(new Intent(getContext(), PaymentActivity.class));
    }
}
