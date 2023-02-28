package com.example.myapplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TryToUpload extends Service {
    private MediaPlayer mp1;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    private  boolean didupload;
    private FirebaseFirestore db;

    private FirebaseAuth auth;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        try{
        netInfo = cm.getActiveNetworkInfo();}catch (Exception e){
            Toast.makeText(this, "a network error has occured please contact supervisior error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        db =  FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        auth.signInAnonymously()
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("FB", "signInAnonymously:success");
                            FirebaseUser user = auth.getCurrentUser();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("FB", "signInAnonymously:failure", task.getException());
                            Toast.makeText(TryToUpload.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        while (!didupload){
            if(netInfo.isConnected()){
                String data="";
                try{
                FileInputStream fin = openFileInput("scoutersavedata.txt");
                int c;

                while( (c = fin.read()) != -1){
                    data = data + Character.toString((char)c);
                }

                    fin.close();} catch (Exception e) {
                Toast.makeText(this, "an error has occured please contact suprevisor error details: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if(data.length()>0){
                    String parsevalue = "";
                    String parsename = "";
                    String parsepath="";
                    String autostart = "data:";
                    String autoend = "/endauto/";
                    String teleend = "/endtele/";
                    String endgame = "/endgame/";
                    while (data.length()>0){
                        parsepath = data.substring(0,data.indexOf(autostart));
                        addfielstofirebase(parsepath,data.substring(data.indexOf(autostart)+autostart.length(),data.indexOf(autoend)),"autonomous");
                        parsepath = data.substring(0,data.indexOf(autoend));
                        addfielstofirebase(parsepath,data.substring(data.indexOf(autoend)+autoend.length(),data.indexOf(teleend)),"teleop");
                        parsepath = data.substring(0,data.indexOf(teleend));
                        addfielstofirebase(parsepath,data.substring(data.indexOf(teleend)+teleend.length(),data.indexOf(endgame)),"endgame");
                        data = data.substring(data.indexOf(endgame)+endgame.length());

                    }
                }


            }
        }
        return START_STICKY;
    }
    private  void addfielstofirebase(String datapath,String data,String mode){
        String temp="";
        Map<String, Object> firebasedat = new HashMap<>();
        String nameseprator = ":";
        String valueseprator = "/de";
        String name="";
        String value="";
        while (data.length()>0){
            name = data.substring(0,data.indexOf(nameseprator));
            value = data.substring(data.indexOf(nameseprator)+nameseprator.length(),data.indexOf(valueseprator));
            try{
                int valueint = Integer.parseInt(value);
                firebasedat.put(name,valueint);
            }catch (Exception e){
                try {
                    double valuedouble = Double.parseDouble(value);
                    firebasedat.put(name,valuedouble);
                }catch (Exception e2){
                    firebasedat.put(name,value);
                }

            }
            data = data.substring(data.indexOf(valueseprator)+valueseprator.length());
        }
        db.collection(datapath).document(mode).set(firebasedat, SetOptions.merge());
    }
}