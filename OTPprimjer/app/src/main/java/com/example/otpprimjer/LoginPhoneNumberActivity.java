package com.example.otpprimjer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.hbb20.CountryCodePicker;

public class LoginPhoneNumberActivity extends AppCompatActivity {

    //dodavamo varijable
    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        //povezujemo varijable sa id u xml file
        countryCodePicker = findViewById(R.id.login_country_code);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpButton = findViewById(R.id.send_otp_button_login);
        progressBar = findViewById(R.id.login_progress_bar);

        // gasimo progres barr
        progressBar.setVisibility(View.GONE);

        // povezujemo countryCode sa edit text-om
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpButton.setOnClickListener(v -> {
            // provjeravamo da li je broj telefona validan i ako nije
            // ispisujemo error
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone number not valid");
                return;
            }
            // previmo intet tj. povezujemo 2 activity kako bi sa jednog activity-a
            // mogli da saljemo podatke u drugi activity
            Intent intent = new Intent(LoginPhoneNumberActivity.this,LoginOtpActivity.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            // nakon toga pokrecemo novi activiti :
            startActivity(intent);
        });


    }
}