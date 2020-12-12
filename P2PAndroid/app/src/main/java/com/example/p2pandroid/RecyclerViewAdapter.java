package com.example.p2pandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {


    String[] sendersNames;
    String[] sendersMsg;
    String[] timeStamps;

    Context context;

    public RecyclerViewAdapter (Context ct, String[] names, String[] msgs, String[] times) {
        context = ct;
        sendersMsg = msgs;
        sendersNames = names;
        timeStamps = times;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_message, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.fromName.setText(sendersNames[position]);
        holder.message.setText(sendersMsg[position]);
        holder.timeStamp.setText(timeStamps[position]);

    }

    @Override
    public int getItemCount() {
        return sendersNames.length;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView fromName;
        TextView timeStamp;
        TextView message;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            fromName  = itemView.findViewById(R.id.nameOfSender);
            timeStamp = itemView.findViewById(R.id.timeStamp);
            message   = itemView.findViewById(R.id.messageText);
        }
    }
}
