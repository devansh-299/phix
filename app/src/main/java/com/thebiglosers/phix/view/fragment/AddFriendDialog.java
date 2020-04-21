package com.thebiglosers.phix.view.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.User;
import com.thebiglosers.phix.view.adapter.UserAdapter;
import com.thebiglosers.phix.viewmodel.FriendViewModel;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;


public class AddFriendDialog extends BottomSheetDialogFragment {

    @BindView(R.id.add_friend_layout)
    LinearLayout addLayout;

    @BindView(R.id.loading_layout)
    RelativeLayout loadingLayout;

    @BindView(R.id.friend_list_layout)
    LinearLayout friendListLayout;

    @BindView(R.id.bottomsheet_friend_list)
    RecyclerView rvFriendList;

    @BindView(R.id.et_search_query)
    EditText etSearchQuery;

    @BindView(R.id.bottomsheet_add_friend)
    Button btSearchFriend;

    @BindView(R.id.add_friend_progress)
    ProgressBar progressBar;

    @BindView(R.id.done_adding_friend)
    LottieAnimationView checkAnimation;

    @BindView(R.id.error_adding_animation)
    LottieAnimationView errorAnimation;

    private BottomSheetBehavior mBottomSheetBehavior;
    private FriendViewModel viewModel;
    private UserAdapter mAdapter;
    private List<User> friendList = new ArrayList<>();


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.bottomsheet_add_friend, null);

        dialog.setContentView(view);
        mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        ButterKnife.bind(this, view);

        viewModel = ViewModelProviders.of(getActivity()).get(FriendViewModel.class);
        mAdapter = new UserAdapter(friendList, getContext());
        @SuppressLint("RestrictedApi") RecyclerView.LayoutManager mLayoutManager =
                new LinearLayoutManager(getContext());
        rvFriendList.setLayoutManager(mLayoutManager);
        rvFriendList.setItemAnimator(new DefaultItemAnimator());
        rvFriendList.addItemDecoration(new DividerItemDecoration(rvFriendList.getContext(),
                DividerItemDecoration.VERTICAL));
        rvFriendList.setAdapter(mAdapter);

        return dialog;
    }

    public static AddFriendDialog newInstance () {

        AddFriendDialog fragment = new AddFriendDialog();
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        loadingLayout.setVisibility(View.GONE);
        checkAnimation.setVisibility(View.GONE);
        errorAnimation.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        friendListLayout.setVisibility(View.GONE);
        btSearchFriend.setOnClickListener(v -> btAddTransaction());
    }

    private void btAddTransaction() {

        if (etSearchQuery.getText().toString().equals("")) {
            Toast.makeText(getContext(), R.string.please_complete_details, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        addLayout.setVisibility(View.GONE);
        loadingLayout.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        viewModel.searchFriend(etSearchQuery.getText().toString());
        observeViewModel();
    }

    private void observeViewModel() {

        viewModel.friendList.observe(getActivity(), friendList -> {
            if (friendList != null && friendList instanceof List) {

                if (friendList.isEmpty()) {
                    addLayout.setVisibility(View.VISIBLE);
                    friendListLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    btSearchFriend.setText(R.string.search_again);
                    Toast.makeText(getContext(), R.string.no_friend_found, Toast.LENGTH_SHORT)
                            .show();
                } else {
                    addLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.GONE);
                    friendListLayout.setVisibility(View.VISIBLE);
                    mAdapter.updateImageList(friendList);
                }
            }
        });

        // for error
        viewModel.fetchingError.observe(getActivity(), isError -> {
            if (isError != null && isError == true) {
                addLayout.setVisibility(View.GONE);
                friendListLayout.setVisibility(View.GONE);
                loadingLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                checkAnimation.setVisibility(View.GONE);
                errorAnimation.setVisibility(View.VISIBLE);
            }
        });

        // in progress
        viewModel.isLoading.observe(getActivity(), isLoading -> {
            if (isLoading != null && isLoading instanceof Boolean) {
                if (isLoading) {
                    addLayout.setVisibility(View.GONE);
                    friendListLayout.setVisibility(View.GONE);
                    loadingLayout.setVisibility(View.VISIBLE);
                    checkAnimation.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    errorAnimation.setVisibility(View.GONE);
                }
            }
        });
    }
}
