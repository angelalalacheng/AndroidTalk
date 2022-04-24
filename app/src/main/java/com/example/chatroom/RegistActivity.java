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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegistActivity extends AppCompatActivity {
    //widgets
    EditText name, password, email;
    Button btn;

    //Firebase
    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        //initial
        name=findViewById(R.id.user);
        password=findViewById(R.id.passwordtext);
        email=findViewById(R.id.emailtext);
        btn=findViewById(R.id.button);
        //Firebase auth
        auth=FirebaseAuth.getInstance();
        //Button
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_txt=name.getText().toString();
                String email_txt=email.getText().toString();
                String password_txt=password.getText().toString();

                if(TextUtils.isEmpty(username_txt) || TextUtils.isEmpty(email_txt) || TextUtils.isEmpty(password_txt)){
                    Toast.makeText( RegistActivity.this, "Please Fill All Fields", Toast.LENGTH_SHORT).show();
                }
                else{
                    RegisterNow(username_txt, email_txt, password_txt);
                }
            }
        });
    }

    private void RegisterNow(final String username, String email, String password){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            FirebaseUser firebaseUser=auth.getCurrentUser();
                            String userid=firebaseUser.getUid();

                            myRef= FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);

                            //Hashmap
                            HashMap<String, String>hashmap=new HashMap<>();
                            hashmap.put("id", userid);
                            hashmap.put("name", username);
                            hashmap.put("imageURL", "default");

                            //after successful registration
                            myRef.setValue(hashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent i =new Intent(RegistActivity.this, MainActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                        finish();
                                    }
                                }
                            });

                        }

                        else{
                            Toast.makeText( RegistActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}