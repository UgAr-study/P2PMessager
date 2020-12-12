package com.example.p2pandroid;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    ArrayList<MessageItem> messageItems;
    Context context;

    public RecyclerViewAdapter (Context ct, ArrayList<MessageItem> items) {
        context = ct;
        messageItems = items;
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

        String name = messageItems.get(position).getName();
        SpannableString ss=new SpannableString(name);
        ss.setSpan(new UnderlineSpan(), 0, name.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        holder.fromName.setText(ss);
        holder.message.setText(messageItems.get(position).getMessage());
        holder.timeStamp.setText(messageItems.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return messageItems.size();
    }

    public void addItem (MessageItem item) {
        messageItems.add(item);
        notifyItemChanged(getItemCount());
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

class MessageItem {
    private String name;
    private String time;
    private String message;

    public MessageItem(String na, String msg, String t) {
        name = na;
        message = msg;
        time = t;
    }

    public String getName() {
        return name;
    }

    public String getMessage() {
        return message;
    }

    public String getTime() {
        return time;
    }

}
