package com.dinc.messenger;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    TextView usernameText;
    EditText namePro;
    EditText surnamePro;
    EditText emailPro;
    EditText joinPro;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sharedPreferences = this.getSharedPreferences("com.dinc.messenger", Context.MODE_PRIVATE);

        namePro = findViewById(R.id.namePro);
        surnamePro = findViewById(R.id.surnamePro);
        emailPro = findViewById(R.id.emailPro);
        joinPro = findViewById(R.id.joinPro);

        profileUpdate();

        usernameText=findViewById(R.id.username);
        usernameText.setText("@" + sharedPreferences.getString("username","")+"'s" +" Profile");
    }

    public void profileUpdate(){
        namePro.setText(sharedPreferences.getString("name",""));
        surnamePro.setText(sharedPreferences.getString("surname",""));
        emailPro.setText(sharedPreferences.getString("email",""));
        joinPro.setText(sharedPreferences.getString("date","").toString());

    }
}