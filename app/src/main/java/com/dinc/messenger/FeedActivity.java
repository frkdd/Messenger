package com.dinc.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.dinc.messenger.Adapters.FriendAdapter;
import com.dinc.messenger.Adapters.NotificationAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class FeedActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    SharedPreferences sharedPreferences;
    BottomNavigationView bottomNavigation;
    BottomNavigationItemView notificationItemView,profileItemView;
    TabLayout tabLayout;
    ArrayList<String> usernameFromFB,requesterUsernameFFB;
    Button availableButton;
    PopupWindow popupWindow;
    RecyclerView popupRecycler,friendListReView;
    LinearLayout popupView;
    ConstraintLayout feedActivity;
    LayoutInflater layoutInflater;
    NotificationAdapter notificationAdapter;
    View viewPopUp;
    ArrayList<String> friendList;
    HashMap<String, Object> friendToFL;
    ArrayList<String> friendListFFB;
    FriendAdapter friendAdapter;
    View friendListConstraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        friendList = new ArrayList<>();
        friendToFL = new HashMap<>();
        usernameFromFB = new ArrayList<>();
        usernameFromFB.clear();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        popupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,true);

        friendListConstraintLayout = findViewById(R.id.friendListConstraintLayout);

        friendListFFB = new ArrayList<>();
        friendListReView =findViewById(R.id.friendListReView);
        layoutInflater = FeedActivity.this.getLayoutInflater();
        notificationItemView = findViewById(R.id.notification_bar);
        profileItemView = findViewById(R.id.profile_bar);

        popupView = findViewById(R.id.popupView);
        feedActivity = findViewById(R.id.feedActivity);

        bottomNavigation = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);

        requesterUsernameFFB = new ArrayList<>();

        viewPopUp = layoutInflater.inflate(R.layout.popup_window,null);
        LinearLayoutManager layoutManager = new LinearLayoutManager(viewPopUp.getContext());

        requesterUsernameFFB.clear();


        popupRecycler = viewPopUp.findViewById(R.id.popupRecycler);
        popupRecycler.setLayoutManager(layoutManager);

        notificationAdapter = new NotificationAdapter(requesterUsernameFFB);
        popupRecycler.setAdapter(notificationAdapter);

        tabLayout = findViewById(R.id.tabLayout);

        availableButton=findViewById(R.id.availableButton);
        sharedPreferences = this.getSharedPreferences("com.dinc.messenger", Context.MODE_PRIVATE);
        friendAdapter = new FriendAdapter(friendListFFB);
        getFriendListFFB();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getFriendListFFB();
    }

    public void updateFriendListOfUser(){
        String user = sharedPreferences.getString("username","sharedPrefERROR1");
        CollectionReference collectionReference =firebaseFirestore.collection("FriendList");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){

                }
                if (value!=null){
                    for(DocumentSnapshot snapshot : value.getDocuments()){
                        Map<String,Object> data = snapshot.getData();
                        if(data.get(user+"sList")!=null) {
                            ArrayList<String> friendList = (ArrayList) data.get(user + "sList");

                        }
                    }
                }

            }
        });
    }

    public void intentToChatScreen(View view){
        View row = layoutInflater.inflate(R.layout.friend_list_row,null);
        ViewGroup vG = (ViewGroup) view;
        TextView friendUsernameT = (TextView) vG.getChildAt(1);
        String friendUsername = friendUsernameT.getText().toString();

        if(row.getId()==view.getId()){
            Intent intentToChat = new Intent(FeedActivity.this,ChatActivity.class);
            if(intentToChat.getStringExtra("friend")!=null){

                intentToChat.putExtra("friend",friendUsername);
                startActivity(intentToChat);
            }else{
                intentToChat.removeExtra("friend");
                intentToChat.putExtra("friend",friendUsername);
                startActivity(intentToChat);
            }

        }
    }

    public void getFriendListFFB(){
        friendAdapter.notifyDataSetChanged();
        String user = sharedPreferences.getString("username", "sharedPrefERROR");
        firebaseFirestore.collection("FriendList").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                }
                if (value!=null){
                    friendListFFB.clear();
                    for (DocumentSnapshot snapshot : value.getDocuments()){

                        Map<String, Object> data = snapshot.getData();
                        if(data.get(user + "sList")!=null) {
                            friendListFFB = (ArrayList<String>) data.get(user + "sList");

                            friendListReView.setAdapter(friendAdapter);
                            friendListReView.setLayoutManager(new LinearLayoutManager(FeedActivity.this));
                            friendAdapter.notifyDataSetChanged();

                        }
                    }
                }
            }
        });

    }

    public void popUpWindowMethod(View view){
        PopupMenu popupMenuA = new PopupMenu(FeedActivity.this,availableButton);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            popupMenuA.setForceShowIcon(true);
        }
        popupMenuA.getMenuInflater().inflate(R.menu.available_menu,popupMenuA.getMenu());
        popupMenuA.show();
    }

    public void addRequestList(){

        String usernameSPs = sharedPreferences.getString("username","sharedPerfERROR");
        CollectionReference collectionReference = firebaseFirestore.collection("FriendRequests");
        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                requesterUsernameFFB.clear();
                if(error!=null){
                    Toast.makeText(FeedActivity.this, error.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                }
                if (value!=null){
                    for (DocumentSnapshot snapshot : value.getDocuments()){

                        Map<String, Object> data = snapshot.getData();
                        if(data.get("receiver").equals(usernameSPs)){

                            requesterUsernameFFB.add((String) data.get("sender"));
                            notificationAdapter.notifyDataSetChanged();

                        }

                    }
                }
            }
        });
        notificationAdapter.notifyDataSetChanged();

    }

    public void acceptRequest(View view){

        ViewGroup viewGroup = (ViewGroup) view.getParent();
        TextView text = (TextView) viewGroup.getChildAt(1);
        String requester = text.getText().toString();

        String user = sharedPreferences.getString("username","sharedPrefERROR");

        friendList.add(requester);
        friendToFL.put(user+"sList",friendList);

        firebaseFirestore.collection("FriendList")
                .document(user)
                .update(user+"sList",FieldValue.arrayUnion(requester)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                firebaseFirestore.collection("FriendRequests")
                        .document(requester+"->"+user).delete();
                friendList.clear();
                friendToFL.clear();
                notificationAdapter.notifyDataSetChanged();
                Toast.makeText(FeedActivity.this, requester+" added your friend list.", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                firebaseFirestore.collection("FriendList").document(user).set(friendToFL).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        firebaseFirestore.collection("FriendRequests")
                                .document(requester+"->"+user).delete();
                        friendList.clear();
                        friendToFL.clear();


                        HashMap<String ,Object> requesterListUpdate = new HashMap<String, Object>();
                        ArrayList<String> requestersList = new ArrayList<>();
                        requestersList.add(user);
                        requesterListUpdate.put(requester+"sList",requestersList);

                        firebaseFirestore.collection("FriendList").document(requester).update(requesterListUpdate).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                firebaseFirestore.collection("FriendList").document(requester).set(requesterListUpdate);
                            }
                        });

                        notificationAdapter.notifyDataSetChanged();
                        Toast.makeText(FeedActivity.this, requester+" added your friend list.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(FeedActivity.this, e.getLocalizedMessage().toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        addRequestList();
        notificationAdapter.notifyDataSetChanged();
    }

    public void refuseRequest(View view){
        ViewGroup viewGroup = (ViewGroup) view.getParent();
        TextView text = (TextView) viewGroup.getChildAt(1);
        String requester = text.getText().toString();

        String user = sharedPreferences.getString("username","sharedPrefERROR");
        requesterUsernameFFB.clear();
        firebaseFirestore.collection("FriendRequests")
                .document(requester+"->"+user).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(FeedActivity.this, "You have denied "+requester+"'s request", Toast.LENGTH_SHORT).show();
            }
        });

        notificationAdapter.notifyDataSetChanged();

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_settings:
                Intent intentToSettings = new Intent(FeedActivity.this,SettingsActivity.class);
                startActivity(intentToSettings);
                break;
            case R.id.bottom_add_friend:
                try {
                    Intent intentToAddFriend = new Intent(FeedActivity.this, AddFriend.class);
                    startActivity(intentToAddFriend);
                }catch (Exception e){
                    e.printStackTrace();
                }

                break;
            case R.id.notification_bar:
                popupWindow.setAnimationStyle(R.style.animation);
                popupWindow.setElevation(100);
                popupWindow.setContentView(viewPopUp);
                popupWindow.showAtLocation(feedActivity,Gravity.BOTTOM,0,280);
                requesterUsernameFFB.clear();
                notificationAdapter.notifyDataSetChanged();
                addRequestList();
                break;
            case R.id.profile_bar:

                PopupMenu profileMenu = new PopupMenu(FeedActivity.this,profileItemView);
                profileMenu.getMenuInflater().inflate(R.menu.profile_menu,profileMenu.getMenu());
                Menu menuOpts = profileMenu.getMenu();
                menuOpts.getItem(1).setTitle(sharedPreferences.getString("username","Profile"));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    profileMenu.setForceShowIcon(true);
                }
                profileMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.profileMenuItem){
                            Intent intentToProfile = new Intent(FeedActivity.this,ProfileActivity.class);
                            startActivity(intentToProfile);
                        }
                        if(item.getItemId()==R.id.sign_out){
                            AlertDialog alertDialog = new AlertDialog.Builder(FeedActivity.this)
                                    .setTitle("Sign out")
                                    .setMessage("Are you sure to exit from your account?")
                                    .setPositiveButton("Sign Out", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            firebaseAuth.signOut();
                                            Intent intent = new Intent(FeedActivity.this, SignInActivity.class);
                                            sharedPreferences.edit().remove("checkBoxCheck").apply();
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    }).show();
                        }
                        return false;
                    }
                });
                profileMenu.show();

                break;
        }
        return false;
    }
}
