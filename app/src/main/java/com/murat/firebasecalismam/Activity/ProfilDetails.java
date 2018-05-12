package com.murat.firebasecalismam.Activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.murat.firebasecalismam.Adapter.ProfileDetailsAdapter;
import com.murat.firebasecalismam.R;
import com.murat.firebasecalismam.Utils.Upload;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("ALL")
public class ProfilDetails extends AppCompatActivity {

    private TextView textView;
    private RecyclerView recyclerView;
    private Button resimSec;
    private String isim, soyisim;
    private ProgressDialog progressDialog;
    private List<Upload> uploadList;
    private ProfileDetailsAdapter adapter;
    private LinearLayoutManager mLayoutManager;

    private FirebaseAuth mAuth;

    private DatabaseReference databaseReferenceIsim;
    private FirebaseDatabase mFirebaseInstanceIsim;

    private DatabaseReference databaseReferenceSoyisim;
    private FirebaseDatabase mFirebaseInstanceSoyisim;

    public static DatabaseReference databaseReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_details);

        recyclerView = (RecyclerView) findViewById(R.id.ProfileDetailsRecyclerView);
        textView = (TextView) findViewById(R.id.profileDetailsIsimText);
        resimSec = (Button) findViewById(R.id.resimSecButton);
        isimSoyisim();

        resimSec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResimYukle.class);
                startActivity(intent);
            }
        });

    }


    private void isimSoyisim() {
        mAuth = FirebaseAuth.getInstance();

        mFirebaseInstanceIsim = FirebaseDatabase.getInstance();
        mFirebaseInstanceSoyisim = FirebaseDatabase.getInstance();
        databaseReferenceIsim = mFirebaseInstanceIsim.getReference("users").child(mAuth.getCurrentUser().getUid()).child("isim");
        databaseReferenceSoyisim = mFirebaseInstanceSoyisim.getReference("users").child(mAuth.getCurrentUser().getUid()).child("soyisim");


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
                        textView.setText("  Merhaba " + isim + " " + soyisim);
                        inlit();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Değer okunamadı
                        inlit();
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

    private void inlit() {
        uploadList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("images");
        recyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);

        recyclerView.setLayoutManager(mLayoutManager);

        progressDialog = new ProgressDialog(ProfilDetails.this);
        progressDialog.setMessage("Yükleniyor...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        uploadList.clear();

        Query queryRef = databaseReference.orderByChild("mUsersKey").equalTo(mAuth.getCurrentUser().getUid());
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Upload upload = postSnapshot.getValue(Upload.class);
                    uploadList.add(upload);
                }
                adapter = new ProfileDetailsAdapter(getApplicationContext(), uploadList);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }


    @Override
    protected void onRestart() {
        super.onRestart();

        // ilk önce geri dönüşümcü görünümünü temizleyin, böylece öğeler iki kez doldurulmaz
        for (int i = 0; i < adapter.getItemCount(); i++) {
            adapter.delete(i);
        }

        isimSoyisim();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profil_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_item:
                Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(intent);
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
