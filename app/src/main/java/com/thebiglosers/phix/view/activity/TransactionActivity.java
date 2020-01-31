package com.thebiglosers.phix.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.view.adapter.TransactionAdapter;
import com.thebiglosers.phix.viewmodel.TransactionViewModel;


import java.util.ArrayList;
import java.util.List;


public class TransactionActivity extends AppCompatActivity {

    @BindView(R.id.rv_friend_transaction)
    RecyclerView rvFriendTransaction;

    @BindView(R.id.fab_add_transaction)
    FloatingActionButton fab;

    @BindView(R.id.tv_friend_name)
    TextView tvFriendName;

    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;

    @BindView(R.id.error_layout)
    LinearLayout errorLayout;

    private List<Transaction> transactionList = new ArrayList<>();
    private TransactionAdapter mAdapter;

    private TransactionViewModel viewModel;
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);

        // getting ViewModel
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);
        viewModel.refresh();

        loadingLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);

        if (getIntent().getStringExtra("friend_name")!= null) {
            tvFriendName.setText(getIntent().getStringExtra("friend_name"));
        }

        mAdapter = new TransactionAdapter(transactionList);

        @SuppressLint("RestrictedApi") RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        rvFriendTransaction.setLayoutManager(mLayoutManager);
        rvFriendTransaction.setItemAnimator(new DefaultItemAnimator());
        rvFriendTransaction.setAdapter(mAdapter);

        rvFriendTransaction.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        fab.setOnClickListener(view -> popUpAddTransaction());

        // for temporary data
        makeTransactions();

        observeViewModel();
    }

    private void makeTransactions() {
        Transaction transaction = new Transaction((float)20.00, "Coke", "31/01/2020");
        transactionList.add(transaction);
        mAdapter.notifyDataSetChanged();
    }

    private void observeViewModel() {

        viewModel.mTransactions.observe(this, transactionParameter -> {
            if(transactionParameter!=null && transactionParameter instanceof List){
                rvFriendTransaction.setVisibility(View.VISIBLE);
                mAdapter.updateImageList(transactionParameter);
                errorLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
            }
        });

        viewModel.imageLoadError.observe(this, isError -> {
            if (isError != null && isError instanceof Boolean){
                //Toast.makeText(this, "Error getting Transactions",Toast.LENGTH_SHORT).show();
                errorLayout.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.GONE);
                //fab.setVisibility(View.GONE);
            }

        });

        viewModel.loading.observe(this, isLoading -> {
            if(isLoading!= null  && isLoading instanceof Boolean){
                //loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if(isLoading){
                    //listError.setVisibility(View.GONE);
                    rvFriendTransaction.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                }
            }
        });
    }


    public void popUpAddTransaction () {

        myDialog.setContentView(R.layout.popup_transaction);


    }

}
