package com.dinc.messenger.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.dinc.messenger.R;
import java.util.ArrayList;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.PostHolder> {

    private ArrayList<String> friendList;

    public FriendAdapter (ArrayList<String> friendList){
        this.friendList=friendList;
    }

    @NonNull
    @Override
    public FriendAdapter.PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.friend_list_row,parent,false);
        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendAdapter.PostHolder holder, int position) {
        holder.friendListUsername.setText(friendList.get(position));
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class PostHolder extends RecyclerView.ViewHolder{

        TextView friendListUsername;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            friendListUsername = itemView.findViewById(R.id.friendUsername);
        }
    }
}
