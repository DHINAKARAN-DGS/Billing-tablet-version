package com.daatstudios.kadalaimittai.billing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class inventoryActivity extends AppCompatActivity {

    private Button addnewP,stocks;
    private TextView sales;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

;

        addnewP = findViewById(R.id.addnewProductBtn);
        sales = findViewById(R.id.textView);
        stocks = findViewById(R.id.stocks);

        getSupportActionBar().setTitle("Inventory");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        addnewP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(inventoryActivity.this,AddNewPrroductActivity.class));
            }
        });

        stocks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(inventoryActivity.this,stockActivity.class));
            }
        });

        FirebaseFirestore.getInstance().collection("sales").document("today").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String  s  = task.getResult().getString("Today");
                sales.setText("Total price ₹"+s);
            }
        });



    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseFirestore.getInstance().collection("sales").document("today").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                String  s  = task.getResult().get("today").toString();
                sales.setText("Total price ₹"+s);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}