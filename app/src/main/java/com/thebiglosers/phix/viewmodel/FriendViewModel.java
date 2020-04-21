package com.thebiglosers.phix.viewmodel;


import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.thebiglosers.phix.model.User;
import com.thebiglosers.phix.server.ApiClient;
import java.util.List;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;


public class FriendViewModel extends AndroidViewModel {

    public MutableLiveData<Boolean> fetchingError = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    public MutableLiveData<List<User>> friendList = new MutableLiveData<>();

    private ApiClient apiClient = new ApiClient();

    private CompositeDisposable disposable = new CompositeDisposable();

    public FriendViewModel(@NonNull Application application) {
        super(application);
    }

    public void searchFriend(String searchQuery) {
        isLoading.setValue(true);
        disposable.add(
                apiClient.searchFriend(searchQuery)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<User>>() {

                            @Override
                            public void onSuccess(List<User> users) {
                                isLoading.setValue(false);
                                try {
                                    friendList.setValue(users);
                                } catch (Exception e) {
                                    Log.e("EMPTY FR LIST", e.getMessage());
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                isLoading.setValue(false);
                                Log.e("FRIEND ERROR",e.getMessage());
                                fetchingError.setValue(true);
                            }
                        }));
    }
}
