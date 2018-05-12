package com.murat.firebasecalismam.Activity;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.murat.firebasecalismam.R;
import com.murat.firebasecalismam.Utils.Upload;

import java.io.IOException;

@SuppressWarnings("ALL")
public class ResimYukle extends AppCompatActivity {

    private Button btnChoose, btnUpload;
    private ImageView imageView;
    private Uri filePath;
    private final int PICK_IMAGE_REQUEST = 71;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DatabaseReference mDatabaseRef;

    private StorageTask mUploadTask;
    private EditText mEditTextFileName;
    private FirebaseAuth mAuth;
    private Uri mImageUri;

    private DatabaseReference databaseReferenceIsim;
    private FirebaseDatabase mFirebaseInstanceIsim;

    private DatabaseReference databaseReferenceSoyisim;
    private FirebaseDatabase mFirebaseInstanceSoyisim;

    private DatabaseReference databaseReferenceProfilLink;
    private FirebaseDatabase mFirebaseInstanceProfilLink;


    private String isim, soyisim, profilLink, tamAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resim_yukle);
        //arayüz elemanları tanımlandı
        btnChoose = (Button) findViewById(R.id.btnChoose);
        btnUpload = (Button) findViewById(R.id.btnUpload);
        mEditTextFileName = (EditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.imgView);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        mAuth = FirebaseAuth.getInstance();

        //Firebase Storage'ye Erişimim
        storageReference = FirebaseStorage.getInstance().getReference("images");
        //Firebase DataBase Realtime'ye Erişimim
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("images");


        //Firebase Databaseden kullanıcı ismini çeken metod
        mFirebaseInstanceIsim = FirebaseDatabase.getInstance();
        databaseReferenceIsim = mFirebaseInstanceIsim.getReference("users").child(mAuth.getCurrentUser().getUid()).child("isim");
        databaseReferenceIsim.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                isim = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        //Firebase Databaseden kullanıcı soyismini çeken metod
        mFirebaseInstanceSoyisim = FirebaseDatabase.getInstance();
        databaseReferenceSoyisim = mFirebaseInstanceSoyisim.getReference("users").child(mAuth.getCurrentUser().getUid()).child("soyisim");
        databaseReferenceSoyisim.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                soyisim = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //Firebase Databaseden kullanıcı profil linkini çeken metod
        mFirebaseInstanceProfilLink = FirebaseDatabase.getInstance();
        databaseReferenceProfilLink = mFirebaseInstanceProfilLink.getReference("users").child(mAuth.getCurrentUser().getUid()).child("profilResmi");
        databaseReferenceProfilLink.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                profilLink = dataSnapshot.getValue(String.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Değer okunamadı
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        btnChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mUploadTask != null && mUploadTask.isInProgress()) {
                    Toast.makeText(ResimYukle.this, "Yükleme devam ediyor", Toast.LENGTH_SHORT).show();
                } else {
                    uploadImage();
                }
            }
        });


    }

    //Telefondan resim seçmeyi sağlayan metod
    private void chooseImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(Intent.createChooser(intent, "Resim Seçiniz"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                bitmap.describeContents();
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImage() {


        if (mImageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("yükleniyor...");
            progressDialog.show();

            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));




            mUploadTask = fileReference.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();

                            Toast.makeText(ResimYukle.this, "Başarıyla Yüklendi", Toast.LENGTH_LONG).show();

                            //Database'de görünmesini istediğim images çıktıları
                            String uploadId = mDatabaseRef.push().getKey();
                            tamAd = isim + " " + soyisim;

                            Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                                    taskSnapshot.getDownloadUrl().toString()
                                    , mAuth.getCurrentUser().getUid(), isim, soyisim, profilLink, uploadId, tamAd);

                            mDatabaseRef.child(uploadId).setValue(upload);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ResimYukle.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Yükleniyor " + (int) progress + "%");
                        }
                    });
        } else {
            Toast.makeText(this, "Dosya seçilmedi", Toast.LENGTH_SHORT).show();

        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
    }
}

