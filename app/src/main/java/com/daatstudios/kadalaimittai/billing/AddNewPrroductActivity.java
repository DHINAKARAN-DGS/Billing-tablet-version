package com.daatstudios.kadalaimittai.billing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNewPrroductActivity extends AppCompatActivity {

    private EditText name,qty,price;
    private Button addp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_prroduct);

        getSupportActionBar().setTitle("Add new Product");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        name = findViewById(R.id.addpname);
        qty = findViewById(R.id.addpqty);
        price = findViewById(R.id.addpprice);
        addp = findViewById(R.id.addp);

        addp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().isEmpty()&&name.getText().length()>=8){
                    if (!qty.getText().toString().isEmpty()){
                        if (!price.getText().toString().isEmpty()){

                            String pname = name.getText().toString();
                            String pqty = qty.getText().toString();
                            String pprice = price.getText().toString();

                            Map<String,Object> add = new HashMap<>();
                            add.put("name",pname);
                            add.put("price",pprice);
                            add.put("qty",pqty);

                            FirebaseFirestore.getInstance().collection("PRODUCTS")
                                    .add(add).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentReference> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(AddNewPrroductActivity.this, "Added successfully", Toast.LENGTH_SHORT).show();
                                        MainActivity.adapter.notifyDataSetChanged();
                                        name.setText("");
                                        qty.setText("");
                                        price.setText("");

                                    }
                                }
                            });
                        }else{
                            price.setError("*Required");
                        }
                    }
                    else{
                        qty.setError("*Required");
                    }
                }
                else{
                    name.setError("*Required");
                }
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