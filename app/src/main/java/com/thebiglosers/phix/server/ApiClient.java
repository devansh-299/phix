package com.thebiglosers.phix.server;

import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.model.User;

import java.util.List;

import io.reactivex.Single;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private TransactionApi transactionApi;
    private UserApi userApi;
    private static Retrofit retrofitInstance = null;
    private static final String BASE_URL = "https://9ed4cda2.ngrok.io/";


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
    }


    public static String getBaseUrl() {
        return BASE_URL;
    }

    public Single<List<User>> getUser() {
        return userApi.getUser();
    }

    public Single<List<Transaction>> getTransaction() {
        return transactionApi.getTransaction();
    }

    public Single<User> getFriend(String searchQuery) {
        return userApi.getFriend(searchQuery);
    }
}
