package com.thebiglosers.phix.view.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.thebiglosers.phix.R;

import androidx.fragment.app.Fragment;
import butterknife.ButterKnife;

public class GroupFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

}
