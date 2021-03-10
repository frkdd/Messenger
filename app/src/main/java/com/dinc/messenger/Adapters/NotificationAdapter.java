package com.dinc.messenger.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinc.messenger.R;

import java.util.ArrayList;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.PostHolder> {

    private ArrayList<String> requestSenderUsernameList;

    public NotificationAdapter(ArrayList<String> requestSenderUsernameList){
        this.requestSenderUsernameList = requestSenderUsernameList;
    }

    @NonNull
    @Override
    public NotificationAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row_request,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.PostHolder holder, int position) {
        holder.requesterUsername.setText(requestSenderUsernameList.get(position));

    }

    @Override
    public int getItemCount() {
        return requestSenderUsernameList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder{

        TextView requesterUsername;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            requesterUsername = itemView.findViewById(R.id.requestUsername);
        }
    }
}
