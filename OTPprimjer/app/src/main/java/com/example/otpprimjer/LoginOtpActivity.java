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

import org.w3c.dom.Text;

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


        // uzimamo broj telefona koji smo proslijedili u LoginPhoneNumberActivity
        phoneNum = getIntent().getExtras().getString("phone");

        // saljemo otp
        sendOtp(phoneNum,false);

        nextButton.setOnClickListener(v -> {
            String enterOtp = otpInput.getText().toString(); // kada user unese otp

                                                                //s ovom funk. ispod provjeravamo
                                                                // da li je otp code ispravan
                                                                // te ga spremamo u varijablu
                                                                // credential
            PhoneAuthCredential credential =  PhoneAuthProvider.getCredential(verificationCode,enterOtp);

            // nakon prvojere pozivamo singIn funkciju sa prosljedenim parametrom credential
            signIn(credential);
        });

        resendOtp.setOnClickListener(v -> {
            sendOtp(phoneNum,true);
        });




    }


    void sendOtp(String phoneNumber, boolean isResend){
        setInProgres(true);
        startResentTimer();
        PhoneAuthOptions.Builder builder = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(timeOutSec, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // kada je verifikacija kompletna aplikacija ce sama od sebe pozvati ovu funkciju
                        signIn(phoneAuthCredential);
                        setInProgres(false);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        AndroidUtil.showToast(getApplicationContext(),"Verification failed");
                        setInProgres(false);
                    }

                    // funkcija ispod salje otp kod
                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken); // ova metoda salje kod user-u
                        verificationCode = s;
                        resendToken = forceResendingToken;
                        AndroidUtil.showToast(getApplicationContext(),"OTP send successfully");
                        setInProgres(false);
                    }
                });

        if(isResend){
            PhoneAuthProvider.verifyPhoneNumber(builder.setForceResendingToken(resendToken).build());
        }else{
            PhoneAuthProvider.verifyPhoneNumber(builder.build());
        }

    }

    void signIn(PhoneAuthCredential phoneAuthCredential){
        // login i idemo na sljedeci acitivity
        setInProgres(true);
        mAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                setInProgres(false);
                if(task.isSuccessful()){
                    // ako je task uspjesan prelazimo sa jednog acitivirija u drugi
                    // intent koristimo kako bi mogli dodati prijenos podatka tj broja telefona
                    Intent intent = new Intent(LoginOtpActivity.this, LoginUserNameActivity.class);
                    intent.putExtra("phone",phoneNum); //ubaciujemo phneNum sa imenom za phone
                    startActivity(intent);  // idemo u drugi acitivity
                }else{
                    AndroidUtil.showToast(getApplicationContext(),"OTP verification failed");
                }
            }
        });

    }
    void setInProgres(boolean inProgres){
        if(inProgres){ // provjeravamo da li je nesto u procesu
            progressBar.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            nextButton.setVisibility(View.VISIBLE);
        }
    }

    void startResentTimer(){
        resendOtp.setEnabled(false); // postavljamo da ne moze da klikne user
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //svake sekunde ce se pokratati ova funkcija
                timeOutSec--; //postavili smo na 60L tj 60sec
                //izvrsavamo prikaz za koliko sekundi mozemo izvrsiniti resend
                resendOtp.setText("Resend OTP in " + timeOutSec + "sec.");
                if(timeOutSec == 0){
                    timeOutSec = 60L; // postavljamo na pocetnu vrijednost
                    timer.cancel(); // gasim tajmer i omogucavam da se moze stisnuti resend btn
                    runOnUiThread(() -> {
                        resendOtp.setEnabled(true);
                    });
                }
            }
        }, 0,1000);
    }

}