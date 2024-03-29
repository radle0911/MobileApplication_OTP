package com.example.otpprimjer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.otpprimjer.model.userModel;
import com.example.otpprimjer.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import kotlin.collections.UArraySortingKt;

public class LoginUserNameActivity extends AppCompatActivity {

    EditText usernameInput;
    Button next;
    ProgressBar progressBar;

    String phoneNumber;

    userModel user_model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_user_name);

        usernameInput = findViewById(R.id.login_username);
        next = findViewById(R.id.login_next_three);
        progressBar = findViewById(R.id.login_progress_bar_three);

        phoneNumber = getIntent().getExtras().getString("phone");
        getUsername();

        next.setOnClickListener((v) -> {
            setUsername();
        });
    }

    void setInProgress(boolean inProgress){
        if(inProgress){
            progressBar.setVisibility(View.VISIBLE);
            next.setVisibility(View.GONE);
        }else{
            progressBar.setVisibility(View.GONE);
            next.setVisibility(View.VISIBLE);
        }
    }

    void setUsername(){
        String username = usernameInput.getText().toString();
        if(username.isEmpty() || username.length() <= 3){
            usernameInput.setError("Username length should be at least 3 chars");
            return;
        }
        setInProgress(true);
        if(user_model != null){
            user_model.setUsername(username);
        }else{
            user_model = new userModel(phoneNumber,username, Timestamp.now());
        }

        FirebaseUtil.currentUserDetails().set(user_model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginUserNameActivity.this,MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
    }

    void getUsername(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if(task.isSuccessful()){
                    user_model = task.getResult().toObject(userModel.class);
                    if(user_model != null){
                        usernameInput.setText(user_model.getUsername());
                    }
                }
            }
        });
    }
}
