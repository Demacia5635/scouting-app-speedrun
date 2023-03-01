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
import java.util.HashMap;
import java.util.Map;

public class TryToUpload extends Service {
    private MediaPlayer mp1;
    private NetworkInfo netInfo;
    private ConnectivityManager cm;
    private  boolean didfinish;
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
        mp1 = MediaPlayer.create(this,R.raw.popo);
        mp1.setLooping(false);
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
        while (!didfinish){
            if(netInfo.isConnected()){
                mp1.start();
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

                        Log.e("PATH",parsepath);
                        addfielstofirebase(parsepath,data.substring(data.indexOf(autostart)+autostart.length(),data.indexOf(autoend)),"autonomous");
                        Log.e("data given",data.substring(data.indexOf(autostart)+autostart.length(),data.indexOf(autoend)));
                        addfielstofirebase(parsepath,data.substring(data.indexOf(autoend)+autoend.length(),data.indexOf(teleend)),"teleop");
                        addfielstofirebase(parsepath,data.substring(data.indexOf(teleend)+teleend.length(),data.indexOf(endgame)),"endgame");
                        data = data.substring(data.indexOf(endgame)+endgame.length());
                    }
                    didfinish = true;

                }


            }
        }
        return START_STICKY;
    }
    private  void addfielstofirebase(String datapath,String data2,String mode){;
        String temp="";
        Map<String, Object> firebasedat = new HashMap<>();
        String nameseprator = "//://";
        String valueseprator = "/de";
        String name="";
        String value="";
        while (data2.length()>0){
            name = data2.substring(0,data2.indexOf(nameseprator));
            value = data2.substring(data2.indexOf(nameseprator)+nameseprator.length(),data2.indexOf(valueseprator));
            Log.e(name,value);
            try{
                int valueint = Integer.parseInt(value);
                firebasedat.put(name,valueint);
            }catch (Exception e){
                try {
                    double valuedouble = Double.parseDouble(value);
                    firebasedat.put(name,valuedouble);
                }catch (Exception e2){
                    Boolean databollean = false;
                    Log.e("the value",value);
                    if(value.equals("false")){
                        Log.e("value1","false");
                        firebasedat.put(name,0);
                    }else if (value.equals("true")) {
                        Log.e("value","true");
                        firebasedat.put(name,1);
                    }else {
                        Log.e("value11",value);
                    firebasedat.put(name,value);
                    }
                }

            }
            data2 = data2.substring(data2.indexOf(valueseprator)+valueseprator.length());
        }
        try{
        db.collection(datapath).document(mode).set(firebasedat,SetOptions.merge());}
        catch (Exception e){
            Toast.makeText(this, "an error has accourd please contact supervisor details: "+e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("didnt","succed");
            didfinish = true;
        }
    }
}