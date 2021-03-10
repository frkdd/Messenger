package com.dinc.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.CellSignalStrengthGsm;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firestore;
    EditText usernameUp;
    EditText emailUp;
    EditText passwordUp;
    EditText nameUp;
    EditText surnameUp;


    String usernameFirebase;
    ArrayList<String> usernamesFirebase;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernamesFirebase = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firestore = FirebaseFirestore.getInstance();

        getDataFFB();

        usernameUp = findViewById(R.id.usernameUp);
        emailUp = findViewById(R.id.emailUp);
        passwordUp = findViewById(R.id.passwordUp);
        nameUp = findViewById(R.id.nameUp);
        surnameUp = findViewById(R.id.surnameUp);


        sharedPreferences = this.getSharedPreferences("com.dinc.messenger", Context.MODE_PRIVATE);
    }

    public void getDataFFB(){
        CollectionReference collectionReference = firestore.collection("UserData");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error != null){
                    Toast.makeText(SignUpActivity.this, error.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }

                if(value != null){
                    for (DocumentSnapshot snapshot : value.getDocuments()){

                        Map<String,Object> data = snapshot.getData();
                        usernameFirebase =(String) data.get("username");
                        usernamesFirebase.add(usernameFirebase);

                    }
                }
            }
        });
    }



    public void signUp(View view) {
        String email = emailUp.getText().toString();
        String password = passwordUp.getText().toString();
        String username = usernameUp.getText().toString();
        String name = nameUp.getText().toString();
        String surname = surnameUp.getText().toString();


        HashMap<String, Object> userData = new HashMap<>();


        if(usernamesFirebase.contains(username)) {
            Toast.makeText(this, username + " is already in use", Toast.LENGTH_SHORT).show();
        }else{
            if (username.matches("")
                || password.matches("")
                || email.matches("")
                || name.matches("")
                || surname.matches("")) {
            Toast.makeText(this, "Missing information", Toast.LENGTH_SHORT).show();
            }else {
            if (usernameUp.getText().length() < 6) {
                Toast.makeText(this, "The given username is invalid.[ Username should be at least 6 characters]", Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {



                        sharedPreferences.edit().putString("usernameSP", email).apply();
                        Toast.makeText(SignUpActivity.this, "User Created", Toast.LENGTH_SHORT).show();

                        userData.put("email", email);
                        userData.put("password", password);
                        userData.put("username", username);
                        userData.put("name",name);
                        userData.put("surname",surname);
                        userData.put("date", FieldValue.serverTimestamp());

                        firestore.collection("UserData").add(userData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Toast.makeText(SignUpActivity.this, "User Information Updated", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                        Intent intentToMain = new Intent(SignUpActivity.this, FeedActivity.class);
                        startActivity(intentToMain);
                        finish();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }
    }

}