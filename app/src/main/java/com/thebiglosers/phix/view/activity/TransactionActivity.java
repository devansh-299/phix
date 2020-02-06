package com.thebiglosers.phix.view.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.view.adapter.TransactionAdapter;
import com.thebiglosers.phix.view.fragment.PersonalFragment;
import com.thebiglosers.phix.viewmodel.TransactionViewModel;

import java.util.ArrayList;
import java.util.List;


public class TransactionActivity extends AppCompatActivity implements
        SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_friend_transaction)
    RecyclerView rvFriendTransaction;

    @BindView(R.id.fab_add_transaction)
    FloatingActionButton fab;

    @BindView(R.id.tv_friend_name)
    TextView tvFriendName;

    @BindView(R.id.iv_friend)
    ImageView ivFriend;

    @BindView(R.id.loading_layout)
    NestedScrollView loadingLayout;

    @BindView(R.id.error_layout)
    LinearLayout errorLayout;


    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<Transaction> transactionList = new ArrayList<>();
    private TransactionAdapter mAdapter;

    private TransactionViewModel viewModel;
    Dialog myDialog;

    String friendUniqueUserName;
    String mUniqueUserName;
    String friendUpiId;
    String friendFullName;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);

        preferences = getSharedPreferences("com.thebiglosers.phix", MODE_PRIVATE);

        // getting ViewModel
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);


        swipeRefreshLayout.setOnRefreshListener(this);

        if (getIntent().getStringExtra("friend_name") != null) {
            tvFriendName.setText(getIntent().getStringExtra("friend_name"));
        }

        if (getIntent().getStringExtra("friend_unique_username") != null) {
            friendUniqueUserName = getIntent().getStringExtra("friend_unique_username");
        }

        if (getIntent().getStringExtra("friendUPIID") != null) {
            friendUpiId = getIntent().getStringExtra("friendUPIID");
        }
        if (getIntent().getStringExtra("friendFullName") != null) {
            friendFullName = getIntent().getStringExtra("friendFullName");
        }


        mUniqueUserName = preferences.getString("UserUniqueName", "");

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.splash_icon)
                .error(R.drawable.splash_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.IMMEDIATE);

        Glide.with(this).load(getIntent().getStringExtra("friend_image"))
                .apply(options)
                .into(ivFriend);

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

        rvFriendTransaction.addOnItemTouchListener(
                new PersonalFragment.RecyclerItemClickListener(getApplicationContext(),
                        (view1, position) -> {
                            Intent intent = new Intent(this, PaymentActivity.class);
                            intent.putExtra("Amount",
                                    Float.toString(transactionList.get(position).getAmount()));
                            intent.putExtra("friendUpiId", friendUpiId);
                            intent.putExtra("friendFullName", friendFullName);
                            startActivity(intent);
                        })
        );

        viewModel.refresh(mUniqueUserName, friendUniqueUserName);

        fab.setOnClickListener(view -> popUpAddTransaction());

        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.mTransactions.observe(this, transactionParameter -> {
            if (transactionParameter != null && transactionParameter instanceof List) {
                rvFriendTransaction.setVisibility(View.VISIBLE);
                mAdapter.updateImageList(transactionParameter);
                errorLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
            }
        });

        // for error
        viewModel.imageLoadError.observe(this, isError -> {
            if (isError != null && isError == true) {
                errorLayout.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.GONE);
                rvFriendTransaction.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        // for success
        viewModel.successfullyLoadedTransaction.observe(this, loaded -> {
            if (loaded != null && loaded == true){
                errorLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                rvFriendTransaction.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }

        });


        viewModel.loading.observe(this, isLoading -> {
            if (isLoading != null && isLoading instanceof Boolean) {
                if (isLoading) {
                    errorLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.VISIBLE);
                    rvFriendTransaction.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(true);
                }
            }
        });
    }

    public void popUpAddTransaction() {
        myDialog.setContentView(R.layout.popup_transaction);
    }

    @Override
    public void onRefresh() {
        viewModel.refresh(mUniqueUserName, friendUniqueUserName);
    }
}
