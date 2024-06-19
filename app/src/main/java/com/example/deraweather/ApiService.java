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

    // Private constructor to initialize the RequestQueue using Volley
    private ApiService(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    // Singleton instance creation method
    public static synchronized ApiService getInstance(Context context) {
        if (instance == null) {
            instance = new ApiService(context);
        }
        return instance;
    }

    // Listener interface for API responses
    public interface ApiResponseListener {
        void onResponse(JSONObject response);
        void onError(String errorMessage);
    }

    // Method to fetch weather data from OpenWeatherMap API
    public void getWeather(String country, String city, String appid, ApiResponseListener listener) {
        String url = String.format("https://api.openweathermap.org/data/2.5/weather?q=%s,%s&appid=%s", city, country, appid);

        // Create a JsonObjectRequest for fetching weather data
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                listener::onResponse, // Success listener
                error -> {
                    String errorMessage = parseVolleyError(error);
                    listener.onError(errorMessage);
                });

        // Add the request to the RequestQueue for execution
        requestQueue.add(jsonObjectRequest);
    }

    // Method to parse VolleyError and extract error message
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
