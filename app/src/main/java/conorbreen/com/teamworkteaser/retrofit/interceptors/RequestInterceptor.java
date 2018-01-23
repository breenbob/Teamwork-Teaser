package conorbreen.com.teamworkteaser.retrofit.interceptors;

import android.util.Log;

import java.io.IOException;

import conorbreen.com.teamworkteaser.BuildConfig;
import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Conor Breen on 22/01/2018.
 */

public class RequestInterceptor  implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder builder = chain.request().newBuilder();

        Log.d("RequestInterceptor", "Adding default request headers to API request.");

        // Authorization header is in format username:password, where username is Teamwork API token and password is an arbitrary string of text
        String authCredential = Credentials.basic(BuildConfig.TeamworkApiToken, "xxx");

        builder.addHeader("Authorization", authCredential);
        builder.addHeader("Accept", "application/json");
        builder.addHeader("Content-Type", "application/json");

        return chain.proceed(builder.build());
    }
}
