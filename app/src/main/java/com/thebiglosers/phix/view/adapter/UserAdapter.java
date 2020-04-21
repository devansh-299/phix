package com.thebiglosers.phix.view.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.thebiglosers.phix.R;
import com.thebiglosers.phix.model.User;
import java.util.List;
import androidx.recyclerview.widget.RecyclerView;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {

    private List<User> mUser;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public ImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.tv_friend_name);
            imageView = view.findViewById(R.id.iv_friend_image);
        }
    }


    public UserAdapter(List<User> user,Context context) {
        this.mUser = user;
        this.context = context;
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

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .circleCrop()
                .placeholder(R.drawable.splash_icon)
                .error(R.drawable.splash_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.IMMEDIATE);

        Glide.with(context).load(user.getImageString())
                .apply(options)
                .into(holder.imageView);
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