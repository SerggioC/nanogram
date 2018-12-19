package com.sergiocruz.nanogram.retrofit;

import android.util.Log;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;

public class CustomResponseInterceptor implements Interceptor {

    private static String newToken;
    private String bodyString;

    private final String TAG = getClass().getSimpleName();

    public okhttp3.Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        okhttp3.Response response = chain.proceed(request);
        if (response.code() != 200) {
            return makeTokenRefreshCall(request, chain);
        }
        Log.d(TAG, "INTERCEPTED: " + response.toString());
        return response;
    }

    private okhttp3.Response makeTokenRefreshCall(Request req, Interceptor.Chain chain) throws IOException {
        Log.d(TAG, "Retrying new request");
        /* fetch refreshed token, some synchronous API call, whatever */
        String newToken = fetchToken();
        /* make a new request which is same as the original one, except that its headers now contain a refreshed token */
        Request newRequest;
        newRequest = req.newBuilder().header("Authorization", " Token " + newToken).build();
        okhttp3.Response another = chain.proceed(newRequest);
        while (another.code() != 200) {
            makeTokenRefreshCall(newRequest, chain);
        }
        return another;
    }

    private String fetchToken() {
        return "";
    }
}