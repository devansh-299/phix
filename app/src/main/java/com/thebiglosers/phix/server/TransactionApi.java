package com.thebiglosers.phix.server;

import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.model.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;

public interface TransactionApi {

    @GET("DevTides/DogsApi/master/dogs.json")
    Single<List<Transaction>> getTransaction();
}
