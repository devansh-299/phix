package com.thebiglosers.phix.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.server.ApiClient;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class TransactionViewModel extends AndroidViewModel {

    public MutableLiveData<List<Transaction>> mTransactions = new MutableLiveData<>();
    public MutableLiveData<List<Transaction>> mAllTransactions = new MutableLiveData<>();
    public MutableLiveData<Boolean> imageLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private ApiClient apiClient = new ApiClient();

    private CompositeDisposable disposable = new CompositeDisposable();


    public TransactionViewModel(@NonNull Application application) {
        super(application);
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
                                Toast.makeText(getApplication(), "Data retrieved from Server",
                                        Toast.LENGTH_SHORT).show();
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
        imageLoadError.setValue(false);
        loading.setValue(false);
    }
    private void allTransactionsRetrived(List<Transaction> transactions) {
        mAllTransactions.setValue(transactions);
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
                                Toast.makeText(getApplication(), "Data retrieved from Server",
                                        Toast.LENGTH_SHORT).show();
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
}
