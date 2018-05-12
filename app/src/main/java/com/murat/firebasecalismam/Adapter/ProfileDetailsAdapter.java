package com.murat.firebasecalismam.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.murat.firebasecalismam.R;
import com.murat.firebasecalismam.Utils.Upload;

import java.util.List;

public class ProfileDetailsAdapter extends RecyclerView.Adapter<ProfileDetailsAdapter.MyViewHolder> {

    private Context mContext;
    public static List<Upload> usersList;

    private FirebaseStorage mStorage;
    private DatabaseReference mDatabaseRef;

    public ProfileDetailsAdapter(Context context, List<Upload> personList) {

        this.mContext = context;
        this.usersList = personList;
    }

    public ProfileDetailsAdapter(List<Upload> personList) {
        this.usersList = personList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.profil_cardview, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final Upload users = usersList.get(position);

        holder.isim.setText("  " + users.getIsim() + " " + users.getSoyisim());
        holder.ImageIsim.setText(users.getName());
        holder.setOnClickLink(holder, position);


        holder.imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(mContext, holder.imageButton);
                popup.inflate(R.menu.options_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menu1:
                                Upload selectedItem = usersList.get(position);

                                mStorage = FirebaseStorage.getInstance();
                                mDatabaseRef = FirebaseDatabase.getInstance().getReference("images");

                                final String selectedKey = selectedItem.getKey();
                                final String selectedImageKey = selectedItem.getImageKey();

                                StorageReference imageRef = mStorage.getReferenceFromUrl(selectedItem.getImageUrl());
                                imageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mDatabaseRef.child(selectedImageKey).removeValue();
                                        Toast.makeText(mContext, "Resim Silindi", Toast.LENGTH_SHORT).show();
                                    }
                                });
                                usersList.clear();

                                break;
                            case R.id.menu2:
                                //istediğinizi yazınız
                                break;
                            case R.id.menu3:
                                //istediğinizi yazınız
                                break;
                        }
                        return false;
                    }
                });
                popup.show();

            }
        });


        try {
            Glide.with(mContext)
                    .load(users.getImageUrl())

                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.cardImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void delete(int position) {
        usersList.remove(position);
        notifyItemRemoved(position);
    }


    @Override
    public int getItemCount() {
        return usersList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView isim, ImageIsim;
        public ImageView cardImage;
        public ImageButton imageButton;
        private View v;


        public MyViewHolder(View view) {
            super(view);
            v = itemView;
            isim = (TextView) view.findViewById(R.id.profilCardIsim);
            ImageIsim = (TextView) view.findViewById(R.id.profilCardImageIsim);
            cardImage = (ImageView) view.findViewById(R.id.profilCardImage);
            imageButton = (ImageButton) view.findViewById(R.id.profilImageButton);


        }

        public void setOnClickLink(final MyViewHolder holder, final int position) {
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(mContext, "Click position " + position, Toast.LENGTH_SHORT).show();
                }
            });
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Upload selectedItem = usersList.get(position);
                    final String selectedKey = selectedItem.getImageKey();
                    DatabaseReference dltImages = FirebaseDatabase.getInstance().getReference("images").child(selectedKey);
                    dltImages.removeValue();
                    usersList.remove(position);

                    Toast.makeText(mContext, "Item deleted", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        }
    }

}


