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
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText firstname;
    EditText lastname;
    Button login;
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
                                List<String> group = (List<String>) document.get(id);
                                if(group.get(1) == lastname){
                                    paths.add(group.get(4)+"data:/end");
                                    writeToInternal("datapaths.txt",group.get(4));

                                }
                            }
                        }
                    }
                });
        if(!failed){
            Intent intent = new Intent(this,AutonomousActivity.class);
            String[] pathsArray = new String[paths.size()];
            paths.toArray(pathsArray);
            intent.putExtra("paths",pathsArray);
            intent.putExtra("index",0);
            startActivity(intent);
        }


    }
    public void writeToInternal(String filename,String content){
        File path = getApplicationContext().getFilesDir();
        try {
            FileOutputStream writer = new FileOutputStream(new File(path,filename));
            content = content+"data:/end";
            writer.write(content.getBytes());
        }catch (Exception e){
            failed = true;
            Toast.makeText(this, "Internal storage error please contact suprevisor error details: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


}