package com.thebiglosers.phix.server;

import com.thebiglosers.phix.model.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Url;


public interface UserApi {

    @GET("friends/testuser")
    Single<List<User>> getUser();

    @GET
    Single<User> getFriend(@Url String url);

}
