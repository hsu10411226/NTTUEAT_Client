package com.example.pohan_pc.nttueat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class TabMenuActivity extends AppCompatActivity {

    private TabLayout mTabs;
    private ViewPager mViewPager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    private ArrayList<food> list = new ArrayList<food>();
    private ListView food_listview;
    private Myadapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_menu);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();


        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(TabMenuActivity.this,"請先登入",Toast.LENGTH_SHORT).show();
                    noLogin();
                }
            }
        };

        mViewPager = (ViewPager)findViewById(R.id.viewpager);
        MyPageAdapter ad = new MyPageAdapter();
        mViewPager.setAdapter(ad);
        mTabs = (TabLayout)findViewById(R.id.tabs);
        mTabs.setupWithViewPager(mViewPager);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logoutoption:
                AlertDialog.Builder builder = new AlertDialog.Builder(TabMenuActivity.this);
                builder.setTitle("即將登出")
                        .setMessage("確定要登出此帳號?")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                FirebaseAuth.getInstance().signOut();
                                noLogin();
                            }
                        });
                builder.show();
                return true;
                /*food test = new food("卡拉雞腿堡","50","http://www.hend-fond.com.tw/templates/cache/5683/images/products/photooriginal-5683-154313.jpg?04164");
                                mRef.child("Hamburger").child("1").setValue(test2);*/
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private class MyPageAdapter extends PagerAdapter {

        private final String[] tabTitles = {"Hamburger","Drinks","飲料類","燒餅類","套餐","超值套餐","今日優惠"};

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return object == view;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getLayoutInflater().inflate(R.layout.page_view,container,false);
            TextView title = (TextView) view.findViewById(R.id.item_title);
            title.setText(tabTitles[position]);
            food_listview = (ListView)view.findViewById(R.id.show_item);
            adapter = new Myadapter(TabMenuActivity.this,list);
            food_listview.setAdapter(adapter);
            getDataFromFirebase(tabTitles[position]);
            food_listview.setTag("new");
            container.addView(view);
            Toast.makeText(TabMenuActivity.this,"position:"+position,Toast.LENGTH_SHORT).show();
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public class Myadapter extends BaseAdapter{
        private LayoutInflater myInflater;
        private ArrayList<food> foods;

        public Myadapter(Context context, ArrayList<food> f){
            myInflater = LayoutInflater.from(context);
            this.foods = f;
        }
        class ViewHolder {
            ImageView img;
            TextView foodname;
            TextView foodvalue;
            public ViewHolder(TextView name,TextView value,ImageView img){
                this.foodname = name;
                this.foodvalue = value;
                this.img=img;
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
                convertView = myInflater.inflate(R.layout.food_item, null);
                holder = new ViewHolder(
                        (TextView)convertView.findViewById(R.id.food_title),
                        (TextView)convertView.findViewById(R.id.food_value),
                        (ImageView)convertView.findViewById(R.id.food_image)
                );
                convertView.setTag(holder);
            }
            else{
                holder = (ViewHolder)convertView.getTag();
            }
            final food food2 = (food) getItem(position);
            holder.foodname.setText(food2.getFoodName());
            holder.foodvalue.setText(food2.getFoodValue());
            Glide.with(holder.img.getContext()).load(food2.getFoodImg()).into(holder.img);
            return convertView;

        }
    }

    private void getDataFromFirebase(String want){
        mRef = database.getReference("food-data/"+want);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot ds : dataSnapshot.getChildren()){
                    food n = new food();
                    n.setFoodName((String)ds.child("foodName").getValue());
                    n.setFoodValue((String)ds.child("foodValue").getValue());
                    n.setFoodImg((String)ds.child("foodImg").getValue());
                    list.add(n);
                }
                adapter.notifyDataSetChanged();
                ListView n = (ListView) mViewPager.findViewWithTag("new");
                if(n!=null){
                    n.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void noLogin(){
        Intent intent = new Intent();
        intent.setClass(TabMenuActivity.this,LoginActivity.class);
        startActivity(intent);
    }
}
