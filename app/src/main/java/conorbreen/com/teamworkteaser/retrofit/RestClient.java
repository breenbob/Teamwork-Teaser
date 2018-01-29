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
import conorbreen.com.teamworkteaser.gson.MultiFormatDateDeserializer;
import conorbreen.com.teamworkteaser.gson.NullableIntTypeAdapter;
import conorbreen.com.teamworkteaser.gson.ProjectDeserializationStrategy;
import conorbreen.com.teamworkteaser.gson.ProjectSerializer;
import conorbreen.com.teamworkteaser.models.Project;
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

        // Had to use custom type adapters to allow for multiple date formats, and check for nulls
        // as project start/end used full ISO 8601 date format, whereas project created/updated used short yyyyMMdd format.
        // Also, fetching projects from API with status=ALL brought back some projects with empty dates
        // which caused Json parsing exceptions

        /*Gson gson =  new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create();*/

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new MultiFormatDateDeserializer())
                .registerTypeAdapter(Number.class, new NullableIntTypeAdapter())
                .registerTypeAdapter(int.class, new NullableIntTypeAdapter())
                .registerTypeAdapter(Integer.class, new NullableIntTypeAdapter())
                .registerTypeAdapter(double.class, new NullableIntTypeAdapter())
                .registerTypeAdapter(Double.class, new NullableIntTypeAdapter())
                .registerTypeAdapter(long.class, new NullableIntTypeAdapter())
                .registerTypeAdapter(Long.class, new NullableIntTypeAdapter())
                .registerTypeAdapter(Project.class, new ProjectSerializer())
                // Important as some fields only exist in posted data, and int fields that don't exist or have empty values
                // in get models will throw number format exceptions
                .addDeserializationExclusionStrategy(new ProjectDeserializationStrategy())
                .create();

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
