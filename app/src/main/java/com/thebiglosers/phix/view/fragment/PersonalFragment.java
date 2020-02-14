package com.thebiglosers.phix.view.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.User;
import com.thebiglosers.phix.server.ApiClient;
import com.thebiglosers.phix.server.UserApi;
import com.thebiglosers.phix.view.activity.MainActivity;
import com.thebiglosers.phix.view.activity.TransactionActivity;
import com.thebiglosers.phix.view.adapter.UserAdapter;
import com.thebiglosers.phix.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class PersonalFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.rv_Transactions)
    RecyclerView rvUsers;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.loading_layout)
    NestedScrollView loadingLayout;

    @BindView(R.id.error_layout)
    View errorLayout;

    @BindView(R.id.not_found)
    View notFoundLayout;

    @BindView(R.id.swipe_container)
    SwipeRefreshLayout swipeRefreshLayout;


    private List<User> userList = new ArrayList<>();
    private UserAdapter mAdapter;
    Dialog myDialog;

    private UserViewModel viewModel;

    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_personal, container, false);
        ButterKnife.bind(this, view);

        viewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        viewModel.refresh(((MainActivity) getActivity()).getUniqueUserName());


        TextView errorText = (TextView) notFoundLayout.findViewById( R.id.tv_not_found );
        errorText.setText("You currently have no friends");

        Button errorButton = (Button) errorLayout.findViewById( R.id.error_layout_retry );
        errorButton.setOnClickListener(view1 -> onRefresh());

        notFoundLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);

        mAdapter = new UserAdapter(userList,getActivity());
        @SuppressLint("RestrictedApi") RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        rvUsers.setLayoutManager(mLayoutManager);
        rvUsers.setItemAnimator(new DefaultItemAnimator());
        rvUsers.addItemDecoration(new DividerItemDecoration(rvUsers.getContext(),
                DividerItemDecoration.VERTICAL));
        rvUsers.setAdapter(mAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        rvUsers.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(),
                        (view1, position) -> {
                            Intent intent = new Intent(getActivity(), TransactionActivity.class);
                            User friendUser = new User (userList.get(position).getFullName(),
                                    userList.get(position).getImageString(),
                                    userList.get(position).getEmail(),
                                    userList.get(position).getUpiString(),
                                    userList.get(position).getMobileNumber());
                            intent.putExtra("selected_friend", friendUser);
                            intent.putExtra("friend_unique_username",userList.get(position)
                                    .getUserName());
                            startActivity(intent);
                        })
        );

        rvUsers.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        myDialog = new Dialog(getActivity());
        fab.setOnClickListener(view12 -> popAddFriend());
        observeViewModel();

        return view;
    }

    @SuppressLint("RestrictedApi")
    private void observeViewModel() {

        viewModel.mUser.observe(getActivity(), userParemeter -> {
            if (userParemeter != null && userParemeter instanceof List) {

                if (userParemeter.isEmpty()) {
                    rvUsers.setVisibility(View.GONE);
                    notFoundLayout.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                    errorLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                } else {
                    notFoundLayout.setVisibility(View.GONE);
                    rvUsers.setVisibility(View.VISIBLE);
                    mAdapter.updateImageList(userParemeter);
                    swipeRefreshLayout.setRefreshing(false);
                    errorLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    fab.setVisibility(View.VISIBLE);
                }

            }
        });

        // for error
        viewModel.imageLoadError.observe(getActivity(), isError -> {
            if (isError != null && isError == true) {
                loadingLayout.setVisibility(View.GONE);
                rvUsers.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                fab.setVisibility(View.GONE);
            }
        });

        // for success
        viewModel.successfullyLoaded.observe(getActivity(), loaded -> {
            if (loaded != null && loaded == true){
                loadingLayout.setVisibility(View.GONE);
                rvUsers.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                fab.setVisibility(View.VISIBLE);
            }
        });

        // in progress
        viewModel.loading.observe(getActivity(), isLoading -> {
            if (isLoading != null && isLoading instanceof Boolean) {
                //loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    rvUsers.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(true);
                    fab.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onRefresh() {
        viewModel.refresh(((MainActivity) getActivity()).getUniqueUserName());
    }


    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context,
                    new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    public void popAddFriend() {

        EditText etFriendName;
        EditText etFriendNumber;
        Button btSearchFriend;
        Button btSearchAgain;
        Button selectNameInput;
        Button selectNumberInput;
        Button btnYes;
        LinearLayout searchLayout;
        LinearLayout searchAgainLayout;
        LinearLayout topLayout;
        TextView foundFriendName;
        AtomicInteger FLAG = new AtomicInteger();

        myDialog.setContentView(R.layout.popup_user);

        topLayout = myDialog.findViewById(R.id.popup_top_bar);
        searchAgainLayout = myDialog.findViewById(R.id.layout_found);
        searchAgainLayout.setVisibility(View.GONE);
        searchLayout = myDialog.findViewById(R.id.layout_search_friend);
        etFriendName = myDialog.findViewById(R.id.et_friend_name);
        etFriendNumber = myDialog.findViewById(R.id.et_friend_phone_number);
        selectNameInput = myDialog.findViewById(R.id.bt_search_friend);
        selectNumberInput = myDialog.findViewById(R.id.bt_search_phone_number);
        btSearchFriend = myDialog.findViewById(R.id.bt_search_friend);
        btSearchAgain = myDialog.findViewById(R.id.bt_search_again);
        foundFriendName = myDialog.findViewById(R.id.tv_found_friend);
        btnYes = myDialog.findViewById(R.id.btn_yes_);

        // defining initial status
        etFriendNumber.setVisibility(View.GONE);

        // selecting Number Input
        selectNumberInput.setOnClickListener(view -> {
            FLAG.set(1);
            etFriendNumber.setVisibility(View.VISIBLE);
            etFriendName.setVisibility(View.GONE);

        });

        // selecting Name Input
        selectNameInput.setOnClickListener(view -> {
            FLAG.set(0);
            etFriendName.setVisibility(View.VISIBLE);
            etFriendNumber.setVisibility(View.GONE);

        });

        btSearchFriend.setOnClickListener(view -> {

            // for loading
            foundFriendName.setText("Loading...");
            btnYes.setVisibility(View.GONE);
            searchLayout.setVisibility(View.GONE);
            searchAgainLayout.setVisibility(View.VISIBLE);
            topLayout.setVisibility(View.GONE);

            Call<User> call;

            UserApi userApi = ApiClient.getClient().create(UserApi.class);
            if (FLAG.get() == 0) {
                call = userApi.searchFriendByName(etFriendName.getText().toString());
            } else {
                call = userApi.searchFriendByNumber(etFriendNumber.getText().toString());
            }

            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(retrofit2.Call<User> call, Response<User>
                        response) {

                    //foundFriendName.setText();
                    btnYes.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                    searchAgainLayout.setVisibility(View.VISIBLE);

                    Log.e("ADD FRIEND", "Call Successful");
                }

                @Override
                public void onFailure(retrofit2.Call<User> call, Throwable t) {

                    foundFriendName.setText("No user found!");
                    btnYes.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.GONE);
                    searchAgainLayout.setVisibility(View.VISIBLE);

                    Log.e("ADD FRIEND", "Call Unsuccessful "+t.getMessage());
                }
            });
        });

        btnYes.setOnClickListener(view -> {
            Toast.makeText(getActivity(), "Friend Added",Toast.LENGTH_SHORT).show();
            myDialog.dismiss();
        });

        btSearchAgain.setOnClickListener(view -> {
            topLayout.setVisibility(View.VISIBLE);
            searchLayout.setVisibility(View.VISIBLE);
            searchAgainLayout.setVisibility(View.GONE);
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}
