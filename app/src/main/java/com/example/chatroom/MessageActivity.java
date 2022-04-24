package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.chatroom.Adapter.MessageAdapter;
import com.example.chatroom.Model.Chat;
import com.example.chatroom.Model.Users;
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

public class MessageActivity extends AppCompatActivity {
    TextView username;
    ImageView imageView;

    RecyclerView recyclerView;
    EditText msg_editText;
    ImageButton sendBtn;

    FirebaseUser fuser;
    DatabaseReference reference;
    Intent intent;

    MessageAdapter messageAdapter;
    List<Chat> mchat;

    RecyclerView recyclerViewy;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //widgets
        imageView=findViewById(R.id.profile);
        username=findViewById(R.id.username);
        sendBtn=findViewById(R.id.btn_send);
        msg_editText=findViewById(R.id.textsend);

        //RecyclerView
        recyclerViewy=findViewById(R.id.recycler_view);
        recyclerViewy.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerViewy.setLayoutManager(linearLayoutManager);
/*
        Toolbar toolbar=findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

*/
        intent=getIntent();

        String userid=intent.getStringExtra("userid");

        fuser= FirebaseAuth.getInstance().getCurrentUser();
        reference= FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user=snapshot.getValue(Users.class);
                username.setText(user.getName());

                if (user.getImageURL().equals("default")){
                    imageView.setImageResource(R.mipmap.ic_launcher);
                }

                else{
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(imageView);
                }

                readMessage(fuser.getUid(), userid, user.getImageURL());
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg=msg_editText.getText().toString();

                if(!msg.equals("")){
                    sendMessage(fuser.getUid(), userid, msg);
                }

                else{
                    Toast.makeText(MessageActivity.this, "Please send a none empty message.",  Toast.LENGTH_SHORT).show();
                }

                msg_editText.setText("");
            }
        });
    }

    private void sendMessage(String sender, String receiver, String message){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap=new HashMap<>();
        hashMap.put("sender", sender);
        hashMap.put("receiver", receiver);
        hashMap.put("message", message);

        reference.child("Chats").push().setValue(hashMap);

    }

    private void readMessage(String myid, String userid, String imageurl){
        mchat=new ArrayList<>();

        reference=FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mchat.clear();

                for(DataSnapshot datasnapshot:snapshot.getChildren()){
                    Chat chat=datasnapshot.getValue(Chat.class);

                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(userid) ||
                            chat.getReceiver().equals(userid) && chat.getSender().equals(myid)){
                        mchat.add(chat);
                    }
                    messageAdapter=new MessageAdapter(MessageActivity.this, mchat, imageurl);
                    recyclerViewy.setAdapter((messageAdapter));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void setSupportActionBar(Toolbar toolbar) {
    }
}