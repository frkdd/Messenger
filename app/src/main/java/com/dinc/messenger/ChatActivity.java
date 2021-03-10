package com.dinc.messenger;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import com.dinc.messenger.Adapters.MessageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class ChatActivity extends AppCompatActivity {

    TextView chatFriendName;
    TextInputEditText messageBox;
    String usernameSP;
    private FirebaseFirestore firestore;
    SharedPreferences sharedPreferences;
    SQLiteDatabase sqLiteDatabase;
    MessageAdapter messageAdapter;
    ArrayList<String> messagesSQL,timestampSQL;
    RecyclerView chatRecycler;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        chatFriendName = findViewById(R.id.chatFriendName);
        contentView();

        firestore = FirebaseFirestore.getInstance();
        messageBox = findViewById(R.id.messageBox);
        sharedPreferences = getSharedPreferences("com.dinc.messenger", MODE_PRIVATE);
        sqLiteDatabase = ChatActivity.this.openOrCreateDatabase("chatLogs", MODE_PRIVATE, null);


        usernameSP = sharedPreferences.getString("username", "sharedPrefErrorChatACT");
        messagesSQL = new ArrayList<>();
        timestampSQL = new ArrayList<>();

        chatRecycler = findViewById(R.id.chatRecycler);
        messageAdapter = new MessageAdapter(messagesSQL,timestampSQL);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ChatActivity.this);


        chatRecycler.setLayoutManager(layoutManager);
        chatRecycler.setAdapter(messageAdapter);

        getDataFromSQL();
        getDataFromFB();
    }


    public void backButtonCA(View view) {
        onBackPressed();
        finish();
        InputMethodManager imm = (InputMethodManager) ChatActivity.this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    public void contentView(){
        Bundle extras = getIntent().getExtras();
        String friend = extras.getString("friend");
        chatFriendName.setText(friend);
    }

    public void sendMessage(View view){
        messagesSQL.clear();
        timestampSQL.clear();
        UUID uuid = UUID.randomUUID();
        HashMap<String,Object> objectMap = new HashMap<>();
        ArrayList<Object> messageData = new ArrayList<>();
        messageData.add(messageBox.getText().toString());
        messageData.add(Timestamp.now());
        objectMap.put(usernameSP+"-sender"+uuid,messageData);



        if(!messageBox.getText().toString().equals("")
                &&messageBox!=null
                &&!messageBox.getText().toString().trim().isEmpty()){

            firestore.collection("ChatLogs")
                    .document(chatFriendName.getText().toString()+"-receiver")
                    .set(objectMap,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    try {
                        String sqlExecLine =
                                "CREATE TABLE IF NOT EXISTS "
                                        + usernameSP
                                        + "to" + chatFriendName.getText().toString()
                                        + "(object VARCHAR,sqltime TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL)";

                        sqLiteDatabase.execSQL(sqlExecLine);
                        String sqlExecLineInsert =
                                "INSERT INTO "
                                        + usernameSP
                                        + "to" + chatFriendName.getText().toString()
                                        + "(object) VALUES (" +"'"+ messageBox.getText().toString()+"'" +")";
                        sqLiteDatabase.execSQL(sqlExecLineInsert);
                        messageBox.setText("");

                        getDataFromSQL();
                    }catch (Exception e){
                        Toast.makeText(ChatActivity.this, "SQL database is unreachable. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ChatActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        objectMap.clear();
        messageData.clear();

        }
    }

    public  void getDataFromFB(){
        Map<String,Object> data = new HashMap<>();
        DocumentReference reference = firestore.collection("ChatLogs").document(usernameSP);
        reference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
           if(error!=null){
               Toast.makeText(ChatActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
           }
                if(value!=null){

                    data.put("messageData",value.getData());
                    System.out.println(data);
                }
            }
        });
        Map<String,Object> realData = (Map<String, Object>) data.get("messageData");
    }

    public void getDataFromSQL(){

        try {
            String getDataSQLExec = "SELECT * FROM "
                    + usernameSP
                    + "to" + chatFriendName.getText().toString();

            Cursor cursor = sqLiteDatabase.rawQuery(getDataSQLExec, null);
            int objectIx = cursor.getColumnIndex("object");
            int timestampIx = cursor.getColumnIndex("sqltime");
            String message = "";
            String timestamp = "";

            while (cursor.moveToNext()) {


                message = cursor.getString(objectIx);
                timestamp = cursor.getString(timestampIx);

                messagesSQL.add(message);
                timestampSQL.add(timestamp+" GMT");
                messageAdapter.notifyDataSetChanged();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

/* //Create database
            SQLiteDatabase database = this.openOrCreateDatabase("Musicians",MODE_PRIVATE,null);
            //Create content of database
            database.execSQL("CREATE TABLE IF NOT EXISTS musicians(id INTEGER PRIMARY KEY,name VARCHAR, age INTEGER)");

            //Add content
            database.execSQL("INSERT INTO  musicians(name, age) VALUES ('James',50)");
            //database.execSQL("INSERT INTO musicians(name, age) VALUES ('Lars',55)");
            //database.execSQL("INSERT INTO musicians(name, age) VALUES ('Kirk',45)");

            //DELETE AND UPDATE PROCESS
            //database.execSQL("UPDATE musicians SET age = 61 WHERE name = 'Lars'");
            //database.execSQL("DELETE FROM musicians");

            //DATABASE PRINTER
            //Cursor cursor = database.rawQuery("SELECT * FROM musicians WHERE name = 'James'",null);
            Cursor cursor = database.rawQuery("SELECT * FROM musicians",null);

            //DATABASE PRINTER WITH FILTER
            //Cursor cursor = database.rawQuery("SELECT * FROM musicians WHERE name LIKE '%s'",null);
            //Cursor cursor = database.rawQuery("SELECT * FROM musicians WHERE name LIKE 'K%'",null);


            int nameIx =cursor.getColumnIndex("name");
            int ageIx = cursor.getColumnIndex("age");
            int idIx = cursor.getColumnIndex("id");

            while(cursor.moveToNext()){
                System.out.println("Name: " + cursor.getString(nameIx));
                System.out.println("Age: " + cursor.getInt(ageIx));
                System.out.println("Id: "+ cursor.getInt(idIx));}

 */