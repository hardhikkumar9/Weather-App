package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout homeRl;
    private ProgressBar loadingPB;
    private TextView cityNameTv, temperatureTV, conditionTV;
    private TextInputEditText cityEdt;
    private ImageView backIV, iconIV, searchIV;
    private RecyclerView weatherRV;
    private ArrayList<WeatherRVModal2> weatherRVModalrArrayList2;
    private WeatherRVAdapter2 weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String CityName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_main);
        homeRl = findViewById(R.id.idRLHome);
        loadingPB = findViewById(R.id.PBLoading);
        cityNameTv = findViewById(R.id.idTVCityName);
        temperatureTV = findViewById(R.id.idTVTemperature);
        conditionTV = findViewById(R.id.idTVCondition);
        weatherRV = findViewById(R.id.idRVWeather);
        backIV = findViewById(R.id.idVBack);
        iconIV = findViewById(R.id.idIVIcon);
        searchIV = findViewById(R.id.idIVSearch);
        cityEdt = findViewById(R.id.idEdtCity);
        weatherRVModalrArrayList2 = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter2(this, weatherRVModalrArrayList2);
        weatherRV.setAdapter(weatherRVAdapter);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
         CityName = getCityName(location.getLatitude(), location.getLongitude());
        getWeatherInfo(CityName);

        searchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityEdt.getText().toString();
                if (city.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please enter city name ", Toast.LENGTH_SHORT).show();
                }else {
                    cityNameTv.setText(CityName);
                    getWeatherInfo(city);
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==PERMISSION_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(this, "Please provide permissions..", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private String getCityName(double latitude, double longitude){
        String CityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try {
            List<Address>addresses = gcd.getFromLocation(latitude,longitude,10);

            for (Address adr : addresses){
                if (adr!= null){
                    String city = adr.getLocality();
                    if (city!=null && !city.equals("")){
                        CityName = city;
                    }else{
                        Log.d("TAG","CITY NOT FOUND");
                        Toast.makeText(this,"User City Not Found..", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return CityName;
    }

    private void getWeatherInfo(String CityName) {
        String url = "http://api.weatherapi.com/v1/forecast.json?key=0fda4df3e427425ebf0125904220607&q=" + CityName + "&days=1&aqi=yes&alerts=yes";
        cityNameTv.setText(CityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                loadingPB.setVisibility(View.GONE);
                homeRl.setVisibility(View.VISIBLE);
                weatherRVModalrArrayList2.clear();

                try {
                    String temperature = response.getJSONObject("current").getString("temp_c");
                    temperatureTV.setText(temperature+"°C");
                    int isDay =response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionIcon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("http:".concat(conditionIcon)).into(iconIV);
                    conditionTV.setText(condition);
                    if (isDay==1){
                        //morning
                        Picasso.get().load("file:///Users/hardhikkumar/Downloads/anton-repponen-3MNzGlQM7qs-unsplash.jpg").into(backIV);
                    }else {
                        //night
                        Picasso.get().load("file:///Users/hardhikkumar/Downloads/timothee-duran-ilfsT5p_qvA-unsplash.jpg").into(backIV);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecast0 = forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecast0.getJSONArray("hour");

                    for (int i=0; i<=hourArray.length(); i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temp = hourObj.getString("temp_c");
                        String img = hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");

                        weatherRVModalrArrayList2.add(new WeatherRVModal2(time,temp,img,wind));
                    }

                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Please enter valid city name..", Toast.LENGTH_SHORT).show();
            }
        });
        requestQueue.add(jsonObjectRequest);
    }
}