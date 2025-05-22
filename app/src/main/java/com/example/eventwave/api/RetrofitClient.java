package com.example.eventwave.api;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    private static final String OPENAGENDA_BASE_URL = "https://openagenda.com/";
    private static Retrofit openAgendaRetrofit = null;

    public static Retrofit getOpenAgendaClient() {
        if (openAgendaRetrofit == null) {
            // Ajouter un intercepteur de logging pour déboguer les requêtes API
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            openAgendaRetrofit = new Retrofit.Builder()
                    .baseUrl(OPENAGENDA_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return openAgendaRetrofit;
    }

    public static OpenAgendaApi getOpenAgendaApi() {
        return getOpenAgendaClient().create(OpenAgendaApi.class);
    }
}