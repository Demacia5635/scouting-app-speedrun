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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    EditText firstname;
    EditText lastname;
    Button login;
    Button reloadata;

    Map<String,String> thingsTosave = new HashMap<>();
    String thingsToResave = "";
    ArrayList<String> paths = new ArrayList<String>();
    boolean failed = false;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FileOutputStream fOut;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Intent intent2 = getIntent();
        boolean reloaddata = false;
        thingsTosave.put("Practices","");
        thingsTosave.put("Quals","");
        thingsTosave.put("Playoffs","");
        String mode = "Quals";
        try {
            reloaddata = intent2.getExtras().getBoolean("reloaddata",false);
            Log.e("mode:",mode);
            mode = intent2.getExtras().getString("mode","Quals");
        }catch (Exception e){

        }
        String varToLoad ="";
        if(mode.equals("Practices")){
            varToLoad = "Practice";
        } else if (mode.equals("Quals")) {
            varToLoad = "Qual";
        } else if (mode.equals("Playoffs")) {
            varToLoad="Match";
        }
        reloadata = findViewById(R.id.ReLoadData);
        Log.e("path",getFilesDir().toString());
        firstname = findViewById(R.id.firstname);
        lastname = findViewById(R.id.lastname);
        login = findViewById(R.id.login);
        login.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
        StringBuilder sb = new StringBuilder();

        try{
            FileInputStream fin = openFileInput(mode+"scoutersavedata.txt");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin,"UTF-8"));
            int c;
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            fin.close();} catch (Exception e) {
            Toast.makeText(this, "an error has occured please contact suprevisor error details: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        Log.e("reload data",reloaddata+"");
        Log.e("mode",mode);
        String data = sb.toString();
        if(data.length()>0 && !reloaddata){
            String parsepath ="";
            String endgame = "/endgame/";
            while (data.length()>0){
                parsepath = data.substring(0,data.indexOf(endgame)+endgame.length());
                paths.add(parsepath);
                data = data.substring(data.indexOf(endgame)+endgame.length());
            }
            Intent intent = new Intent(LoginActivity.this,AutonomousActivity.class);
            ArrayList<Integer> sorting = new ArrayList<Integer>();
            Map<String,String> toPaths= new HashMap<>();
            for (String path:paths) {
                Log.e("value before",path);
                String qualssubpath = path;
                String quals = varToLoad;
                if(qualssubpath.indexOf(mode)!=-1){
                    qualssubpath = qualssubpath.substring(qualssubpath.indexOf(mode)+mode.length()+1);
                    Log.e("WTF",quals+" space "+ path);
                    Log.e("why",qualssubpath);
                    qualssubpath = qualssubpath.substring(0,qualssubpath.indexOf("/"));
                    Log.e("trin",qualssubpath);
                    if(qualssubpath.equals("023")||qualssubpath.equals("ns")){
                        String asd ="asds";
                        asd.length();
                    }
                    toPaths.put(qualssubpath,path);
                    qualssubpath = qualssubpath.substring(qualssubpath.indexOf(quals)+quals.length());
                    sorting.add(Integer.parseInt(qualssubpath));
                    qualssubpath = path;}
            }
            Collections.sort(sorting);
            for (int i = 0; i < sorting.size(); i++) {
                Log.e("value",toPaths.get(varToLoad+sorting.get(i)));
                paths.add(toPaths.get(varToLoad+sorting.get(i)));

            }
            Log.e("yellow","Strat");
            intent.putExtra("paths",paths);
            intent.putExtra("index",0);
            intent.putExtra("mode",mode);
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
        Toast.makeText(this, "loading please wait...", Toast.LENGTH_SHORT).show();
        if(view.equals(login)){
            try {

                FileOutputStream fOut = openFileOutput("Playoffs"+"scoutersavedata.txt", Context.MODE_PRIVATE);
                String thingsToDelete = "";
                fOut.write(thingsToDelete.getBytes());
                fOut.close();

                FileOutputStream fOut2 = openFileOutput("Quals"+"scoutersavedata.txt", Context.MODE_PRIVATE);
                String thingsToDelete2 = "";
                fOut.write(thingsToDelete.getBytes());
                fOut.close();

                FileOutputStream fOut3 = openFileOutput("Practices"+"scoutersavedata.txt", Context.MODE_PRIVATE);
                String thingsToDelete3 = "";
                fOut.write(thingsToDelete.getBytes());
                fOut.close();
            }catch (Exception e){
                Toast.makeText(this, "Internal storage error please contact suprevisor error details: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
        for (int i = 0; i <6; i++) {

            writefielddata(i+"",firstname.getText().toString(),lastname.getText().toString(),"Playoffs",false);
            writefielddata(i+"",firstname.getText().toString(),lastname.getText().toString(),"Quals",true);
//            writefielddata(i+"",firstname.getText().toString(),lastname.getText().toString(),"Practices",true);

        }} else if (view.equals(reloadata)) {
            for (int i = 0; i < 6; i++) {
                reLoadfielddata(i+"",firstname.getText().toString(),lastname.getText().toString(),"Playoffs",false);
                reLoadfielddata(i+"",firstname.getText().toString(),lastname.getText().toString(),"Quals",false);
                reLoadfielddata(i+"",firstname.getText().toString(),lastname.getText().toString(),"Practices",true);
            }
        }
    }
    private void writefielddata(String id,String firstname,String lastname,String mode,boolean last){
        Log.e("mode",mode);
        db.collection("seasons/2023/competitions/ARPKY/"+mode)
                .whereArrayContains(id,firstname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String varToLoad ="";
                        if(mode.equals("Practices")){
                            varToLoad = "Practice";
                        } else if (mode.equals("Quals")) {
                            varToLoad = "Qual";
                        } else if (mode.equals("Playoffs")) {
                            varToLoad="Match";
                        }
                        Boolean auth = false;
                        if(task.isSuccessful()){
                            if(task.getResult().getDocuments().size() > 0){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                ArrayList<String> group = (ArrayList<String>) document.get(id);
                                if(group.get(2).equals(lastname)){
                                    Log.e("read: ",document.getReference().getPath()+"/"+document.getString(id+"-path"));
                                    paths.add(document.getReference().getPath()+"/"+document.getString(id+"-path")+"data:/endauto//endtele//endgame/");
                                    auth = true;
                                    writeToInternal("datapaths.txt",document.getReference().getPath()+"/"+document.getString(id+"-path"),mode);

                                }else {
//                                    Log.e("rejected",document.getReference().getPath()+"/"+document.getString(id+"-path")+"data:/endauto//endtele//endgame/");
                                }

                            }
                            if(auth){
                            Intent intent = new Intent(LoginActivity.this,AutonomousActivity.class);
                            ArrayList<Integer> sorting = new ArrayList<Integer>();
                                Map<String,String> toPaths= new HashMap<>();
                                for (String path:paths) {
                                    Log.e("value before",path);
                                    String qualssubpath = path;
                                    String quals = varToLoad;
                                    if(qualssubpath.indexOf(mode)!=-1){
                                        qualssubpath = qualssubpath.substring(qualssubpath.indexOf(mode)+mode.length()+1);
                                        Log.e("WTF",quals+" space "+ path);
                                        if(path.contains("13/")){
                                            Log.e("fpund","it");
                                        }
                                        Log.e("why",qualssubpath);
                                        qualssubpath = qualssubpath.substring(0,qualssubpath.indexOf("/"));
                                        Log.e("trin",qualssubpath);
                                        toPaths.put(qualssubpath,path);
                                        qualssubpath = qualssubpath.substring(qualssubpath.indexOf(varToLoad)+varToLoad.length());
                                        sorting.add(Integer.parseInt(qualssubpath));
                                        qualssubpath = path;}
                                }
                                Collections.sort(sorting);
                                for (int i = 0; i < sorting.size(); i++) {
                                    Log.e("value2",varToLoad+sorting.get(i));
                                    Log.e("value",toPaths.get(varToLoad+sorting.get(i)));
                                    paths.set(i,toPaths.get(varToLoad+sorting.get(i)));

                                }

                            if(last){ intent.putExtra("paths",paths);
                                Log.e("patjo",paths.size()+"");
                                intent.putExtra("index",0);
                                intent.putExtra("mode",mode);
                            startActivity(intent);}}else  {
                            }
                            }else {
                            }
                        }else {

                            Toast.makeText(LoginActivity.this, "no such user exsits", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        if(!failed){

        }


    }
    public void writeToInternal(String filename,String content,String mode){
        try {
            FileOutputStream fOut = openFileOutput(mode+"scoutersavedata.txt",Context.MODE_PRIVATE);
            Log.e("WTImode",mode);
            OutputStreamWriter writer =  new OutputStreamWriter(fOut);
            thingsTosave.put(mode,thingsTosave.get(mode)+content+"data:/endauto//endtele//endgame/");
            fOut.write(thingsTosave.get(mode).getBytes());
            fOut.close();
        }catch (Exception e){
            failed = true;
            Toast.makeText(this, "Internal storage error please contact suprevisor error details: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    private void reLoadfielddata(String id,String firstname,String lastname,String mode,boolean last){

        db.collection("seasons/2023/competitions/ARPKY/"+mode)
                .whereArrayContains(id,firstname)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        String varToLoad ="";
                        if(mode.equals("Practices")){
                            varToLoad = "Practice";
                        } else if (mode.equals("Quals")) {
                            varToLoad = "Qual";
                        } else if (mode.equals("Playoffs")) {
                            varToLoad="Match";
                        }
                        Boolean auth = false;
                        String overalldata = "";
                        String endgame = "/endgame/";
                        StringBuilder sb = new StringBuilder();
                        try{
                            FileInputStream fin = openFileInput(mode+"scoutersavedata.txt");
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fin,"UTF-8"));
                            int c;
                            String line = null;
                            while ((line = bufferedReader.readLine()) != null) {
                                sb.append(line);
                            }

                            fin.close();} catch (Exception e) {
                        }
                        overalldata = sb.toString();
                        if(task.isSuccessful()){
                            if(task.getResult().getDocuments().size() > 0){
                                for (QueryDocumentSnapshot document : task.getResult()) {

                                    ArrayList<String> group = (ArrayList<String>) document.get(id);
                                    if(group.get(2).equals(lastname)){
                                        if(!overalldata.contains(document.getReference().getPath()+"/"+document.getString(id+"-path")+"data:")) {
                                            Log.e("read: ", document.getReference().getPath() + "/" + document.getString(id + "-path"));
                                            paths.add(document.getReference().getPath() + "/" + document.getString(id + "-path") + "data:/endauto//endtele//endgame/");
                                            auth = true;
                                            rewriteToInternal("datapaths.txt", document.getReference().getPath() + "/" + document.getString(id + "-path")+"data:/endauto//endtele//endgame/",mode);
                                        }else {
                                            Log.e("read: ", document.getReference().getPath() + "/" + document.getString(id + "-path"));
                                            paths.add(overalldata.substring(overalldata.indexOf(document.getReference().getPath()+"/"+document.getString(id+"-path")+"data:"),
                                                    overalldata.indexOf(endgame)+endgame.length()));

                                            auth = true;
                                            rewriteToInternal("datapaths.txt", overalldata.substring(overalldata.indexOf(document.getReference().getPath()+"/"+document.getString(id+"-path")+"data:"),
                                                    overalldata.indexOf(endgame)+endgame.length()),mode);
                                        }

                                    }else {

                                    }

                                }
                                if(auth){
                                    Intent intent = new Intent(LoginActivity.this,AutonomousActivity.class);
                                    ArrayList<Integer> sorting = new ArrayList<Integer>();
                                    Map<String,String> toPaths= new HashMap<>();
                                    for (String path:paths) {
                                        Log.e("value before",path);
                                        String qualssubpath = path;
                                        String quals = varToLoad;
                                        if(qualssubpath.indexOf(mode)!=-1){
                                            qualssubpath = qualssubpath.substring(qualssubpath.indexOf(mode)+mode.length()+1);
                                            Log.e("WTF",quals+" space "+ path);
                                            Log.e("why",qualssubpath);
                                            qualssubpath = qualssubpath.substring(0,qualssubpath.indexOf("/"));
                                            Log.e("trin",qualssubpath);
                                            if(qualssubpath.equals("023")||qualssubpath.equals("ns")){
                                                String asd ="asds";
                                                asd.length();
                                            }
                                            toPaths.put(qualssubpath,path);
                                            qualssubpath = qualssubpath.substring(qualssubpath.indexOf(quals)+quals.length());
                                            sorting.add(Integer.parseInt(qualssubpath));
                                            qualssubpath = path;}
                                    }
                                    Collections.sort(sorting);
                                    for (int i = 0; i < sorting.size(); i++) {
                                        Log.e("value",toPaths.get(varToLoad+i));
                                        paths.add(toPaths.get(varToLoad+i));

                                    }
                                    if(last){ intent.putExtra("paths",paths);
                                        Log.e("patjo",paths.size()+"");
                                        intent.putExtra("index",0);
                                        intent.putExtra("mode",mode);
                                        startActivity(intent);}else  {
                                }
                                }else  {
                                }
                            }else {

                            }
                        }else {

                            Toast.makeText(LoginActivity.this, "no such user exsits", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        if(!failed){

        }



    }
    public void rewriteToInternal(String filename,String content,String mode){
        try {
            FileOutputStream fOut = openFileOutput(mode+"scoutersavedata.txt",Context.MODE_PRIVATE);
            Log.e("WTImode",mode);
            OutputStreamWriter writer =  new OutputStreamWriter(fOut);
            thingsTosave.put(mode,thingsTosave.get(mode)+content+"data:/endauto//endtele//endgame/");
            fOut.write(thingsTosave.get(mode).getBytes());
            fOut.close();
        }catch (Exception e){
            failed = true;
            Toast.makeText(this, "Internal storage error please contact suprevisor error details: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


}