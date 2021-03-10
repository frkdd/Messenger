package com.dinc.messenger.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dinc.messenger.R;
import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.PostHolder> {

    private ArrayList<String> messages;
    private ArrayList<String> timestamps;

    public MessageAdapter(ArrayList<String> messages,ArrayList<String> timestamps){
        this.messages=messages;
        this.timestamps=timestamps;
    }

    @NonNull
    @Override
    public MessageAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.user_message_layout,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.PostHolder holder, int position) {
        holder.messageText.setText(messages.get(position));
        holder.timestampText.setText(timestamps.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class PostHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        TextView timestampText;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
        }
    }
}
