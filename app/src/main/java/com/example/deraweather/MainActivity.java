package com.example.deraweather;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
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

        // Enable edge-to-edge display
        EdgeToEdge.enable(this);

        // Set the layout for this activity
        setContentView(R.layout.activity_main);

        // Apply system bars insets to the main layout
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        result = findViewById(R.id.result);
        search = findViewById(R.id.search);

        // Set click listener for the search button
        search.setOnClickListener(v -> fetchWeatherData());
    }

    // Method to fetch weather data
    private void fetchWeatherData() {
        // Retrieve user input from EditText fields
        String countryInput = country.getText().toString().trim();
        String cityInput = city.getText().toString().trim();

        // Validate user input
        if (countryInput.isEmpty() || cityInput.isEmpty()) {
            result.setText("Please enter both country and city.");
            result.setTextColor(Color.RED);
            return;
        }

        // Call API service to get weather data
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

    // Method to handle API response and update UI
    private void handleWeatherResponse(JSONObject response) {
        try {
            // Extract main weather details
            JSONObject main = response.getJSONObject("main");
            JSONArray weatherArray = response.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0); // Get the first weather object from the array
            double temperature = main.getDouble("temp");
            int pressure = main.getInt("pressure");
            int humidity = main.getInt("humidity");
            String weatherDescription = weather.getString("main");

            // Format weather information string
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
