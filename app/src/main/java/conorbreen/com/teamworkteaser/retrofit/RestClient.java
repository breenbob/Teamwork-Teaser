package conorbreen.com.teamworkteaser.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import conorbreen.com.teamworkteaser.BuildConfig;
import conorbreen.com.teamworkteaser.retrofit.converterFactories.EnumConverterFactory;
import conorbreen.com.teamworkteaser.retrofit.interceptors.RequestInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Conor Breen on 22/01/2018.
 */

/**
 * Wrapper class for Teamwork API singleton instance
 */
public class RestClient {
    private static RestClient instance = null;
    private ITeamworkApi service;

    private RestClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient()
                .newBuilder()
                // Add request interceptor to auto-inject Authorization/Content headers into every request
                .addInterceptor(new RequestInterceptor())
                // For debugging purposes
                .addInterceptor(loggingInterceptor)
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        OkHttpClient client = builder.build();

        // Use Gson builder to tell gson to use ISO 8601 date format when parsing date properties
        Gson gson =  new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(BuildConfig.TeamworkApiBaseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(new EnumConverterFactory())
                .build();

        service = retrofit.create(ITeamworkApi.class);
    }

    public static RestClient getInstance() {
        if (instance == null) {
            instance = new RestClient();
        }

        return instance;
    }

    public ITeamworkApi getApiService() {
        return service;
    }
}
