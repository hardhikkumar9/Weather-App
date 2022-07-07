package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter2 extends RecyclerView.Adapter<WeatherRVAdapter2.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModal2> weatherRVModal2ArrayList;

    public WeatherRVAdapter2(Context context, ArrayList<WeatherRVModal2> weatherRVModal2ArrayList) {
        this.context = context;
        this.weatherRVModal2ArrayList = weatherRVModal2ArrayList;
    }

    @NonNull
    @Override
    public WeatherRVAdapter2.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherRVAdapter2.ViewHolder holder, int position) {

        WeatherRVModal2 modal = weatherRVModal2ArrayList.get(position);
        holder.temperatureTV.setText(modal.getTemperature() + "°C");
        Picasso.get().load("http:".concat(modal.getIcon())).into(holder.conditionTV);
        holder.windTV.setText(modal.getWindSpeed() + "Km/h");
        SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd hh:mm");
        SimpleDateFormat output = new SimpleDateFormat("hh:mm aa");
        try {
            Date t = input.parse(modal.getTime());
            holder.timeTV.setText(output.format(t));
        } catch (ParseException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return weatherRVModal2ArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView windTV, temperatureTV, timeTV;
        private ImageView conditionTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            windTV = itemView.findViewById(R.id.idTVWindSpeed);
            temperatureTV = itemView.findViewById(R.id.idTVTemperature);
            timeTV = itemView.findViewById(R.id.idTVTime);
            conditionTV = itemView.findViewById(R.id.idIVCondition);
        }
    }
}
