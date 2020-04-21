package com.thebiglosers.phix.server;


import com.thebiglosers.phix.model.Transaction;
import java.util.List;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;


public interface TransactionApi {

    @GET("transactions/{m_user_name}/{friend_user_name}")
    Single<List<Transaction>> getTransaction(@Path("m_user_name") String m_user_name,
                                             @Path("friend_user_name") String friend_user_name);

    @GET("all_transaction/{m_user_name}/")
    Single<List<Transaction>> getAllTransactions(@Path("m_user_name") String m_user_name);

    @FormUrlEncoded
    @POST("create_transaction/")
    Call<Transaction> addTransaction(@Field("description") String description,
                                     @Field("amount") Float amount,
                                     @Field("source") String source,
                                     @Field("destination") String destination);
}
