package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.File;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        for (int i = 0; i < 6; i++) {
            writefielddata(i+"",firstname.getText().toString(),lastname.getText().toString());
        }
    }
    private void writefielddata(String id,String firstname,String lastname){
        db.collection("seasons/2023/competitions/ISDE2")
                .whereArrayContains(id,firstname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String[] group = (String[]) document.get(id);
                                if(group[2] == lastname){
                                    paths.add(group[3]+"data:/end/");
                                    writeToInternal("datapaths.txt",group[3]);

                                }
                            }
                        }else {
                            Toast.makeText(LoginActivity.this, "no such user exsits", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        if(!failed){
            Intent intent = new Intent(this,AutonomousActivity.class);
            intent.putExtra("paths",paths);
            intent.putExtra("index",0);
            startActivity(intent);
        }


    }
    public void writeToInternal(String filename,String content){
        try {
            FileOutputStream fOut = openFileOutput("file name",Context.MODE_PRIVATE);
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