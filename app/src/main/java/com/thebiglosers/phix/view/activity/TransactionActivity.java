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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.model.User;
import com.thebiglosers.phix.server.ApiClient;
import com.thebiglosers.phix.server.TransactionApi;
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
    View errorLayout;

    @BindView(R.id.not_found)
    View notFoundLayout;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<Transaction> transactionList = new ArrayList<>();
    private TransactionAdapter mAdapter;

    private TransactionViewModel viewModel;
    Dialog myDialog;
    Dialog addTransactionDialog;

    String friendUniqueUserName;
    String mUniqueUserName;
    String friendUpiId;
    String friendFullName;

    User friendUser;

    Float finalAmount;

    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        ButterKnife.bind(this);

        preferences = getSharedPreferences(getString(R.string.preferences), MODE_PRIVATE);

        // getting ViewModel
        viewModel = ViewModelProviders.of(this).get(TransactionViewModel.class);

        notFoundLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        Button myView = (Button) errorLayout.findViewById( R.id.error_layout_retry );
        myView.setOnClickListener(view1 -> onRefresh());

        myDialog = new Dialog(this);
        addTransactionDialog = new Dialog(this);

        swipeRefreshLayout.setOnRefreshListener(this);

        if (getIntent().getParcelableExtra("selected_friend") != null) {
            friendUser = getIntent().getParcelableExtra("selected_friend");
            friendUniqueUserName = getIntent().getStringExtra("friend_unique_username");
            tvFriendName.setText(friendUser.getFullName());
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
                            intent.putExtra("friendUpiId", friendUpiId);
                            intent.putExtra("friendFullName", friendFullName);
                            popUpSplitAmount(transactionList.get(position).getAmount(), intent);
                        })
        );

        viewModel.refresh(mUniqueUserName, friendUniqueUserName);

        fab.setOnClickListener(view -> popUpAddTransaction());

        observeViewModel();
    }

    private void popUpSplitAmount(Float amount, Intent intent) {

        RadioGroup radioGroup;
        Button btnPay;

        myDialog.setContentView(R.layout.popup_split_amount);

        btnPay = myDialog.findViewById(R.id.bt_pay);
        radioGroup = myDialog.findViewById(R.id.rd_split);
        btnPay.setEnabled(false);

        finalAmount = amount;
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            switch(checkedId){
                case R.id.split_half:
                    finalAmount = amount /2;
                    intent.putExtra("Amount", Float.toString(finalAmount));
                    btnPay.setEnabled(true);
                    break;
                case R.id.owe_total:
                    btnPay.setEnabled(true);
                    finalAmount = amount;
                    intent.putExtra("Amount", Float.toString(finalAmount));
                    break;
            }
        });
        btnPay.setOnClickListener(view -> startActivity(intent));

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }

    @SuppressLint("RestrictedApi")
    private void observeViewModel() {

        viewModel.mTransactions.observe(this, transactionParameter -> {
            if (transactionParameter != null && transactionParameter instanceof List) {

                if (transactionParameter.isEmpty()) {
                    notFoundLayout.setVisibility(View.VISIBLE);
                    rvFriendTransaction.setVisibility(View.GONE);
                    notFoundLayout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                } else {
                    notFoundLayout.setVisibility(View.GONE);
                    rvFriendTransaction.setVisibility(View.VISIBLE);
                    mAdapter.updateImageList(transactionParameter);
                    errorLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }
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
            if (loaded != null && loaded == true) {
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
                    notFoundLayout.setVisibility(View.GONE);
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

        LinearLayout addLayout;
        RelativeLayout transactionLoading;
        EditText etAmount;
        EditText etDescription;
        Button btAddTransaction;
        LottieAnimationView checkAnimation;
        LottieAnimationView errorAnimation;
        ProgressBar progressBar;
        addTransactionDialog.setContentView(R.layout.popup_transaction);

        addLayout = addTransactionDialog.findViewById(R.id.add_transaction_layout);
        btAddTransaction = addTransactionDialog.findViewById(R.id.popup_add_transaction);
        etAmount = addTransactionDialog.findViewById(R.id.popup_amount);
        etDescription = addTransactionDialog.findViewById(R.id.popup_description);
        transactionLoading = addTransactionDialog.findViewById(R.id.transaction_loading);
        progressBar = addTransactionDialog.findViewById(R.id.add_transaction_progress);
        checkAnimation = addTransactionDialog.findViewById(R.id.done_adding_transaction);
        errorAnimation = addTransactionDialog.findViewById(R.id.error_adding_animation);


        transactionLoading.setVisibility(View.GONE);
        checkAnimation.setVisibility(View.GONE);
        errorAnimation.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        btAddTransaction.setOnClickListener(view -> {

            addLayout.setVisibility(View.GONE);
            transactionLoading.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);


            TransactionApi transactionApi = ApiClient.getClient().create(TransactionApi.class);
            Call<Transaction> call = transactionApi.addTransaction(
                    etDescription.getText().toString(),
                    Float.valueOf(etAmount.getText().toString()),
                    mUniqueUserName,
                    friendUniqueUserName);

            call.enqueue(new Callback<Transaction>() {
                @Override
                public void onResponse(retrofit2.Call<Transaction> call, Response<Transaction>
                        response) {

                    int statusCode = response.code();

                    progressBar.setVisibility(View.GONE);
                    checkAnimation.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Transaction Added",
                            Toast.LENGTH_SHORT).show();

                    Log.e("TRANS_PASS", getString(R.string.status_code)
                            + statusCode);
                    try {
                        Log.e("TRANS_PASS", "Body" + response.body().toString());
                    } catch (Exception e) {
                        Log.e ("TRANS PASS", "Empty Response");
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<Transaction> call, Throwable t) {
                    Log.e("FAIL_TRANS", getString(R.string.error)
                            + t.toString());

                    progressBar.setVisibility(View.GONE);
                    errorAnimation.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Error occurred",
                            Toast.LENGTH_SHORT).show();

                }
            });
        });
        addTransactionDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        addTransactionDialog.show();

    }

    @Override
    public void onRefresh() {
        viewModel.refresh(mUniqueUserName, friendUniqueUserName);
    }
}
