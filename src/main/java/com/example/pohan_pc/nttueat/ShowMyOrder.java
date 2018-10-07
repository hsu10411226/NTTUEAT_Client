package com.example.pohan_pc.nttueat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShowMyOrder extends AppCompatActivity {
    private ListView Myorder;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private String Nuser;
    private ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();
    private SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_my_order);

        Myorder = (ListView)findViewById(R.id.ListViewMyOrder);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(ShowMyOrder.this,"請先登入",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Nuser = user.getEmail().toString();
                }
            }
        };

        mRef = database.getReference("Order/");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    if(ds.child("orderPerson").getValue().toString().equals(Nuser)){
                        HashMap<String,String> n = new HashMap<String, String>();
                        n.put("Id",(String)ds.child("orderId").getValue());
                        n.put("Status","訂單狀態 : "+(String)ds.child("orderStatus").getValue() + "      " + "金額 : " + String.valueOf(ds.child("orderTotal").getValue()));
                        list.add(n);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        adapter = new SimpleAdapter(this,list,android.R.layout.simple_list_item_2,new String[]{"Id","Status"},new int[]{android.R.id.text1,android.R.id.text2});
        Myorder.setAdapter(adapter);
        Myorder.setOnItemClickListener(ListviewListener);
    }

    private ListView.OnItemClickListener ListviewListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String cid = ((TextView) view.findViewById(android.R.id.text1)).getText().toString();
            Intent intent = new Intent(ShowMyOrder.this,QrCode.class);
            intent.putExtra("id",cid);
            startActivity(intent);
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK))
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
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
