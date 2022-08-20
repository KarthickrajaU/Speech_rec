package com.example.speech;

import static com.example.speech.R.drawable.ic_baseline_hearing_24;
import static com.example.speech.R.drawable.ic_mic_icon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class main_recycler extends RecyclerView.Adapter<main_recycler.ViewHolder> {
    private List<String> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    int x = 0;

    // data is passed into the constructor
    main_recycler(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = mInflater.inflate(R.layout.main_option_layout, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String option = mData.get(position);
        holder.myTextView.setText(option);
        holder.imageButton.setBackgroundResource(R.color.trans_red);

        switch (x) {
            case 0:
                holder.imageButton.setImageResource(ic_mic_icon);

                break;
            case 1:
                holder.imageButton.setImageResource(ic_baseline_hearing_24);
                break;
            case 2:
                break;
            default:
                break;
        }
        x++;
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;
        ImageButton imageButton;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.textView);
            imageButton = itemView.findViewById(R.id.imageButton);


            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
