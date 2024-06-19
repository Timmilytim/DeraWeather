package com.example.deraweather;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private EditText country, city;
    private TextView result;
    private Button search;
    private final String appId = "0f75210e7a185333f9a4850a5850539e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        result = findViewById(R.id.result);
        search = findViewById(R.id.search);

        search.setOnClickListener(v -> fetchWeatherData());
    }

    private void fetchWeatherData() {
        String countryInput = country.getText().toString().trim();
        String cityInput = city.getText().toString().trim();

        if (countryInput.isEmpty() || cityInput.isEmpty()) {
            result.setText("Please enter both country and city.");
            result.setTextColor(Color.RED);
            return;
        }

        ApiService.getInstance(this).getWeather(countryInput, cityInput, appId, new ApiService.ApiResponseListener() {
            @Override
            public void onResponse(JSONObject response) {
                handleWeatherResponse(response);
            }

            @Override
            public void onError(String errorMessage) {
                result.setText(errorMessage);
                result.setTextColor(Color.RED);
            }
        });
    }

    private void handleWeatherResponse(JSONObject response) {
        try {
            JSONObject main = response.getJSONObject("main");
            JSONArray weatherArray = response.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0); // Get the first weather object from the array
            double temperature = main.getDouble("temp");
            int pressure = main.getInt("pressure");
            int humidity = main.getInt("humidity");
            String weatherDescription = weather.getString("main");

            String weatherInfo = String.format("Temperature: %.2f°C\nPressure: %d hPa\nHumidity: %d%%\nWeather: %s", temperature, pressure, humidity, weatherDescription);
            result.setText(weatherInfo);
            result.setTextSize(20);
            result.setTextColor(Color.BLACK);
        } catch (Exception e) {
            e.printStackTrace();
            result.setText("Error parsing weather data.");
            result.setTextColor(Color.RED);
        }
    }
}
