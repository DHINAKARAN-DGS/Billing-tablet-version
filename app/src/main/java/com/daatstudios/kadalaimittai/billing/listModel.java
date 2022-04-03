package com.daatstudios.kadalaimittai.billing;

public class listModel {
    public static final int ITEM_LAYOUT = 0;
    public static final int TOTAL_PRICE = 1;
    private String name,price,qty,id,tp;

    public listModel(String name,  String price,String qty,String id,String  tp) {
        this.name = name;
        this.qty = qty;
        this.price = price;
        this.id = id;
        this.tp = tp;
    }

    public String getTp() {
        return tp;
    }

    public void setTp(String tp) {
        this.tp = tp;
    }

    private int type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
