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

    CountryCodePicker countryCodePicker;
    EditText phoneInput;
    Button sendOtpButton;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_phone_number);

        // Connect variables with IDs in the XML file
        countryCodePicker = findViewById(R.id.login_country_code);
        phoneInput = findViewById(R.id.login_mobile_number);
        sendOtpButton = findViewById(R.id.send_otp_button_login);
        progressBar = findViewById(R.id.login_progress_bar);

        // Hide the progress bar
        progressBar.setVisibility(View.GONE);

        // Connect countryCode with the EditText
        countryCodePicker.registerCarrierNumberEditText(phoneInput);
        sendOtpButton.setOnClickListener(v -> {
            // Check if the phone number is valid, and if not, display an error
            if(!countryCodePicker.isValidFullNumber()){
                phoneInput.setError("Phone number not valid");
                return;
            }
            // Prepare the intent to link two activities and send data from one to another
            Intent intent = new Intent(LoginPhoneNumberActivity.this,LoginOtpActivity.class);
            intent.putExtra("phone",countryCodePicker.getFullNumberWithPlus());
            // Start the new activity
            startActivity(intent);
        });
    }
}
