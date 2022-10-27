package com.example.onlinemcqexam;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.onlinemcqexam.model.Response;

import java.util.ArrayList;

public class MainActAdapter extends RecyclerView.Adapter<MainActAdapter.Viewholder> {

    Context context;
   // String[] categoryName;
    ArrayList<Response> responses;

    /*public MainActAdapter(Context context, String[] categoryName) {
        this.context = context;
        this.categoryName = categoryName;
    }*/

    public MainActAdapter(Context context, ArrayList<Response> responses) {
        this.context = context;
        this.responses = responses;
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.cardMainActRCV);
            textView = itemView.findViewById(R.id.tvCategoryNameRCV);

        }
    }


    @NonNull
    @Override
    public MainActAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.custom_rcv_main_act, parent, false);
        Viewholder viewholder = new Viewholder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(@NonNull MainActAdapter.Viewholder holder, int position) {

      //  holder.textView.setText(categoryName[position]);

        Response item=responses.get(position);
        holder.textView.setText(item.getCategory());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, QuestionActivity.class);
                String categoryName= item.getCategory();
                intent.putExtra("categoryName",categoryName);
                context.startActivity(intent);

            }
        });

    }

    @Override
    public int getItemCount() {
        return responses.size();
    }
}
