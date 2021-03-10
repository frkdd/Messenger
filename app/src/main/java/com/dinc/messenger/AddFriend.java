package com.dinc.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dinc.messenger.Adapters.AddFriendAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class AddFriend extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPreferences;

    String usernameFB;
    String fullNameFB;
    String searchKey;
    ArrayList<String> usernameFromFB;
    ArrayList<String> fullNameFromFB;
    String usernameFROMSP;

    RecyclerView recyclerViewAdd;
    AddFriendAdapter addFriendAdapter;
    TextInputLayout textInputLayout;

    ArrayList<String> friendRequest;
    EditText searchText;
    FloatingActionButton floatingActionButton;
    TextView recyclerUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);


        textInputLayout = findViewById(R.id.TextField);
        usernameFromFB = new ArrayList<>();
        fullNameFromFB = new ArrayList<>();
        searchText = findViewById(R.id.searchText);
        friendRequest = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        textChangeListener();

        recyclerViewAdd = findViewById(R.id.recyclerViewAdd);
        recyclerViewAdd.setLayoutManager(new LinearLayoutManager(this));
        addFriendAdapter = new AddFriendAdapter(usernameFromFB, fullNameFromFB);
        recyclerViewAdd.setAdapter(addFriendAdapter);

        floatingActionButton = findViewById(R.id.floatingActionButton);
        recyclerUsername = findViewById(R.id.recyclerUsername);

        sharedPreferences = this.getSharedPreferences("com.dinc.messenger", Context.MODE_PRIVATE);
        usernameFROMSP = sharedPreferences.getString("username", "nullUsername");
    }

    public void backButton(View view) {
        onBackPressed();
        InputMethodManager imm = (InputMethodManager) AddFriend.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }


    public void getDataFromFirestoreAdd() {

        searchKey = searchText.getText().toString();

        CollectionReference collectionReference = firebaseFirestore.collection("UserData");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    Toast.makeText(AddFriend.this, error.getLocalizedMessage().toString(), Toast.LENGTH_LONG).show();
                }

                if (value != null) {
                    for (DocumentSnapshot snapshot : value.getDocuments()) {

                        Map<String, Object> data = snapshot.getData();

                        usernameFB = (String) data.get("username");
                        fullNameFB = (String) data.get("name") + " " + data.get("surname");


                        if (!searchKey.matches("")) {
                            if (usernameFB.regionMatches(true, 0, searchKey, 0, searchKey.length())
                                    || fullNameFB.regionMatches(true, 0, searchKey, 0, searchKey.length())) {
                                usernameFromFB.add(usernameFB);
                                fullNameFromFB.add(fullNameFB);
                                addFriendAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        });


    }

    public void textChangeListener() {

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Animation fallDown = AnimationUtils.loadAnimation(AddFriend.this, R.anim.fade_in);
                recyclerViewAdd.startAnimation(fallDown);
                addFriendAdapter.notifyDataSetChanged();

            }

            @Override
            public void afterTextChanged(Editable s) {
                fullNameFromFB.clear();
                usernameFromFB.clear();
                getDataFromFirestoreAdd();

            }
        });

    }

    public void addButton(View view) {

        ViewGroup row = (ViewGroup) view.getParent();
        TextView text = (TextView) row.getChildAt(1);
        String addUsername = text.getText().toString();

        view.setVisibility(View.INVISIBLE);



        HashMap<String, Object> currentUser = new HashMap<>();

        currentUser.put("sender", usernameFROMSP);
        currentUser.put("receiver", addUsername);
        firebaseFirestore.collection("FriendRequests")
                .document(usernameFROMSP + "->" + addUsername)
                .set(currentUser).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(AddFriend.this, "Friend Request Sent", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddFriend.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                view.setVisibility(View.VISIBLE);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        currentUser.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
    

}