package com.example.otpprimjer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.otpprimjer.utils.AndroidUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class LoginOtpActivity extends AppCompatActivity {

    String phoneNum;
    Long timeOutSec = 60L;

    String verificationCode;
    PhoneAuthProvider.ForceResendingToken resendToken;

    Button nextButton;
    ProgressBar progressBar;
    EditText otpInput;
    TextView resendOtp;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_otp);

        nextButton = findViewById(R.id.login_next_button);
        progressBar = findViewById(R.id.login_progress_bar_recived_otp);
        otpInput = findViewById(R.id.login_otp);
        resendOtp = findViewById(R.id.resend_otp_textview);

        // Retrieve the phone number passed from LoginPhoneNumberActivity
        phoneNum = getIntent().getExtras().getString("phone");

        // Send OTP
        sendOtp(phoneNum, false);

        nextButton.setOnClickListener(v -> {
            // When the user enters OTP, we check if the OTP code is correct
            // and store it in the credential variable
            String enterOtp = otpInput.getText().toString();
            PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(verificationCode, enterOtp);

            // After verification, we call the signIn function with the passed credential parameter
            signIn(credential);
        });

        resendOtp.setOnClickListener(v -> {
            sendOtp(phoneNum, true);
        });
    }

    void sendOtp(String phoneNumber, boolean isResend){
        setInProgress(true);
        startResentTimer();
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeOutSec, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // When verification is complete, the application will automatically call this function
                        signIn(phoneAuthCredential);
                        setInProgress(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showToast(getApplicationContext(),"Verification failed");
                        setInProgress(false);
                    }

                    // This function sends the OTP code
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        // This method sends the code to the user
                        verificationCode = s;
                        resendToken = forceResendingToken;
                        AndroidUtil.showToast(getApplicationContext(),"OTP sent successfully");
                        setInProgress(false);
                    }
                });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        // Log in and go to the next activity
        setInProgress(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    // If the task is successful, move from one activity to another
                    // We use intent to be able to add data transfer, i.e., phone number
                    Intent intent = new Intent(LoginOtpActivity.this, LoginUserNameActivity.class);
                    intent.putExtra("phone",phoneNum);
                    startActivity(intent);
                }else{
                    AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                }
            }
        });
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    void startResentTimer(){
        resendOtp.setEnabled(false);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // This function will be executed every second
                timeOutSec--;
                // We execute a display for how many seconds we can perform a resend
                resendOtp.setText("Resend OTP in " + timeOutSec + "sec.");
                if(timeOutSec == 0){
                    timeOutSec = 60L;
                    timer.cancel();
                    runOnUiThread(() -> {
                        resendOtp.setEnabled(true);
                    });
                }
            }
        }, 0, 1000);
    }
}
