package com.thebiglosers.phix.server;

import com.thebiglosers.phix.model.User;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;


public interface UserApi {

    @GET("friends/{user_name}/")
    Single<List<User>> getUser(@Path("user_name") String user_name);

    @GET
    Single<User> getFriend(@Url String url);


    @Headers("Accept: application/json")
    @POST("/create_user/")
    Call<User> saveUser(@Body User user);

}
