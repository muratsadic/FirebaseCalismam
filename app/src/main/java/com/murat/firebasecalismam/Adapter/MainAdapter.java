package com.murat.firebasecalismam.Adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.murat.firebasecalismam.Activity.ProfileActivity;
import com.murat.firebasecalismam.R;
import com.murat.firebasecalismam.Utils.Upload;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {
    private Context mContext;
    public static List<Upload> usersList;

    public MainAdapter(Context context, List<Upload> personList) {
        this.mContext = context;
        this.usersList = personList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_main_cardview, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Upload users = usersList.get(position);
        holder.isim.setText("  " + users.getIsim() + " " + users.getSoyisim());
        holder.ImageIsim.setText(users.getName());

        holder.profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ProfileActivity.class);
                v.getContext().startActivity(intent);
            }
        });


        holder.setOnClickLink(holder, position);

        try {
            Glide.with(mContext)
                    .load(users.getImageUrl())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.cardImage);
        } catch (Exception e) {
            e.printStackTrace();
        }


        try {
            Glide.with(mContext).load(users.getProfilLink())
                    .asBitmap()
                    .centerCrop()
                    .error(R.mipmap.ic_launcher_round)
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.profile_image);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void delete(int position) {
        usersList.remove(position);
        notifyItemRemoved(position);
    }

    public int getSize() {
        return usersList.size();
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView isim, ImageIsim;
        public ImageView cardImage;
        public CircleImageView profile_image;
        private View v;


        public MyViewHolder(View view) {
            super(view);
            v = itemView;
            isim = (TextView) view.findViewById(R.id.cardIsim);
            ImageIsim = (TextView) view.findViewById(R.id.cardImageIsim);
            cardImage = (ImageView) view.findViewById(R.id.cardImage);
            profile_image = (CircleImageView) view.findViewById(R.id.profile_image);

        }

        public void setOnClickLink(final MyViewHolder holder, final int position) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Click position " + position, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}


