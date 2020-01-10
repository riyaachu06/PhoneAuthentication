package com.example.phoneauthentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private Button logoutButn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        init();
        logoutButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                sendusertologin();
            }
        });
    }

    private void init() {
        logoutButn=findViewById(R.id.logoutbutn);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser == null) {
            sendusertologin();
        }
    }
private void sendusertologin(){
    Intent loginintent = new Intent(MainActivity.this, LoginActiivty.class);
    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    loginintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    startActivity(loginintent);
    finish();
}
}
