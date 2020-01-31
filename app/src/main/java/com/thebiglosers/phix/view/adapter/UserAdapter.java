package com.thebiglosers.phix.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.Transaction;
import com.thebiglosers.phix.model.User;

import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private List<User> mUser;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.tv_friend_name);
            imageView = (ImageView) view.findViewById(R.id.iv_friend_image);
        }
    }


    public UserAdapter(List<User> user) {
        this.mUser = user;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        User user = mUser.get(position);
        holder.name.setText(user.getFullName());
        //holder.imageView.setText(transaction.getDate());
    }

    public void updateImageList(List<User> newList) {
        mUser.clear();
        mUser.addAll(newList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }
}