package com.daatstudios.kadalaimittai.billing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class search extends AppCompatActivity {

    private EditText searchView;
    private Button searchBtn;
    private RecyclerView recyclerView;

    private List<listModel> listModels = new ArrayList<>();

    productadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();

        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.searchRV);
        searchBtn = findViewById(R.id.searchbutton2);


        LinearLayoutManager layoutManager = new LinearLayoutManager(search.this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);


        adapter = new productadapter(listModels);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String q = searchView.getText().toString();
                FirebaseFirestore.getInstance().collection("PRODUCTS").orderBy("name").startAt(q).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                listModels.add(new listModel(documentSnapshot.getString("name")
                                        , documentSnapshot.getString("price"),
                                        documentSnapshot.getString("qty"),
                                        documentSnapshot.getId(), ""));
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
            }
        });

        recyclerView.setAdapter(adapter);


    }



}