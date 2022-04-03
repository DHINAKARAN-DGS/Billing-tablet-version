package com.daatstudios.kadalaimittai.billing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class stockActivity extends AppCompatActivity {

    private RecyclerView pc;
    private List<listModel> listModels = new ArrayList<>();
    private List<String> bb = new ArrayList<>();

    EditText searchtxt;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("List of products and quantity");

        pc = findViewById(R.id.pc);


        search = findViewById(R.id.search);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);


//

        productadapter adapter = new productadapter(listModels);
        adapter.notifyDataSetChanged();

        pc.setLayoutManager(layoutManager);
        pc.setAdapter(adapter);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(stockActivity.this, search.class));
            }
        });
        FirebaseFirestore.getInstance().
                collection("PRODUCTS").
                get().
                addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                listModels.add(new listModel(documentSnapshot.getString("name")
                                        , documentSnapshot.getString("price"),
                                        documentSnapshot.getString("qty"),
                                        documentSnapshot.getId(), ""));
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(stockActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}