package com.daatstudios.kadalaimittai.billing;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.dantsu.escposprinter.exceptions.EscPosBarcodeException;
import com.dantsu.escposprinter.exceptions.EscPosConnectionException;
import com.dantsu.escposprinter.exceptions.EscPosEncodingException;
import com.dantsu.escposprinter.exceptions.EscPosParserException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private RecyclerView listRv;
    private Button invertory, add, print;
    private AutoCompleteTextView addProductToList;
    public static List<listModel> listModel = new ArrayList<>();
    public static TextView tiems, totalPrice, disPrice;
    public static int tprice = 0;
    public static int count = 0;
    public static listAdapter adapter;
    public static List<String> ids;
    private boolean reductionSuccess;

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";

    private UsbManager mUsbManager;
    private UsbDevice mDevice;
    private UsbDeviceConnection mConnection;
    private UsbInterface mInterface;
    private UsbEndpoint mEndPoint;
    private PendingIntent mPermissionIntent;

    private EditText percentBtn;
    private Button dis, canceldis;

    int totalSale;


    List<String> bb = new ArrayList<>();
    List<listModel> aa = new ArrayList<>();


    String pqty;
    String damount;
    String am;


    private static Boolean forceCLaim = true;

    HashMap<String, UsbDevice> mDeviceList;
    Iterator<UsbDevice> mDeviceIterator;
    String fam;
    byte[] testBytes;

    Button save;
    static int dcount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        listRv = findViewById(R.id.listRV);
        invertory = findViewById(R.id.inventory);
        addProductToList = findViewById(R.id.searchAndAdd);
        add = findViewById(R.id.addToL);
        tiems = findViewById(R.id.totalItems);
        totalPrice = findViewById(R.id.totalPrice);
        print = findViewById(R.id.printBtn);
        dis = findViewById(R.id.disc);
        percentBtn = findViewById(R.id.percentage);
        save = findViewById(R.id.saveBtn);
        disPrice = findViewById(R.id.disPrice);
        canceldis = findViewById(R.id.canceldisc);

        canceldis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePrice();
            }
        });

        FirebaseFirestore.getInstance().collection("sales")
                .document("today").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String s = task.getResult().get("today").toString();
                            totalSale = Integer.parseInt(s);
                            System.out.println("T" + totalSale);
                            Toast.makeText(MainActivity.this, "Start Billing ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s[] = totalPrice.getText().toString().split("₹");
                final int s1 = Integer.parseInt(s[1]);
                totalSale = totalSale + s1;
                Map<String, Object> mp = new HashMap<>();
                mp.put("today", totalSale);
                FirebaseFirestore.getInstance().collection("sales").document("today").update(mp);
                qty();
                listModel.clear();
                aa.clear();
                adapter.notifyDataSetChanged();
                tiems.setText("0");
                totalPrice.setText("0");
                count = 0;
                totalSale = totalSale + tprice;
                tprice = 0;
                dcount = 0;
                refreshData();
            }
        });


        getSupportActionBar().hide();

        invertory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, inventoryActivity.class);
                intent.putExtra("TS", totalSale);
                startActivity(intent);
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);

