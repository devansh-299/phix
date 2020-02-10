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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.User;
import com.thebiglosers.phix.view.activity.MainActivity;
import com.thebiglosers.phix.view.activity.TransactionActivity;
import com.thebiglosers.phix.view.adapter.UserAdapter;
import com.thebiglosers.phix.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import butterknife.ButterKnife;

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

        errorLayout.setVisibility(View.GONE);
        Button myView = (Button) errorLayout.findViewById( R.id.error_layout_retry );
        myView.setOnClickListener(view1 -> onRefresh());

        mAdapter = new UserAdapter(userList,getActivity());
        @SuppressLint("RestrictedApi") RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        rvUsers.setLayoutManager(mLayoutManager);
        rvUsers.setItemAnimator(new DefaultItemAnimator());
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

        viewModel.mUser.observe(this, userParemeter -> {
            if (userParemeter != null && userParemeter instanceof List) {
                rvUsers.setVisibility(View.VISIBLE);
                mAdapter.updateImageList(userParemeter);
                swipeRefreshLayout.setRefreshing(false);
                errorLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);

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
        Button btSearchFriend;
        Button btSearchAgain;
        Button btnYes;
        LinearLayout searchLayout;
        LinearLayout searchAgainLayout;
        TextView foundFriendName;

        myDialog.setContentView(R.layout.popup_user);

        searchAgainLayout = (LinearLayout) myDialog.findViewById(R.id.layout_found);
        searchAgainLayout.setVisibility(View.GONE);
        searchLayout = (LinearLayout) myDialog.findViewById(R.id.layout_search_friend);
        etFriendName = (EditText) myDialog.findViewById(R.id.et_friend_name);
        btSearchFriend = (Button) myDialog.findViewById(R.id.bt_search_friend);
        btSearchAgain = (Button) myDialog.findViewById(R.id.bt_search_again);
        foundFriendName = (TextView) myDialog.findViewById(R.id.tv_found_friend);
        btnYes = (Button) myDialog.findViewById(R.id.btn_yes_);

        btSearchFriend.setOnClickListener(view -> {
            String friendName  = etFriendName.getText().toString();
            viewModel.fetchFriend(friendName,((MainActivity) getActivity()).getUniqueUserName());

            viewModel.friend.observe(getActivity(), friendParameter -> {
                if (friendParameter != null && friendParameter instanceof User) {
                    foundFriendName.setText("Loading...");
                    btnYes.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.GONE);
                    searchAgainLayout.setVisibility(View.VISIBLE);
                }
            });

            // for success
            viewModel.foundFriendName.observe(getActivity(), fName -> {
                if (fName != null && fName instanceof String){
                    foundFriendName.setText(fName);
                    btnYes.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                    searchAgainLayout.setVisibility(View.VISIBLE);
                }

            });

            // for error
            viewModel.friendNotFound.observe(getActivity(), isError -> {
                if (isError != null && isError instanceof Boolean) {
                    foundFriendName.setText("No user found!");
                    btnYes.setVisibility(View.GONE);
                    searchLayout.setVisibility(View.GONE);
                    searchAgainLayout.setVisibility(View.VISIBLE);
                }

            });
        });

        btnYes.setOnClickListener(view -> myDialog.dismiss());

        btSearchAgain.setOnClickListener(view -> {
            searchLayout.setVisibility(View.VISIBLE);
            searchAgainLayout.setVisibility(View.GONE);
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}
