package com.murat.firebasecalismam.Activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.murat.firebasecalismam.R;
import com.murat.firebasecalismam.Utils.Upload;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

@SuppressWarnings("ALL")
public class ProfileActivity extends AppCompatActivity {

    private Button buttonSaveImage;
    private Button buttonSelectImage;
    private CircleImageView imageView;
    private ProgressDialog progressDialog;
    private static final int PICK_IMAGE_REQUEST = 123;
    private Uri filePath;
    private TextView textView;
    private String isim, soyisim;

    private FirebaseAuth mAuth;
    private FirebaseStorage fStorage;
    private DatabaseReference databaseReference;

    private DatabaseReference databaseReferenceIsim;
    private FirebaseDatabase mFirebaseInstanceIsim;

    private DatabaseReference databaseReferenceSoyisim;
    private FirebaseDatabase mFirebaseInstanceSoyisim;


    private String profilLink;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        fStorage = FirebaseStorage.getInstance();
        isimSoyisim();

        buttonSaveImage = (Button) findViewById(R.id.buttonSaveImage);
        buttonSelectImage = (Button) findViewById(R.id.buttonSelectImage);
        imageView = (CircleImageView) findViewById(R.id.imageViewProfile);

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Yükleniyor...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StorageReference storageRef = fStorage.getReference()
                .child("UserProfil")
                .child(mAuth.getCurrentUser().getUid())
                .child("UserProfil");

        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                progressDialog.dismiss();
                //  Picasso.with(ProfileActivity.this).load(uri).into(imageView);
                try {

                    Glide.with(ProfileActivity.this).load(uri)
                            .override(800, 800)
                            .centerCrop()
                            .into(imageView);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                //Picasso.with(ProfileActivity.this).load(uri).fit().centerCrop().into(imageView);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this, "Profil resminiz yok, Profil resminizi yükleyin", Toast.LENGTH_SHORT).show();

            }
        });


        buttonSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                // intent.setType("profilResmi/*");
                // intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Resim Seçiniz"), PICK_IMAGE_REQUEST);

            }
        });


        buttonSaveImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (filePath != null) {


                    progressDialog = new ProgressDialog(ProfileActivity.this);
                    progressDialog.setMessage("Yükleniyor...");
                    progressDialog.setCancelable(false);
                    progressDialog.show();


                    StorageReference storageRef = fStorage.getReference()
                            .child("UserProfil")
                            .child(mAuth.getCurrentUser().getUid())
                            .child("UserProfil");
                    storageRef.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            progressDialog.dismiss();
                            final Uri downloadUri = taskSnapshot.getDownloadUrl();
                            imageView.setImageBitmap(null);

                            DatabaseReference DatabaseRefe = FirebaseDatabase.getInstance().getReference().child("users");
                            databaseReference = DatabaseRefe.child(mAuth.getCurrentUser().getUid());
                            databaseReference.child("profilResmi").setValue(downloadUri.toString().trim());


                            DatabaseReference buyRef = FirebaseDatabase.getInstance().getReference().child("images");
                            Query queryRef = buyRef.orderByChild("mUsersKey").equalTo(mAuth.getCurrentUser().getUid());
                            queryRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                                        Upload upload = postSnapshot.getValue(Upload.class);
                                        profilLink = downloadUri.toString().trim();
                                        postSnapshot.getRef().child("profilLink").setValue(profilLink);

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Toast.makeText(ProfileActivity.this, "Fotoğraf başarılı bir şekilde kaydedildi.", Toast.LENGTH_SHORT).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Yükleniyor " + (int) progress + "%");
                        }
                    });

                }
            }
        });

    }


    private void isimSoyisim() {
        mAuth = FirebaseAuth.getInstance();

        mFirebaseInstanceIsim = FirebaseDatabase.getInstance();
        mFirebaseInstanceSoyisim = FirebaseDatabase.getInstance();
        databaseReferenceIsim = mFirebaseInstanceIsim.getReference("users").child(mAuth.getCurrentUser().getUid()).child("isim");
        databaseReferenceSoyisim = mFirebaseInstanceSoyisim.getReference("users").child(mAuth.getCurrentUser().getUid()).child("soyisim");


        textView = (TextView) findViewById(R.id.profileIsimText);


        mFirebaseInstanceIsim = FirebaseDatabase.getInstance();
        databaseReferenceIsim = mFirebaseInstanceIsim.getReference("users").child(mAuth.getCurrentUser().getUid()).child("isim");
        databaseReferenceIsim.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                isim = dataSnapshot.getValue(String.class);

                mFirebaseInstanceSoyisim = FirebaseDatabase.getInstance();
                databaseReferenceSoyisim = mFirebaseInstanceSoyisim.getReference("users")
                        .child(mAuth.getCurrentUser().getUid())
                        .child("soyisim");

                databaseReferenceSoyisim.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        soyisim = dataSnapshot.getValue(String.class);

                        textView.setText("Merhaba " + isim + " " + soyisim);


                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Değer okunamadı
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Değer okunamadı
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
                //  Picasso.with(ProfileActivity.this).load(filePath).fit().centerCrop().into(imageView);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}
