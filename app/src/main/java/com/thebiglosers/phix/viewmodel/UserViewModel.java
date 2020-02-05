package com.thebiglosers.phix.viewmodel;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.model.User;
import com.thebiglosers.phix.server.ApiClient;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class UserViewModel extends AndroidViewModel {

    public MutableLiveData<List<User>> mUser = new MutableLiveData<>();
    public MutableLiveData<Boolean> imageLoadError = new MutableLiveData<>();
    public MutableLiveData<Boolean> successfullyLoaded = new MutableLiveData<>();
    public MutableLiveData<Boolean> foundFriend = new MutableLiveData<>();
    public MutableLiveData<Boolean> friendNotFound = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>();

    public MutableLiveData<User> friend = new MutableLiveData<>();

    private ApiClient apiClient = new ApiClient();

    private CompositeDisposable disposable = new CompositeDisposable();

    public UserViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh(String userName) {
        fetchFromRemote(userName);
    }

    private void fetchFromRemote(String mUser) {
        loading.setValue(true);
        disposable.add(

                apiClient.getUser(mUser)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<User>>() {

                            @Override
                            public void onSuccess(List<User> users) {
                                Toast.makeText(getApplication(), "Data retrieved from Server",
                                        Toast.LENGTH_SHORT).show();
                                usersRetrieved(users);
                            }

                            @Override
                            public void onError(Throwable e) {
                                loading.setValue(false);
                                Log.e("USER ERROR",e.getMessage());
                                imageLoadError.setValue(true);
                            }
                        }));
    }


    private void usersRetrieved(List<User> users) {
        mUser.setValue(users);
        successfullyLoaded.setValue(true);
        imageLoadError.setValue(false);
        loading.setValue(false);
    }


    public void fetchFriend(String friendUserName, String mUniqueUserName) {
        disposable.add(
                apiClient.getFriend("friend/"+mUniqueUserName+"/"+friendUserName)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<User>() {

                            @Override
                            public void onSuccess(User mfriend) {
                                Toast.makeText(getApplication(), "Found",
                                        Toast.LENGTH_SHORT).show();
                                Log.e("Positive","GOT FRIEND");
                                foundFriend.setValue(true);
                                friend.setValue(mfriend);
                            }

                            @Override
                            public void onError(Throwable e) {
                                loading.setValue(false);
                                Log.e("Friend ERROR",e.getMessage());
                                friendNotFound.setValue(true);
                            }
                        }));
    }

}
