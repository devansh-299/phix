package com.thebiglosers.phix.view.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.LinearLayout;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.Transaction;

import com.thebiglosers.phix.view.activity.MainActivity;

import com.thebiglosers.phix.view.adapter.TransactionAdapter;

import com.thebiglosers.phix.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class GroupFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_Transactions)
    RecyclerView rvUsers;

    @BindView(R.id.loading_layout)
    ShimmerRecyclerView loadingLayout;

    @BindView(R.id.error_layout_personal)
    LinearLayout errorLayout;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;


    private List<Transaction> transactionList = new ArrayList<>();
    private TransactionAdapter mAdapter;
    Dialog myDialog;

    private TransactionViewModel viewModel;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        ButterKnife.bind(this, view);

        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        viewModel.refresh1(((MainActivity) getActivity()).getUniqueUserName());

        mAdapter = new TransactionAdapter(transactionList);
        @SuppressLint("RestrictedApi") RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        rvUsers.setLayoutManager(mLayoutManager);
        rvUsers.setItemAnimator(new DefaultItemAnimator());
        rvUsers.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        myDialog = new Dialog(getActivity());
        observeViewModel();

        return view;
    }

    private void observeViewModel() {

        viewModel.mAllTransactions.observe(getActivity(), mAll -> {
            if (mAll != null && mAll instanceof List) {
                rvUsers.setVisibility(View.VISIBLE);
                mAdapter.updateImageList(mAll);
                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
            }
        });

        // for error
        viewModel.imageLoadError.observe(getActivity(), isError -> {
            if (isError != null && isError instanceof Boolean) {
                rvUsers.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        // for success
        viewModel.successfullyLoadedAllTransactions.observe(this, loaded -> {
            if (loaded != null && loaded instanceof Boolean){
                rvUsers.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }

        });

        // for loading
        viewModel.loading.observe(getActivity(), isLoading -> {
            if (isLoading != null && isLoading instanceof Boolean) {
                if (isLoading) {
                    rvUsers.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        viewModel.refresh1(((MainActivity) getActivity()).getUniqueUserName());
    }

}
