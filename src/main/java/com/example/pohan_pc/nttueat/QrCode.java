package com.example.pohan_pc.nttueat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.QRCodeWriter;
import android.Manifest;

import java.util.HashMap;

import static android.Manifest.permission.CAMERA;


public class QrCode extends AppCompatActivity {

    private ImageView qr_pic;
    private IntentIntegrator integrator;
    private Button scan_qr;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String data = "";
    private FirebaseDatabase database;
    private DatabaseReference mRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_code);

        if(ContextCompat.checkSelfPermission(QrCode.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(QrCode.this,new String[]{Manifest.permission.CAMERA},0);
        }

        qr_pic = (ImageView)findViewById(R.id.qrcode_show);
        //scan_qr = (Button)findViewById(R.id.scan_qrcode);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        Intent intent = this.getIntent();
        data = intent.getStringExtra("id");

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){
                    //data = (String) user.getEmail().toString();
                    QRCodeWriter writer = new QRCodeWriter();
                    try {
                        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE,512,512);
                        int width = bitMatrix.getWidth();
                        int height = bitMatrix.getHeight();
                        Bitmap bmp = Bitmap.createBitmap(width,height,Bitmap.Config.RGB_565);
                        for(int x=0;x<width;x++){
                            for(int y=0;y<height;y++){
                                bmp.setPixel(x,y,bitMatrix.get(x,y)? Color.BLACK:Color.WHITE);
                            }
                        }
                        qr_pic.setImageBitmap(bmp);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        //scan_qr.setOnClickListener(btnlist);
    }

    /*public Button.OnClickListener btnlist = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            integrator = new IntentIntegrator(QrCode.this);
            initiateScanning();
        }
    };*/

    private void initiateScanning(){
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("請掃描");
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        String a="";
        if (scanResult != null) {
            if(data!=null){
                a = data.getStringExtra("SCAN_RESULT");
                //Toast.makeText(QrCode.this,"You are : " + a,Toast.LENGTH_SHORT).show();
                mRef = database.getReference("Order/"+a);
                mRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String c = dataSnapshot.child("orderId").getValue().toString() + "\n";
                        c += dataSnapshot.child("orderPerson").getValue().toString()+ "\n";
                        c += dataSnapshot.child("orderPhone").getValue().toString()+ "\n";
                        c += dataSnapshot.child("orderStatus").getValue().toString()+ "\n";
                        c += dataSnapshot.child("orderTime").getValue().toString()+ "\n";
                        c += dataSnapshot.child("orderTotal").getValue().toString()+ "\n";
                        c += dataSnapshot.child("orderItem").getValue().toString();
                        Toast.makeText(QrCode.this,c,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        }

        // else continue with any other code you need in the method
        super.onActivityResult(requestCode, resultCode, data);
    }

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
