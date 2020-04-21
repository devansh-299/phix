package com.thebiglosers.phix.viewmodel;


import android.app.Application;
import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.User;
import com.thebiglosers.phix.server.ApiClient;
import com.thebiglosers.phix.server.UserApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginViewModel extends AndroidViewModel {

    public MutableLiveData<Boolean> loginSuccessful = new MutableLiveData<>();
    private UserApi userApi;

    public LoginViewModel(@NonNull Application application) {
        super(application);
        userApi = ApiClient.getClient().create(UserApi.class);
    }

    public void loginUser(Context context, User user1) {

        Call<User> call = userApi.saveUser(user1.getFullName(),
                user1.getImageString(),
                user1.getEmail(),
                user1.getUpiString(),
                user1.getMobileNumber());

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(retrofit2.Call<User> call, Response<User> response) {

                int statusCode = response.code();

                Log.e(context.getString(R.string.pass), context.getString(R.string.status_code)
                        + statusCode);
                try {

                    /**
                    this log also checks if response is null or not
                     */

                    Log.e(context.getString(R.string.pass), "Body"
                            + response.body().toString());

                    loginSuccessful.setValue(true);
                } catch (Exception e) {
                    Log.e("LOG PASS", "Empty Response");
                    loginSuccessful.setValue(false);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<User> call, Throwable t) {
                Log.e(context.getString(R.string.fail), context.getString(R.string.error)
                        + t.toString());
                loginSuccessful.setValue(false);
            }
        });
    }

}
