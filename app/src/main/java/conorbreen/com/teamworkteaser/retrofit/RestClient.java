package conorbreen.com.teamworkteaser.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
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
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(0, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        if (BuildConfig.DEBUG) {
            // For debugging purposes
            builder.addInterceptor(loggingInterceptor);
        }

        OkHttpClient client = builder.build();

        // Had to use custom type adapter to check for nulls instead of following simpler code
        // as fetching projects from API with status=ALL brought back some projects with empty dates
        // which caused Json parsing exceptions
        /*Gson gson =  new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();*/

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            // Use Gson builder to tell gson to use ISO 8601 date format when parsing date properties
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK);
            @Override
            public Date deserialize(final JsonElement json, final Type typeOfT, final JsonDeserializationContext context)
                    throws JsonParseException {
                try {
                    return df.parse(json.getAsString());
                } catch (ParseException e) {
                    return null;
                }
            }
        });

        Gson gson = gsonBuilder.create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.TeamworkApiBaseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addConverterFactory(new EnumConverterFactory())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
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
