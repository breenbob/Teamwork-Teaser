package conorbreen.com.teamworkteaser.retrofit.interceptors;

import android.util.Log;

import java.io.IOException;

import conorbreen.com.teamworkteaser.BuildConfig;
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

        builder.addHeader("Accept", "application/json");
        builder.addHeader("Content-Type", "application/json");
        builder.addHeader("Authorization", BuildConfig.TeamworkApiToken);

        return chain.proceed(builder.build());
    }
}
