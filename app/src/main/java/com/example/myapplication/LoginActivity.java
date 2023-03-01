package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText firstname;
    EditText lastname;
    Button login;
    String thingsTosave = "";
    ArrayList<String> paths = new ArrayList<String>();
    boolean failed = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FileOutputStream fOut;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Log.e("path",getFilesDir().toString());
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        String data="";
        try{
            FileInputStream fin = openFileInput("scoutersavedata.txt");
            int c;

            while( (c = fin.read()) != -1){
                data = data + Character.toString((char)c);
            }

            fin.close();} catch (Exception e) {
        }
        if(data.length()>0){
            String parsepath ="";
            String endgame = "/endgame/";
            while (data.length()>0){

                parsepath = data.substring(0,data.indexOf(endgame)+endgame.length());
                paths.add(parsepath);
                data = data.substring(data.indexOf(endgame)+endgame.length());
            }
            Intent intent = new Intent(LoginActivity.this,AutonomousActivity.class);
            intent.putExtra("paths",paths);
            intent.putExtra("index",0);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        auth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FB", "signInAnonymously:success");
                            FirebaseUser user = auth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FB", "signInAnonymously:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {
        for (int i = 0; i < 6; i++) {
            writefielddata(i+"",firstname.getText().toString(),lastname.getText().toString());
        }
    }
    private void writefielddata(String id,String firstname,String lastname){
        db.collection("seasons/2023/competitions/ISDE2/Quals")
                .whereArrayContains(id,firstname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                ArrayList<String> group = (ArrayList<String>) document.get(id);
                                if(group.get(2).equals(lastname)){
                                    Log.e("read: ",document.getString(id+"-path"));
                                    paths.add(document.getString(id+"-path")+"data:/endauto//endtele//endgame/");
                                    writeToInternal("datapaths.txt",document.getString(id+"-path"));

                                }
                                Intent intent = new Intent(LoginActivity.this,AutonomousActivity.class);
                                intent.putExtra("paths",paths);
                                intent.putExtra("index",0);
                                startActivity(intent);
                            }
                        }else {
                            Log.e("ERROR:", task.getException().toString());
                            Toast.makeText(LoginActivity.this, "no such user exsits", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        if(!failed){

        }


    }
    public void writeToInternal(String filename,String content){
        try {
            FileOutputStream fOut = openFileOutput("scoutersavedata.txt",Context.MODE_PRIVATE);
            OutputStreamWriter writer =  new OutputStreamWriter(fOut);
            thingsTosave += content+"data:/endauto//endtele//endgame/";
            fOut.write(thingsTosave.getBytes());
            fOut.close();
        }catch (Exception e){
            failed = true;
            Toast.makeText(this, "Internal storage error please contact suprevisor error details: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


}