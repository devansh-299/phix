package com.thebiglosers.phix.view.activity;


import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.thebiglosers.phix.R;
import java.util.ArrayList;


public class PaymentActivity extends AppCompatActivity {

    @BindView(R.id.et_payment_amount)
    EditText etAmount;

    @BindView(R.id.et_payment_upi_id)
    EditText etUpiId;

    @BindView(R.id.et_payment_name)
    EditText etName;

    @BindView(R.id.et_payment_description)
    EditText etDescription;

    @BindView(R.id.bt_payment_pay)
    Button btPay;

    final int UPI_PAYMENT = 299;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);

        if (getIntent().getStringExtra("Amount") != null) {
            etAmount.setText(getIntent().getStringExtra("Amount"));
        }
        if (getIntent().getStringExtra("friendUpiId") != null) {
            etUpiId.setText(getIntent().getStringExtra("friendUpiId"));
        }
        if (getIntent().getStringExtra("friendFullName") != null) {
            etName.setText(getIntent().getStringExtra("friendFullName"));
        }
    }


    @OnClick(R.id.bt_payment_pay)
    public void beginPayment() {
        String amount = etAmount.getText().toString();
        String name = etName.getText().toString();
        String upiId = etUpiId.getText().toString();
        String description = etDescription.getText().toString();

        if (amount.isEmpty()) {
            etAmount.setError(getString(R.string.et_error_amount));
            return;
        }
        if (name.isEmpty()) {
            etName.setError(getString(R.string.et_error_name));
            return;
        }
        if (upiId.isEmpty()) {
            etUpiId.setError(getString(R.string.et_error_upi_id));
            return;
        }
        if (description.isEmpty()) {
            description = " ";
        }
        payUsingUpi(amount, upiId, name, description);
    }

    void payUsingUpi(String amount, String upiId, String name, String note) {

        Uri uri = Uri.parse("upi://pay").buildUpon()
                .appendQueryParameter("pa", upiId)
                .appendQueryParameter("pn", name)
                .appendQueryParameter("tn", note)
                .appendQueryParameter("am", amount)
                .appendQueryParameter("cu", "INR")
                .build();


        Intent upiPayIntent = new Intent(Intent.ACTION_VIEW);
        upiPayIntent.setData(uri);

        // app chooser
        Intent chooser = Intent.createChooser(upiPayIntent, getString(R.string.pay_with));

        // check if intent resolves
        if (null != chooser.resolveActivity(getPackageManager())) {
            startActivityForResult(chooser, UPI_PAYMENT);
        } else {
            Toast.makeText(this, R.string.no_upi_app_found, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case UPI_PAYMENT:
                if ((RESULT_OK == resultCode) || (resultCode == 11)) {
                    if (data != null) {
                        String trxt = data.getStringExtra("response");
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add(trxt);
                        upiPaymentDataOperation(dataList);
                    } else {
                        ArrayList<String> dataList = new ArrayList<>();
                        dataList.add("nothing");
                        upiPaymentDataOperation(dataList);
                    }
                } else {
                    //when user simply back without payment
                    ArrayList<String> dataList = new ArrayList<>();
                    dataList.add("nothing");
                    upiPaymentDataOperation(dataList);
                }
                break;
        }
    }

    private void upiPaymentDataOperation(ArrayList<String> data) {
        if (isConnectionAvailable(this)) {
            String str = data.get(0);
            String paymentCancel = "";
            if (str == null) str = "discard";
            String status = "";
            String approvalRefNo = "";
            String response[] = str.split("&");
            for (int i = 0; i < response.length; i++) {
                String equalStr[] = response[i].split("=");
                if (equalStr.length >= 2) {
                    if (equalStr[0].toLowerCase().equals("Status".toLowerCase())) {
                        status = equalStr[1].toLowerCase();
                    } else if (equalStr[0].toLowerCase().equals("ApprovalRefNo".toLowerCase()) ||
                            equalStr[0].toLowerCase().equals("txnRef".toLowerCase())) {
                        approvalRefNo = equalStr[1];
                    }
                } else {
                    paymentCancel = "Payment cancelled by user.";
                }
            }

            if (status.equals("success")) {
                //Code to handle successful transaction here.
                Toast.makeText(this, R.string.transaction_successful,
                        Toast.LENGTH_SHORT).show();
            } else if ("Payment cancelled by user.".equals(paymentCancel)) {
                Toast.makeText(this, R.string.transaction_cancelled_by_user,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.transaction_failed,
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this,
                    R.string.payment_no_internet, Toast.LENGTH_SHORT).show();
        }
    }

    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
