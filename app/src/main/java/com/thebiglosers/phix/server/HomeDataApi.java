package com.thebiglosers.phix.server;

import com.thebiglosers.phix.model.HomeDataModel;
import com.thebiglosers.phix.model.Transaction;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface HomeDataApi {

    @GET("analytics/{user_name}")
    Single<HomeDataModel> getData(@Path("user_name") String user_name);
}
