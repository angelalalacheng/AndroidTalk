package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText userLogin, passwordLogin;
    Button BtnLogin, BtnReg;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null){
            Intent i=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userLogin=findViewById(R.id.email_login);
        passwordLogin=findViewById(R.id.password_login);
        BtnLogin=findViewById((R.id.button2));
        BtnReg=findViewById(R.id.button3);

        auth=FirebaseAuth.getInstance();
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();

        //check user existences
        if (firebaseUser != null){
            Intent i=new Intent(LoginActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        }

        //Register(not member)
        BtnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(LoginActivity.this, RegistActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();
            }
        });

        //Login
        BtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email_txt=userLogin.getText().toString();
                String password_txt=passwordLogin.getText().toString();

                //pass data to firebase to check
                if (TextUtils.isEmpty(email_txt) || TextUtils.isEmpty((password_txt))){
                    Toast.makeText(LoginActivity.this, "Please Fill ALL Fields", Toast.LENGTH_SHORT).show();
                }

                else{
                    auth.signInWithEmailAndPassword(email_txt, password_txt)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent i =new Intent(LoginActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }

                                    else{
                                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

    }
}