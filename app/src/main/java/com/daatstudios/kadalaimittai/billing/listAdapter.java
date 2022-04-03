package com.daatstudios.kadalaimittai.billing;

import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class listAdapter extends RecyclerView.Adapter<listAdapter.ViewHolder> {
    private List<listModel> listModels;

    public listAdapter(List<listModel> listModels) {
        this.listModels = listModels;
    }

    @NonNull
    @Override
    public listAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_list_container, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull listAdapter.ViewHolder holder, int position) {
        String name = listModels.get(position).getName();
        String price = listModels.get(position).getPrice();
        String qty = listModels.get(position).getQty();
        String tp = listModels.get(position).getTp();

        holder.setData(name, price, qty, position, tp);
    }

    @Override
    public int getItemCount() {
        return listModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTxt, namePrice;
        private TextView qty, total;
        private ImageButton del;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            namePrice = itemView.findViewById(R.id.pprice);
            nameTxt = itemView.findViewById(R.id.pname);
            qty = itemView.findViewById(R.id.pqty);
            del = itemView.findViewById(R.id.deleteBtn);
            total = itemView.findViewById(R.id.ptprice);
        }

        private void setData(String name, String price, String qtyV, int position, String totalP) {
            namePrice.setText("₹" + price);
            total.setText("₹" + totalP);
            nameTxt.setText(name);
            qty.setText(qtyV);

            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity.tprice = MainActivity.tprice - (Integer.parseInt(listModels.get(position).getPrice()) * Integer.parseInt(listModels.get(position).getQty()));
                    MainActivity.listModel.remove(position);
                    MainActivity.adapter.notifyDataSetChanged();
                    MainActivity.count = MainActivity.count - 1;

                    if (listModels.size() == 0) {
                        MainActivity.tprice = 0;
                        MainActivity.dcount = 0;
                    }
                    MainActivity.disPrice.setText("Discounted Price : 0");
                    MainActivity.dcount = 0;
                    MainActivity.changePrice();
                    MainActivity.refresh();
                }
            });

            qty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog dialog = new Dialog(itemView.getContext());
                    dialog.setContentView(R.layout.quanty_dialogue);
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    dialog.show();

                    final EditText quantity_No = dialog.findViewById(R.id.quantityCount);

                    quantity_No.setHint("Quantity");

                    Button cancelBTn = dialog.findViewById(R.id.cancel_btn);
                    Button okBTn = dialog.findViewById(R.id.ok_btn);

                    cancelBTn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    okBTn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String qt = quantity_No.getText().toString();
                            int pq = Integer.parseInt(qty.getText().toString());
                            int tm = 0;
                            int q = Integer.parseInt(quantity_No.getText().toString());
                            int p = Integer.parseInt(listModels.get(position).getPrice());
                            tm = q * p;
//                            ;
//                            int finala = 0;
//                            int c = 0;
//                            int cp = 0;
//                            if (pq!=Integer.parseInt(qt)){
//
//                                finala = tm - Integer.parseInt(price);
//                                if (Integer.parseInt(qt)>pq){
//                                    c=Integer.parseInt(qt)-pq;
//                                }else{
//                                    c=pq-Integer.parseInt(qt);
//                                }
//                                cp = c*Integer.parseInt(MainActivity.listModel.get(position).getPrice());
//                                MainActivity.tprice = MainActivity.tprice - cp;
//                            }
                            qty.setText(qt);
                            MainActivity.listModel.get(position).setQty(qt);
                            total.setText("₹" + tm);
                            MainActivity.listModel.get(position).setTp(String.valueOf(tm));
                            MainActivity.listModel.get(position).setQty(qt);
                            MainActivity.adapter.notifyDataSetChanged();
                            String nq = MainActivity.listModel.get(position).getQty();
                            qty.setText(nq);
//                            MainActivity.tprice = MainActivity.tprice+finala;
                            String amm = String.valueOf(MainActivity.tprice);
                            MainActivity.totalPrice.setText(amm);
                            MainActivity.changePrice();
                            MainActivity.disPrice.setText("Discounted Price : 0");
                            MainActivity.dcount = 0;
                            dialog.dismiss();
                        }
                    });

                }
            });
        }
    }
}
