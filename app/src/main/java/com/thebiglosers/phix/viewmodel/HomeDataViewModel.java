package com.thebiglosers.phix.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.thebiglosers.phix.model.HomeDataModel;
import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.server.ApiClient;
import com.thebiglosers.phix.view.fragment.HomeFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class HomeDataViewModel extends AndroidViewModel {

    public MutableLiveData<HomeDataModel> mData = new MutableLiveData<>();
    public MutableLiveData<Boolean> imageLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    private ApiClient apiClient = new ApiClient();

    private CompositeDisposable disposable = new CompositeDisposable();


    public HomeDataViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh(String userName) {
        fetchFromRemote(userName);
    }

    private void fetchFromRemote(String userName) {
        loading.setValue(true);
        disposable.add(

                apiClient.getHomeData(userName)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<HomeDataModel>() {

                            @Override
                            public void onSuccess(HomeDataModel data) {
                                Toast.makeText(getApplication(), "Data retrieved from Server",
                                        Toast.LENGTH_SHORT).show();
                                Log.e("YOUO","|||||||");
                                homeDataRetrieved(data);
                            }

                            @Override
                            public void onError(Throwable e) {
                                loading.setValue(false);
                                Log.e("ERROR",e.getMessage());
                                imageLoadError.setValue(true);
                            }
                        }));
    }

    private void homeDataRetrieved(HomeDataModel dataModel) {
        mData.setValue(dataModel);
        imageLoadError.setValue(false);
        loading.setValue(false);
    }

}
