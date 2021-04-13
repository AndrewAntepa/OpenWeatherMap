package com.example.openweathermap;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.textclassifier.TextLinks;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button weatherButton;
    EditText cityText;
    TextView tempText, pressText, windText;
    OkHttpClient weatherClient;
    Request weatherRequest;
    Response weatherResponse;

    private final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    private final String API_KEY = "5dfe61a17f664cb83588e5564b0152e5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherButton   = findViewById(R.id.weatherButton);
        cityText        = findViewById(R.id.city);
        tempText        = findViewById(R.id.temper);
        pressText       = findViewById(R.id.press);
        windText        = findViewById(R.id.wind);

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityText.getText().toString();
                //запрос должен быть отправлен в параллельном потоке!!!
                //1 способ: класс AsyncTask и метод для отправки запроса execute()
                WeatherTask weatherTask = new WeatherTask();
//                if(weatherTask.getStatus() == AsyncTask.Status.FINISHED || weatherTask == null){
                    weatherTask.execute(city);
//                }
                //2 способ: использование метода отправки enqueue()
            }
        });
    }

    class WeatherTask extends AsyncTask<String, Void, Response>{
        @Override
        protected Response doInBackground(String... strings) {
            //подготовка запроса
            weatherClient = new OkHttpClient();
            //подготовка запроса с параметрами
            HttpUrl.Builder hub = HttpUrl.parse(WEATHER_URL).newBuilder();
            hub.addQueryParameter("q", strings[0]);
            hub.addQueryParameter("appid", API_KEY);
            hub.addQueryParameter("units", "metric");
            String url = hub.toString();
            weatherRequest = new Request.Builder().url(url).build();
            //отправка запроса
            try {
                weatherResponse = weatherClient.newCall(weatherRequest).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return weatherResponse;
        }

        @Override
        protected void onPostExecute(Response response) {
            super.onPostExecute(response);
            if(weatherResponse != null) {
//                String resp = response.headers().toString();
                String resp = null;
                try {
                    //resp = weatherResponse.body().string();
                    JSONObject mainJson = new JSONObject(response.body().string());
                    JSONObject main = mainJson.getJSONObject("main");
                    tempText.setText(Double.toString(main.getDouble("temp")));
                    pressText.setText(Integer.toString(main.getInt("pressure")));
                    JSONObject wind = mainJson.getJSONObject("wind");
                    windText.setText(wind.toString());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Ответ не получин", Toast.LENGTH_SHORT).show();
            }
        }
    }
}