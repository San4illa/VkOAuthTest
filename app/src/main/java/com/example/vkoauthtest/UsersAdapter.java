package com.example.vkoauthtest;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersAdapterViewHolder> {

    private List<User> users;

    UsersAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UsersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_item, parent, false);
        return new UsersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapterViewHolder viewHolder, int i) {
        User user = users.get(i);

        String photoUrl = user.getPhotoUrl();
        if (!photoUrl.equals("")) {
            Picasso.get().load(user.getPhotoUrl()).into(viewHolder.photoImageView);
        }
        viewHolder.nameTextView.setText(user.getName());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class UsersAdapterViewHolder extends RecyclerView.ViewHolder {

        ImageView photoImageView;
        TextView nameTextView;

        UsersAdapterViewHolder(@NonNull View itemView) {
            super(itemView);

            photoImageView = itemView.findViewById(R.id.iv_user_photo);
            nameTextView = itemView.findViewById(R.id.tv_user_name);
        }
    }
}
