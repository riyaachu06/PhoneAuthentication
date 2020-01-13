package com.example.phoneauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class LoginActiivty extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;

    private TextView mloginformfeedback;
    private EditText mCountryCode, mphonenumner;
    private Button motpButton;
    private ProgressBar mloginProgress;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_actiivty);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        init();
        motpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String countrycode = mCountryCode.getText().toString();
                String phonenumber = mphonenumner.getText().toString();
                String completenumber = "+" + countrycode + " " + phonenumber;

                if (countrycode.isEmpty() || phonenumber.isEmpty()) {
                    mloginformfeedback.setVisibility(View.VISIBLE);
                    mloginformfeedback.setText("please fill the form to continue..");
                } else {
                    mloginformfeedback.setText("working...");
                    mloginProgress.setVisibility(View.VISIBLE);
                    motpButton.setEnabled(false);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(completenumber, 60, TimeUnit.SECONDS, LoginActiivty.this, mcallbacks);
                }
            }
        });

        mcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                mloginformfeedback.setText("verification failed,please try again..");
                mloginformfeedback.setVisibility(View.VISIBLE);
                mloginProgress.setVisibility(View.VISIBLE);
                motpButton.setEnabled(true);
            }

            @Override
            public void onCodeSent(final String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                new android.os.Handler().postDelayed(new Runnable() {
                                                         @Override
                                                         public void run() {
                                                             Intent otpintent = new Intent(LoginActiivty.this, OTPActivity.class);
                                                             otpintent.putExtra("AuthCredentials", s);
                                                             startActivity(otpintent);
                                                         }
                                                     },
                        10000);


            }
        };
    }

    private void init() {
        mCountryCode = findViewById(R.id.countrycode);
        mphonenumner = findViewById(R.id.enternumber);
        motpButton = findViewById(R.id.generateotp);
        mloginProgress = findViewById(R.id.progressBar2);
        mloginformfeedback = findViewById(R.id.login_form_feedback);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            sendusertohome();
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(LoginActiivty.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendusertohome();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mloginformfeedback.setText("error verifying OTP..");
                                mloginformfeedback.setVisibility(View.VISIBLE);
                            }
                        }
                        mloginProgress.setVisibility(View.INVISIBLE);
                        mloginProgress.setEnabled(true);

                    }
                });
    }

    public void sendusertohome() {
        Intent homeintent = new Intent(LoginActiivty.this, MainActivity.class);
        homeintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeintent);
        finish();
    }
}