//

        adapter = new listAdapter(listModel);
        adapter.notifyDataSetChanged();

        listRv.setLayoutManager(layoutManager);
        listRv.setAdapter(adapter);


        FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                bb.add(new String(documentSnapshot.getString("name")
                                        + " ₹ = " + documentSnapshot.getString("price")
                                        + " Q = " + documentSnapshot.getString("qty")
                                        + " ID = " + documentSnapshot.getId()));
                            }
                            Toast.makeText(MainActivity.this, "Start Billing", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, bb);
        addProductToList.setAdapter(adapter2);
        addProductToList.setThreshold(1);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(addProductToList.getText().length() < 1) && !(addProductToList.getText().equals(" "))) {
                    String pn = addProductToList.getText().toString();
                    System.out.println(pn);
                    String[] parts = pn.split(" ₹ = ");
                    String part1 = parts[0]; //name
                    String rest1 = parts[1]; //price
                    System.out.println(rest1);
                    String[] price = rest1.split(" Q = ");
                    String part2 = price[0];
                    System.out.println(part2);
                    String rest2 = price[1];
                    String[] id = rest2.split(" ID = ");
                    pqty = id[0];
                    String ID = id[1];
                    tprice = tprice + Integer.parseInt(part2);
                    count = count + 1;
                    listModel.add(new listModel(part1, part2, "1", ID, part2));
                    aa.add(new listModel(part1, part2, pqty, ID, part2));
                    adapter.notifyDataSetChanged();
                    addProductToList.setText("");
                    totalPrice.setText("Total Price ₹" + tprice);
                    tiems.setText("TotalItems (" + count + ")");
                }
            }
        });


        dis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                damount = percentBtn.getText().toString();
                if (dcount == 1 && damount.length() >= 0) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("Alert");
                    builder1.setMessage("Are you sure want to give discount again?.");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    am = String.valueOf((tprice * Integer.parseInt(damount) / 100));
                                    disPrice.setText("Discounted Price : " + am);
                                    fam = String.valueOf(tprice - (tprice * Integer.parseInt(damount) / 100));
                                    tprice = Integer.valueOf(fam);
                                    totalPrice.setText("Total Price ₹" + tprice);
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }
                if (damount.length() >= 0 && dcount == 0 && listModel.size() != 0) {
                    am = String.valueOf((tprice * Integer.parseInt(damount) / 100));
                    disPrice.setText("Discounted Price : " + am);
                    fam = String.valueOf(tprice - (tprice * Integer.parseInt(damount) / 100));
                    tprice = Integer.valueOf(fam);
                    totalPrice.setText("Total Price ₹" + tprice);
                    dcount = 1;
                }


            }
        });


        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s[] = totalPrice.getText().toString().split("₹");
                final int s1 = Integer.parseInt(s[1]);
                totalSale = totalSale + s1;
                dcount = 0;
                Map<String, Object> mp = new HashMap<>();
                mp.put("today", totalSale);
                FirebaseFirestore.getInstance().collection("sales").document("today").update(mp);
                qty();
                Toast.makeText(MainActivity.this, "Done!", Toast.LENGTH_SHORT).show();
                printUsb();
                totalSale = totalSale + tprice;

            }
        });


    }

    public static void changePrice() {
        int sum = 0;
        for (int x = 0; x < listModel.size(); x++) {
            int p = Integer.parseInt(listModel.get(x).getPrice());
            int q = Integer.parseInt(listModel.get(x).getQty());
            sum = sum + (p * q);
        }
        tprice = sum;
        dcount=0;
        totalPrice.setText(String.valueOf("₹" + tprice));
        disPrice.setText("Discounted price 0");
    }

    private void qty() {
        for (int x = 0; x < listModel.size(); x++) {
            String ids = listModel.get(x).getId();
            Map<String, Object> updateQty = new HashMap<>();
            updateQty.put("qty", String.valueOf(Integer.parseInt(aa.get(x).getQty()) - Integer.parseInt(listModel.get(x).getQty())));
            System.out.println(Integer.parseInt(aa.get(x).getQty()) - Integer.parseInt(listModel.get(x).getQty()));

            FirebaseFirestore.getInstance().collection("PRODUCTS")
                    .document(ids).update(updateQty).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        reductionSuccess = true;
                    } else {
                        Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        disPrice.setText("Discounted Price : 0");
        dcount = 0;
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MainActivity.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {
                            EscPosPrinter printer = null;
                            try {
                                printer = new EscPosPrinter(new UsbConnection(usbManager, usbDevice), 203, 76.2f, 53);
                            } catch (EscPosConnectionException e) {
                                e.printStackTrace();
                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                            try {
                                String combained = "ITEM       " + "QTY    " + "Price  " + "Total" + "\n================================================";
                                for (int x = 0; x < listModel.size(); x++) {
                                    String name = listModel.get(x).getName().substring(0, 8);
                                    String qty = listModel.get(x).getQty();
                                    String price = listModel.get(x).getPrice();
                                    String tprice = listModel.get(x).getTp();
                                    if (price.length() >= 3) {
                                        combained = combained + "\n" + name + "     " + qty + "     " + price + "     " + tprice;
                                    } else {
                                        combained = combained + "\n" + name + "     " + qty + "     " + price + "       " + tprice;
                                    }
                                }
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                                Date date = new Date();
                                String d = formatter.format(date);
                                String a = String.valueOf(Integer.parseInt(fam) + Integer.parseInt(am));
                                String t = "************************************************\nMRP PRICE : ₹" + a + "\n" + "AMOUNT TO BE PAID : ₹" + tprice + "\n" + "Your saving : ₹" + am + "\n" + d + "\n";
                                combained = combained + "\n";
                                String tq = "        **Hope you will visit us again**      ";
                                printer.printFormattedTextAndCut("[C]<u><font size='big'><b>Kadalai Mittai</b></font></u>\n[C]No. 1/189 LakshmiNagar,Chetpet Road\n[C]Arani - 632317 Ph-9942911490\n[L]----------------------------------------------\n" + combained + t + tq);
                                listModel.clear();
                                aa.clear();
                                adapter.notifyDataSetChanged();
                                tiems.setText("0");
                                totalPrice.setText("0");
                                count = 0;
                                totalSale = totalSale + tprice;
                                tprice = 0;
                                refreshData();

                            } catch (EscPosConnectionException e) {
                                e.printStackTrace();
                            } catch (EscPosParserException e) {
                                e.printStackTrace();
                            } catch (EscPosEncodingException e) {
                                e.printStackTrace();
                            } catch (EscPosBarcodeException e) {
                                e.printStackTrace();
                            }
                        } else {
                            Toast.makeText(context, "No printer", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }
    };

    public void printUsb() {
        UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(this);
        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        if (usbConnection != null && usbManager != null) {
            PendingIntent permissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(MainActivity.ACTION_USB_PERMISSION), 0);
            IntentFilter filter = new IntentFilter(MainActivity.ACTION_USB_PERMISSION);
            registerReceiver(this.usbReceiver, filter);
            usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);
        } else {
            Toast.makeText(MainActivity.this, "No printer", Toast.LENGTH_SHORT).show();
            System.out.println("NoP");
            Toast.makeText(MainActivity.this, "No printer", Toast.LENGTH_SHORT).show();
        }

    }


    private void refreshData() {
        bb.clear();
        FirebaseFirestore.getInstance()
                .collection("PRODUCTS")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()) {
                                bb.add(new String(documentSnapshot.getString("name")
                                        + " ₹ = " + documentSnapshot.getString("price")
                                        + " Q = " + documentSnapshot.getString("qty")
                                        + " ID = " + documentSnapshot.getId()));
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        FirebaseFirestore.getInstance().collection("sales")
                .document("today").get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String s = task.getResult().get("today").toString();
                        totalSale = Integer.parseInt(s);
                        System.out.println("T" + totalSale);
                    }
                });
        System.out.println(totalSale);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, bb);
        addProductToList.setAdapter(adapter2);
        addProductToList.setThreshold(1);


    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public static void refresh() {
        totalPrice.setText("Total Price ₹" + tprice);
        tiems.setText("TotalItems (" + count + ")");
    }
}