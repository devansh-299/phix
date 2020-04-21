package com.thebiglosers.phix.viewmodel;


import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.server.ApiClient;
import com.thebiglosers.phix.server.TransactionApi;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TransactionViewModel extends AndroidViewModel {

    public MutableLiveData<List<Transaction>> mTransactions = new MutableLiveData<>();
    public MutableLiveData<List<Transaction>> mAllTransactions = new MutableLiveData<>();
    public MutableLiveData<Boolean> imageLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> successfullyLoadedAllTransactions = new MutableLiveData<>();
    public MutableLiveData<Boolean> successfullyLoadedTransaction = new MutableLiveData<>();
    public MutableLiveData<Boolean> transactionAdded = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();
    private ApiClient apiClient = new ApiClient();
    TransactionApi transactionApi;
    private CompositeDisposable disposable = new CompositeDisposable();


    public TransactionViewModel(@NonNull Application application) {

        super(application);
        transactionApi = ApiClient.getClient().create(TransactionApi.class);
    }

    public void refresh(String mUniqueUserId, String friendUniqueId) {
        fetchFromRemote(mUniqueUserId,friendUniqueId);
    }

    private void fetchFromRemote(String mUniqueUserId, String friendUniqueId) {
        loading.setValue(true);
        disposable.add(

                apiClient.getTransaction(mUniqueUserId, friendUniqueId)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Transaction>>() {

                            @Override
                            public void onSuccess(List<Transaction> transactions) {
                                Log.e("GOT TRANS","transactions found");
                                transactionsRetrived(transactions);
                            }

                            @Override
                            public void onError(Throwable e) {
                                loading.setValue(false);
                                Log.e("ERROR",e.getMessage());
                                imageLoadError.setValue(true);
                            }
                        }));
    }

    private void transactionsRetrived(List<Transaction> transactions) {
        mTransactions.setValue(transactions);
        successfullyLoadedTransaction.setValue(true);
        imageLoadError.setValue(false);
        loading.setValue(false);
    }
    private void allTransactionsRetrived(List<Transaction> transactions) {
        mAllTransactions.setValue(transactions);
        successfullyLoadedAllTransactions.setValue(true);
        imageLoadError.setValue(false);
        loading.setValue(false);
    }

    public void refresh1(String uniqueUserName) {
            fetchAllFromRemote(uniqueUserName);
    }

    private void fetchAllFromRemote(String uniqueUserName) {

        loading.setValue(true);
        disposable.add(
                apiClient.getAllTransaction(uniqueUserName)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<Transaction>>() {

                            @Override
                            public void onSuccess(List<Transaction> transactions) {
                                Log.e("GOT TRANS","transactions found");
                                allTransactionsRetrived(transactions);
                            }

                            @Override
                            public void onError(Throwable e) {
                                loading.setValue(false);
                                Log.e("ERROR",e.getMessage());
                                imageLoadError.setValue(true);
                            }
                        }));
    }

    public void addTransaction(Context context, String mUniqueUserName, String friendUniqueUserName,
                               float amount, String description) {
        Call<Transaction> call = transactionApi.addTransaction(
                description,
                amount,
                mUniqueUserName,
                friendUniqueUserName);

        call.enqueue(new Callback<Transaction>() {
            @Override
            public void onResponse(retrofit2.Call<Transaction> call, Response<Transaction>
                    response) {

                int statusCode = response.code();
                transactionAdded.setValue(true);
;
                Log.e("TRANS_PASS", context.getString(R.string.status_code)
                        + statusCode);
                try {
                    Log.e("TRANS_PASS", "Body" + response.body().toString());
                } catch (Exception e) {
                    Log.e ("TRANS PASS", "Empty Response");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<Transaction> call, Throwable t) {

                Log.e("FAIL_TRANS", context.getString(R.string.error)
                        + t.toString());
                transactionAdded.setValue(false);
            }
        });
    }
}
