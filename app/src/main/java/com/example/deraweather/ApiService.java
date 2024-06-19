package com.example.deraweather;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class ApiService {

    private static ApiService instance;
    private RequestQueue requestQueue;

    private ApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized ApiService getInstance(Context context) {
        if (instance == null) {
            instance = new ApiService(context);
        }
        return instance;
    }

    public interface ApiResponseListener {
        void onResponse(JSONObject response);
        void onError(String errorMessage);
    }

    public void getWeather(String country, String city, String appid, ApiResponseListener listener) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s,%s&appid=%s", city, country, appid);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                listener::onResponse,
                error -> {
                    String errorMessage = parseVolleyError(error);
                    listener.onError(errorMessage);
                });

        requestQueue.add(jsonObjectRequest);
    }

    private String parseVolleyError(VolleyError error) {
        String errorMessage = "An error occurred";
        if (error.networkResponse != null && error.networkResponse.data != null) {
            try {
                String body = new String(error.networkResponse.data, "UTF-8");
                JSONObject errorJson = new JSONObject(body);
                errorMessage = errorJson.optString("message");
            } catch (UnsupportedEncodingException | JSONException e) {
                e.printStackTrace();
            }
        } else if (error instanceof com.android.volley.NoConnectionError) {
            errorMessage = "No internet connection. Please check your connection and try again.";
        }
        return errorMessage;
    }
}
