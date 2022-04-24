package com.example.chatroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.chatroom.Model.Users;
import com.example.chatroom.fragments.ChatsFragment;
import com.example.chatroom.fragments.UsersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseUser firebaseUser;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        myRef= FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users=snapshot.getValue(Users.class);
                //Toast.makeText(MainActivity.this, "User Login: "+users.getName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Tab layout and ViewPager
        TabLayout tabLayout=findViewById(R.id.tabLayout);
        ViewPager2 viewPager=findViewById(R.id.view_pager);
        String[] titles=new String[]{"Users", "Chats"};
        ViewPagerFragmentAdapter viewPagerAdapter=new ViewPagerFragmentAdapter(this);

        //viewPagerAdapter.addFragment(new ChatsFragment(), "Chats");
        //viewPagerAdapter.addFragment(new UsersFragment(), "Users");
        viewPager.setAdapter(viewPagerAdapter);

        //tabLayout.setupWithViewPager(viewPager);
        new TabLayoutMediator(tabLayout, viewPager, ((tab, position) -> tab.setText(titles[position]))).attach();
    }

    //logout function

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
                return true;
        }
        return false;
    }

    class ViewPagerFragmentAdapter extends FragmentStateAdapter {
        //private ArrayList<Fragment> fragments;
        //private ArrayList<String> titles;
        private String[] titles=new String[]{"Users", "Chats"};
        ViewPagerFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
            //this.fragments=new ArrayList<>();
            //this.titles=new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {

            switch (position){
                case 0:
                    return new UsersFragment();
                case 1:
                    return new ChatsFragment();
            }
            return new ChatsFragment();

            //return fragments.get(position);
        }

        @Override
        public int getItemCount() {
            return titles.length;
        }
        /*
        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @NonNull
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
        */
    }
}