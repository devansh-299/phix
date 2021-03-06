package com.thebiglosers.phix.server;


import com.thebiglosers.phix.model.User;
import java.util.List;
import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface UserApi {

    @GET("friends/{user_name}/")
    Single<List<User>> getUser(@Path("user_name") String user_name);

    @FormUrlEncoded
    @POST("/create_user/")
    Call<User> saveUser(@Field("get_full_name") String fullName,
                        @Field("image_string") String imageString,
                        @Field("email") String email,
                        @Field("upi_id") String upiString,
                        @Field("phone_number") String mobileNumber);

    @GET("profile/")
    Single<List<User>> searchFriendByParameter(@Query("keyword") String searchQuery);

}
