package com.dinc.messenger.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dinc.messenger.R;

import java.util.ArrayList;


public class AddFriendAdapter extends RecyclerView.Adapter<AddFriendAdapter.PostHolder> {

    private ArrayList<String> usernameList;
    private ArrayList<String> fullNameList;

    public AddFriendAdapter(ArrayList<String> usernameList,ArrayList<String> fullNameList){
        this.usernameList = usernameList;
        this.fullNameList = fullNameList;
    }

    @NonNull
    @Override
    public AddFriendAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.recycler_row_add,parent,false);
        return new PostHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull AddFriendAdapter.PostHolder holder, int position) {
        holder.username.setText(usernameList.get(position));
        holder.fullName.setText(fullNameList.get(position));
    }

    @Override
    public int getItemCount() {
        return usernameList.size();
    }


    public class PostHolder extends RecyclerView.ViewHolder{

        TextView username;
        TextView fullName;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.recyclerFullName);
            username = itemView.findViewById(R.id.recyclerUsername);

        }
    }
}

