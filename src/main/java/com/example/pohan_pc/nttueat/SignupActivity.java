package com.example.pohan_pc.nttueat;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends Activity {
    EditText email,password,password2;
    Button go;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        email = (EditText)findViewById(R.id.register_email);
        password = (EditText)findViewById(R.id.register_password);
        password2 = (EditText)findViewById(R.id.register_password2);

        go = (Button)findViewById(R.id.register_go);
        go.setOnClickListener(btnlistener);

        mAuth = FirebaseAuth.getInstance();
    }

    Button.OnClickListener btnlistener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(email.getText().toString().contains("@")&&email.getText().toString().contains("nttu.edu.tw")){
                if(password.getText().toString().equals(password2.getText().toString())){
                    mAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                                Toast.makeText(SignupActivity.this,"創建成功，請到註冊信箱收取認證信",Toast.LENGTH_SHORT).show();
                                finish();
                            if(!task.isSuccessful()){
                                Toast.makeText(SignupActivity.this,"創建失敗",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                else{
                    password.setError("密碼不一致");
                }
            }
            else{
                email.setError("格式錯誤");
            }
        }
    };
}
