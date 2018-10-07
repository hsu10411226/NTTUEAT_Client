package com.example.pohan_pc.nttueat;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Cart extends AppCompatActivity {

    private ListView cart_list;
    private Myadapter2 myadapter2;
    private Button send_order;
    private Order order;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private String username;
    private ArrayList<food> a;
    private int Ordertotal = 0;
    private TextView Value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(Cart.this,"請先登入",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    username = user.getEmail().toString();
                }
            }
        };

        cart_list = (ListView)findViewById(R.id.cart_list);
        send_order = (Button)findViewById(R.id.send_order);
        send_order.setOnClickListener(btnLintener);
        CartList list = CartList.getCartList();
        a = list.get();
        myadapter2 = new Myadapter2(Cart.this,a);
        cart_list.setAdapter(myadapter2);
        for(int i = 0;i<a.size();i++){
            Ordertotal += Integer.valueOf(a.get(i).getFoodImg())*Integer.valueOf(a.get(i).getFoodValue());
        }
        Value = (TextView)findViewById(R.id.Cart_Value);
        Value.setText("目前金額: " + Ordertotal);

    }

    private Button.OnClickListener btnLintener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(Ordertotal == 0){
                Toast.makeText(Cart.this,"請點選餐點",Toast.LENGTH_SHORT).show();
            }
            else{
                AlertDialog.Builder builder = new AlertDialog.Builder(Cart.this)
                        .setTitle("即將送出訂單")
                        .setMessage("警告 : 送出後無法再更改該筆訂單!!")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Date currentTime = Calendar.getInstance().getTime();
                                String time = DateFormat.getDateTimeInstance().format(currentTime);
                                String id = UUID.randomUUID().toString();
                                GenerateToken q = new GenerateToken();
                                q.forcerefresh();
                                order = new Order(id,username,"0912345678",time,"New",a,Ordertotal,q.refreshedToken);
                                mRef = database.getReference("Order/");
                                mRef.child(id).setValue(order);
                                CartList.getCartList().clear();
                                Toast.makeText(Cart.this,"Send Order Success",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        });
                builder.show();
            }

        }
    };

    class Myadapter2 extends BaseAdapter {
        private LayoutInflater myInflater;
        private ArrayList<food> foods;

        public Myadapter2(Context context, ArrayList<food> f){
            myInflater = LayoutInflater.from(context);
            this.foods = f;
        }
        class ViewHolder {
            TextView cartname;
            TextView cartnumber;
            TextView carttotal;
            public ViewHolder(TextView name,TextView number,TextView total){
                this.cartname = name;
                this.cartnumber = number;
                this.carttotal=total;
            }
        }
        @Override
        public int getCount() {
            return foods.size();
        }

        @Override
        public Object getItem(int position) {
            return foods.get(position);
        }

        @Override
        public long getItemId(int position) {
            return foods.indexOf(getItem(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView==null){
                convertView = myInflater.inflate(R.layout.cart_item, null);
                holder = new ViewHolder(
                        (TextView)convertView.findViewById(R.id.cart_list_name),
                        (TextView)convertView.findViewById(R.id.cart_list_number),
                        (TextView) convertView.findViewById(R.id.cart_list_total)
                );
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder)convertView.getTag();
            }
            final food food2 = (food) getItem(position);
            holder.cartname.setText(food2.getFoodName());
            holder.cartnumber.setText(food2.getFoodValue());
            holder.carttotal.setText(food2.getFoodImg());
            return convertView;
        }
    }

    @Override
    protected void onStart() {
        mAuth.addAuthStateListener(mAuthListener);
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
