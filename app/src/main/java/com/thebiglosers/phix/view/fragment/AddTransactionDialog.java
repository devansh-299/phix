package com.thebiglosers.phix.view.fragment;


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
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.viewmodel.TransactionViewModel;
import butterknife.BindView;
import butterknife.ButterKnife;


public class AddTransactionDialog extends BottomSheetDialogFragment {

    @BindView(R.id.add_transaction_layout)
    LinearLayout addLayout;

    @BindView(R.id.transaction_loading)
    RelativeLayout transactionLoading;

    @BindView(R.id.bottomsheet_amount)
    EditText etAmount;

    @BindView(R.id.bottomsheet_description)
    EditText etDescription;

    @BindView(R.id.bottomsheet_add_transaction)
    Button btAddTransaction;

    @BindView(R.id.add_transaction_progress)
    ProgressBar progressBar;

    @BindView(R.id.done_adding_transaction)
    LottieAnimationView checkAnimation;

    @BindView(R.id.error_adding_animation)
    LottieAnimationView errorAnimation;

    private static final String userUnique = "UNIQUE_USER_NAME";
    private static final String friendUnique = "FRIEND_UNIQUE_USER_NAME";
    private BottomSheetBehavior mBottomSheetBehavior;
    private String mUniqueUserName;
    private String friendUniqueUserName;
    private TransactionViewModel viewModel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        View view = View.inflate(getContext(), R.layout.bottomsheet_add_transaction, null);

        dialog.setContentView(view);
        mBottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        ButterKnife.bind(this, view);
        viewModel = ViewModelProviders.of(getActivity()).get(TransactionViewModel.class);

        if (getArguments() != null) {
            mUniqueUserName = getArguments().getString(userUnique);
            friendUniqueUserName = getArguments().getString(friendUnique);
        }
        return dialog;
    }

    public static AddTransactionDialog newInstance (String mUniqueUserName,
                                                        String friendUniqueUserName) {

        AddTransactionDialog fragment = new AddTransactionDialog();
        Bundle bundle = new Bundle();
        bundle.putString(userUnique, mUniqueUserName);
        bundle.putString(friendUnique, friendUniqueUserName);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        transactionLoading.setVisibility(View.GONE);
        checkAnimation.setVisibility(View.GONE);
        errorAnimation.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        btAddTransaction.setOnClickListener(v -> btAddTransaction());
    }

    private void btAddTransaction() {

        if (etAmount.getText().toString().equals("")
                || etDescription.getText().toString().equals("")) {
            Toast.makeText(getContext(), R.string.please_complete_details, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        addLayout.setVisibility(View.GONE);
        transactionLoading.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        viewModel.addTransaction(getActivity(), mUniqueUserName, friendUniqueUserName,
                Float.valueOf(etAmount.getText().toString()),etDescription.getText().toString());
        observerViewModel();
    }

    private void observerViewModel() {
        viewModel.transactionAdded.observe(this, isAdded -> {
            if (isAdded != null && isAdded == true) {
                progressBar.setVisibility(View.GONE);
                checkAnimation.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), R.string.transaction_added,
                        Toast.LENGTH_SHORT).show();
            } else if (isAdded != null && isAdded == false) {
                progressBar.setVisibility(View.GONE);
                errorAnimation.setVisibility(View.VISIBLE);
                Toast.makeText(getActivity(), "Error occurred",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
