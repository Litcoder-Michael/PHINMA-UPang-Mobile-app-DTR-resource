package com.example.capstoneprojectfinal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyHolder> {

    Context context;
    ArrayList<Model> models;
    public MyAdapter(Context context, ArrayList<Model> models) {
        this.context = context;
        this.models = models;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_dtr_container, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        holder.tv_date.setText(models.get(position).getDate());
        holder.tv_timein.setText(models.get(position).getTimein());
        holder.tv_timeout.setText(models.get(position).getTimeout());
        holder.tv_totalHrs.setText(models.get(position).getTotalHrs());
    }

    @Override
    public int getItemCount() {
        return models.size();
    }

}
class MyHolder extends RecyclerView.ViewHolder {
    TextView tv_date, tv_timein, tv_timeout,tv_totalHrs;

    public MyHolder(@NonNull View itemView) {
        super(itemView);
        this.tv_date = itemView.findViewById(R.id.text_date);
        this.tv_timein = itemView.findViewById(R.id.text_timein);
        this.tv_timeout = itemView.findViewById(R.id.text_timeout);
        this.tv_totalHrs =itemView.findViewById(R.id.text_totalhrs);
    }
}
