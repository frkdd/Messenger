package com.dinc.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Map;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;

    EditText usernameIn;
    EditText passwordIn;
    CheckBox checkBox;
    Button signInButton;
    Intent intentToFeed;

    String usernameFFB;
    String emailFFB;
    Timestamp dateFFB;
    String nameFFB;
    String surnameFFB;
    String username;
    String password;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        usernameIn = findViewById(R.id.usernameIn);
        passwordIn = findViewById(R.id.passwordIn);
        checkBox = findViewById(R.id.checkBox);
        signInButton = findViewById(R.id.signIn);
        signInButton.setEnabled(true);

        intentToFeed = new Intent(SignInActivity.this,FeedActivity.class);

        sharedPreferences = this.getSharedPreferences("com.dinc.messenger", Context.MODE_PRIVATE);

        if(sharedPreferences.getBoolean("checkBoxCheck",false)){
            startActivity(intentToFeed);
            finish();
        }else {
            firebaseAuth.signOut();
        }

        usernameIn.setText(sharedPreferences.getString("usernameSP",""));


    }

    public void editDataCaches(){
        username = usernameIn.getText().toString();
        CollectionReference collectionReference = firestore.collection("UserData");
        collectionReference.whereEqualTo("email",username).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error!=null){
                    Toast.makeText(SignInActivity.this, error.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                }
                if(value!=null){
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String, Object> dataFromFB =snapshot.getData();

                        usernameFFB = (String) dataFromFB.get("username");
                        emailFFB = (String) dataFromFB.get("email");
                        nameFFB = (String) dataFromFB.get("name");
                        surnameFFB = (String) dataFromFB.get("surname");
                        dateFFB =(Timestamp) dataFromFB.get("date");

                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MMMM.yyyy HH:mm");
                        String timeToDate  = dateFormat.format(dateFFB.toDate());

                        sharedPreferences.edit().putString("username",usernameFFB).apply();
                        sharedPreferences.edit().putString("email",emailFFB).apply();
                        sharedPreferences.edit().putString("name",nameFFB).apply();
                        sharedPreferences.edit().putString("surname",surnameFFB).apply();
                        sharedPreferences.edit().putString("date",timeToDate).apply();
                    }
                }
            }
        });



    }

    public void signFirstUp(View view){
        Intent intent = new Intent(SignInActivity.this,SignUpActivity.class);
        startActivity(intent);

    }

    public void signIn(View view){
        username = usernameIn.getText().toString();
        password = passwordIn.getText().toString();

        sharedPreferences.edit().putString("usernameSP",username).apply();



        if(checkBox.isChecked()){
            sharedPreferences.edit().putBoolean("checkBoxCheck",checkBox.isChecked()).apply();

            signInButton.setEnabled(false);

            //sign in with username -> firebaseAuth.signInWithCredential(new AuthCredential().)
            firebaseAuth.signInWithEmailAndPassword(username,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    editDataCaches();

                    startActivity(intentToFeed);
                    signInButton.setEnabled(true);
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    signInButton.setEnabled(true);
                    Toast.makeText(SignInActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            });

        }else{
            signInButton.setEnabled(false);
            if(username.matches("")||password.matches("")){
                Toast.makeText(this, "Missing information", Toast.LENGTH_SHORT).show();
                signInButton.setEnabled(true);
            }else{
                firebaseAuth.signInWithEmailAndPassword(username,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        editDataCaches();
                        
                        startActivity(intentToFeed);
                        signInButton.setEnabled(true);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        signInButton.setEnabled(true);
                        Toast.makeText(SignInActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
        InputMethodManager imm = (InputMethodManager) SignInActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

}