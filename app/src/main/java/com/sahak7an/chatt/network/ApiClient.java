package com.sahak7an.chatt.network;

import static com.sahak7an.chatt.utilities.Constants.URL;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();

        }

        return retrofit;

    }

}
