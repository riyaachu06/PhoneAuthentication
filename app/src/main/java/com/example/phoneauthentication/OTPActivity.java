package com.example.phoneauthentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

public class OTPActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private String authVerificationId;

    private EditText mOtp;
    private Button mVerifyOtpButn;
    private ProgressBar mOtpProgess;
    private TextView mOtpFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        authVerificationId = getIntent().getStringExtra("AuthCredentials");
        init();
        mVerifyOtpButn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp = mOtp.getText().toString();
                if (otp.isEmpty()) {
                    mOtpFeedback.setText("please enter OTP to continue..");
                    mOtpFeedback.setVisibility(View.VISIBLE);
                } else {
                    mOtpFeedback.setText("working....");
                    mOtpProgess.setVisibility(View.VISIBLE);
                    mOtpFeedback.setEnabled(false);

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(authVerificationId, otp);
                    signInWithPhoneAuthCredential(credential);
                }
            }
        });

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(OTPActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendusertohome();

                        } else {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                mOtpFeedback.setText("error verifying OTP..");
                                mOtpFeedback.setVisibility(View.VISIBLE);
                            }
                        }
                        mOtpProgess.setVisibility(View.INVISIBLE);
                        mOtpFeedback.setEnabled(true);

                    }
                });
    }

    private void init() {
        mOtp = findViewById(R.id.enterotp);
        mVerifyOtpButn = findViewById(R.id.button);
        mOtpProgess = findViewById(R.id.progressBarotp);
        mOtpFeedback = findViewById(R.id.workingotp);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mCurrentUser != null) {
            sendusertohome();
        }
    }

    public void sendusertohome() {
        Intent homeintent = new Intent(OTPActivity.this, MainActivity.class);
        homeintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(homeintent);
        finish();
    }
}
