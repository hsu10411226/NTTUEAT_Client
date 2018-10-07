package com.example.pohan_pc.nttueat;

import java.util.ArrayList;

/**
 * Created by POHAN-PC on 2017/12/29.
 */

public class CartList {
    private static CartList cartList = new CartList();
    private ArrayList<food> cart_item = new ArrayList<food>();

    public CartList(){}

    public void add(food additem){
        cart_item.add(additem);
    }

    public ArrayList<food> get(){
        return cart_item;
    }

    public static CartList getCartList(){
        return cartList;
    }

    public void clear(){cart_item.clear();}
}
