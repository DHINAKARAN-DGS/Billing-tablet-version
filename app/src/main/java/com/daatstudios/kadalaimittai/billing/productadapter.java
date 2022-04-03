package com.daatstudios.kadalaimittai.billing;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class productadapter extends RecyclerView.Adapter<productadapter.ViewHolder> {
    private List<listModel> listModels;

    public productadapter(List<listModel> listModels) {
        this.listModels = listModels;
    }

    @NonNull
    @Override
    public productadapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_holder, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull productadapter.ViewHolder holder, int position) {
        String name = listModels.get(position).getName();
        String price = listModels.get(position).getPrice();
        String qty = listModels.get(position).getQty();
        String id = listModels.get(position).getId();
        holder.setData(name,price,qty,id);
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTxt,namePrice;
        private TextView qty;
        private Button edit;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namePrice = itemView.findViewById(R.id.price_holder);
            nameTxt = itemView.findViewById(R.id.name_holder);
            qty = itemView.findViewById(R.id.qty_holder);
            edit = itemView.findViewById(R.id.editP);
        }
        private void setData(String name,String price,String qtyV,String pid){
            namePrice.setText("₹"+price);
            nameTxt.setText(name);
            qty.setText(qtyV);

            edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse("https://console.firebase.google.com/u/7/project/kadalai-mittai-billing/firestore/data~2FPRODUCTS~2F"+pid); // missing 'http://' will cause crashed
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    itemView.getContext().startActivity(intent);
                }
            });

        }
    }
}
