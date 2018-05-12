package com.murat.firebasecalismam.Activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.murat.firebasecalismam.Adapter.MainAdapter;
import com.murat.firebasecalismam.R;
import com.murat.firebasecalismam.Utils.Upload;

import java.util.ArrayList;
import java.util.List;
@SuppressLint("StaticFieldLeak")
public class MainActivity extends AppCompatActivity {


    private TextView textView;
    public static List<Upload> personList;

    public static MainAdapter adapter;
    public static RecyclerView recyclerView;
    private ProgressDialog progressDialog;
    private LinearLayoutManager mLayoutManager;

    private DatabaseReference databaseReferenceIsim;
    private FirebaseDatabase mFirebaseInstanceIsim;

    private DatabaseReference databaseReferenceSoyisim;
    private FirebaseDatabase mFirebaseInstanceSoyisim;

    private FirebaseAuth auth;
    public static DatabaseReference databaseReference;

    private String isim, soyisim;


    private FirebaseAuth.AuthStateListener authStateListener
            = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            //Kullanıcı getCurrentUser yönteminde oturum açmadıysa, null değerini döndürür
            if (user != null) {
                // Kullanıcı oturum açtı
            } else {
                // Kullanıcı çıkış yaptı
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }

        }
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isimSoyisim();
        Data();


    }


    private void Data() {
        personList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("images");
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(mLayoutManager);
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Yükleniyor...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        personList.clear();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Upload upload = postSnapshot.getValue(Upload.class);
                    personList.add(upload);
                }
                adapter = new MainAdapter(getApplicationContext(), personList);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void isimSoyisim() {
        auth = FirebaseAuth.getInstance();


        mFirebaseInstanceIsim = FirebaseDatabase.getInstance();
        mFirebaseInstanceSoyisim = FirebaseDatabase.getInstance();
        databaseReferenceIsim = mFirebaseInstanceIsim.getReference("users").child(auth.getCurrentUser().getUid()).child("isim");
        databaseReferenceSoyisim = mFirebaseInstanceSoyisim.getReference("users").child(auth.getCurrentUser().getUid()).child("soyisim");


        textView = (TextView) findViewById(R.id.text);


        mFirebaseInstanceIsim = FirebaseDatabase.getInstance();
        databaseReferenceIsim = mFirebaseInstanceIsim.getReference("users").child(auth.getCurrentUser().getUid()).child("isim");
        databaseReferenceIsim.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                isim = dataSnapshot.getValue(String.class);

                mFirebaseInstanceSoyisim = FirebaseDatabase.getInstance();
                databaseReferenceSoyisim = mFirebaseInstanceSoyisim.getReference("users")
                        .child(auth.getCurrentUser().getUid())
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
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }


    @Override
    protected void onRestart() {
        super.onRestart();

        //ilk önce geri dönüşümcü görünümünü temizleyin, böylece öğeler iki kez doldurulmaz

        for (int i = 0; i < adapter.getSize(); i++) {
            adapter.delete(i);
        }

        Data();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_details_item:
                Intent intent = new Intent(getApplicationContext(), ProfilDetails.class);
                startActivity(intent);
                return true;
            case R.id.kullaniciSil:
         /*
                if (user != null) {
                    user.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    //Silme işlemi başarılı oldugunda kullanıcıya bir mesaj gösterilip UyeOlActivity e geçiliyor.
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Hesabın silindi.Yeni bir hesap oluştur!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, SignupActivity.class));
                                        finish();

                                    } else {
                                        //İşlem başarısız olursa kullanıcı bilgilendiriliyor.
                                        Toast.makeText(MainActivity.this, "Hesap silinemedi!", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            });
                }*/
                Toast.makeText(MainActivity.this, "Hesap silinemedi!", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.cikis_yap:
                //FirebaseAuth.getInstance().signOut ile oturumu kapatabilmekteyiz.
                auth.signOut();

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
