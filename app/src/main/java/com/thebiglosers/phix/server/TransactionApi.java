package com.thebiglosers.phix.server;

import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.model.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TransactionApi {

    @GET("transactions/{m_user_name}/{friend_user_name}")
    Single<List<Transaction>> getTransaction(@Path("m_user_name") String m_user_name,
                                             @Path("friend_user_name") String friend_user_name);
    @GET("all_transaction/{m_user_name}/")
    Single<List<Transaction>> getAllTransactions(@Path("m_user_name") String m_user_name);
}
