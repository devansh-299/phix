package com.thebiglosers.phix.view.fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import com.thebiglosers.phix.view.activity.MainActivity;
import com.thebiglosers.phix.view.activity.TransactionActivity;
import com.thebiglosers.phix.view.adapter.UserAdapter;
import com.thebiglosers.phix.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class PersonalFragment extends Fragment {

    @BindView(R.id.rv_Transactions)
    RecyclerView rvUsers;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.loading_layout)
    LinearLayout loadingLayout;

    @BindView(R.id.error_layout)
    LinearLayout errorLayout;


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

        mAdapter = new UserAdapter(userList,getActivity());
        @SuppressLint("RestrictedApi") RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getApplicationContext());
        rvUsers.setLayoutManager(mLayoutManager);
        rvUsers.setItemAnimator(new DefaultItemAnimator());
        rvUsers.setAdapter(mAdapter);


        rvUsers.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(),
                        (view1, position) -> {
                            Intent intent = new Intent(getActivity(), TransactionActivity.class);
                            intent.putExtra("friend_name", userList.get(position)
                                    .getFullName());
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

    private void observeViewModel() {

        viewModel.mUser.observe(this, userParemeter -> {
            if (userParemeter != null && userParemeter instanceof List) {
                rvUsers.setVisibility(View.VISIBLE);
                mAdapter.updateImageList(userParemeter);
                errorLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.GONE);
                fab.setVisibility(View.VISIBLE);
            }
        });

        viewModel.imageLoadError.observe(this, isError -> {
            if (isError != null && isError instanceof Boolean) {
                Toast.makeText(getActivity(), "Error getting Friends", Toast.LENGTH_SHORT).show();
                errorLayout.setVisibility(View.VISIBLE);
                loadingLayout.setVisibility(View.GONE);
                //fab.setVisibility(View.GONE);
            }

        });

        viewModel.loading.observe(this, isLoading -> {
            if (isLoading != null && isLoading instanceof Boolean) {
                //loadingView.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    rvUsers.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.VISIBLE);
                    errorLayout.setVisibility(View.GONE);
                    fab.setVisibility(View.GONE);
                }
            }
        });
    }


    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
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
        LinearLayout searhcLayout;
        LinearLayout searchAgainLayout;
        TextView foundFrienName;

        myDialog.setContentView(R.layout.popup_user);

        searchAgainLayout = (LinearLayout) myDialog.findViewById(R.id.layout_found);
        searchAgainLayout.setVisibility(View.GONE);
        searhcLayout = (LinearLayout) myDialog.findViewById(R.id.layout_search_friend);
        etFriendName = (EditText) myDialog.findViewById(R.id.et_friend_name);
        btSearchFriend = (Button) myDialog.findViewById(R.id.bt_search_friend);
        btSearchAgain = (Button) myDialog.findViewById(R.id.bt_search_again);
        foundFrienName = (TextView) myDialog.findViewById(R.id.tv_found_friend);
        btnYes = (Button) myDialog.findViewById(R.id.btn_yes_);

        btSearchFriend.setOnClickListener(view -> {
            String friendName  = etFriendName.getText().toString();
            viewModel.fetchFriend(friendName);

            viewModel.friend.observe(this, friendParameter -> {
                if (friendParameter != null && friendParameter instanceof User) {
                    rvUsers.setVisibility(View.VISIBLE);
                    foundFrienName.setText(friendParameter.getFullName());
                    searhcLayout.setVisibility(View.GONE);
                    searchAgainLayout.setVisibility(View.VISIBLE);
                }
            });

            viewModel.imageLoadError.observe(this, isError -> {
                if (isError != null && isError instanceof Boolean) {
                    foundFrienName.setText("No user found!");
                    searhcLayout.setVisibility(View.GONE);
                    searchAgainLayout.setVisibility(View.VISIBLE);
                }

            });
        });

        btnYes.setOnClickListener(view -> myDialog.dismiss());

        btSearchAgain.setOnClickListener(view -> {
            searhcLayout.setVisibility(View.VISIBLE);
            searchAgainLayout.setVisibility(View.GONE);
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}
