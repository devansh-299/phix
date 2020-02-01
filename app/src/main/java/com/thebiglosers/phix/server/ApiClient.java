package com.thebiglosers.phix.server;

import com.thebiglosers.phix.model.HomeDataModel;
import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.model.User;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private TransactionApi transactionApi;
    private UserApi userApi;
    private HomeDataApi homeDataApi;
    private static Retrofit retrofitInstance = null;
    private static final String BASE_URL = "https://15ad2c3c.ngrok.io/";


    public ApiClient() {
        transactionApi = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(TransactionApi.class);

        userApi = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(UserApi.class);
        homeDataApi = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
                .create(HomeDataApi.class);
    }

    public static Retrofit getClient() {
        if (retrofitInstance == null) {
            retrofitInstance = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitInstance;
    }


    public static String getBaseUrl() {
        return BASE_URL;
    }

    public Single<List<User>> getUser(String userName) {
        return userApi.getUser(userName);
    }

    public Single<List<Transaction>> getTransaction(String mUserName, String friendUserName) {
        return transactionApi.getTransaction(mUserName, friendUserName);
    }

    public Single<User> getFriend(String searchQuery) {
        return userApi.getFriend(searchQuery);
    }

    public Single<HomeDataModel> getHomeData(String userName) {
        return homeDataApi.getData(userName);
    }

    public Single<List<Transaction>> getAllTransaction(String uniqueUserName) {
        return transactionApi.getAllTransactions(uniqueUserName);
    }
}
