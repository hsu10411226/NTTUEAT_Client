package com.example.pohan_pc.nttueat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by POHAN-PC on 2017/12/23.
 */

public class CustomDialogActivity extends Activity{
    Button btn_confirm,btn_cancel;
    TextView text_content,text_close;
    EditText amount;
    CheckBox drink_big,add_cheese;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_dialog);

        Intent intent = this.getIntent();
        final String item = intent.getStringExtra("item");
        final String value = intent.getStringExtra("value");
        final String type = intent.getStringExtra("type");


        btn_confirm = (Button)findViewById(R.id.btn_confirm);
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        text_content = (TextView)findViewById(R.id.confirm_foodname);
        text_close = (TextView)findViewById(R.id.text_close);
        amount = (EditText)findViewById(R.id.amount);
        drink_big = (CheckBox)findViewById(R.id.drink_big);
        add_cheese = (CheckBox)findViewById(R.id.add_cheese);

        if(type.contains("H")||type.contains("T")||type.contains("蛋")||type.contains("燒")||type.contains("抓")){
            add_cheese.setText("加起司(+10元)");
            add_cheese.setVisibility(View.VISIBLE);
        }
        if(type.contains("飲")){
            drink_big.setText("飲料加大(+5元)");
            drink_big.setVisibility(View.VISIBLE);
        }

        text_content.setText("確定要購買 "+item+" 嗎?");

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(amount.getText().toString())){
                    amount.setError("此欄位不可為空");
                }else{
                    if(Integer.parseInt(amount.getText().toString())>0){
                        Intent i = new Intent();
                        i.putExtra("number",amount.getText().toString());
                        if(add_cheese.isChecked()){
                            i.putExtra("item",item+"(加起司)");
                            i.putExtra("value",String.valueOf((Integer.valueOf(value) + 10)));
                        }
                        else if(drink_big.isChecked()){
                            i.putExtra("item",item+"(加大)");
                            i.putExtra("value",String.valueOf((Integer.valueOf(value) + 5)));
                        }else{
                            i.putExtra("item",item);
                            i.putExtra("value",String.valueOf(Integer.valueOf(value)));
                        }
                        setResult(100,i);
                        finish();
                    }
                    else{
                        amount.setError("數量錯誤");
                    }
                }
            }
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("number","");
                i.putExtra("item","");
                i.putExtra("value","");
                setResult(100,i);
                finish();
            }
        });

        text_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra("number","");
                i.putExtra("item","");
                i.putExtra("value","");
                setResult(100,i);
                finish();
            }
        });
        Intent i = new Intent();
        i.putExtra("number","");
        i.putExtra("item","");
        i.putExtra("value","");
        setResult(100,i);
    }
}
