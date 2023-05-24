package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;


import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Map;

public class AutonomousActivity extends AppCompatActivity implements View.OnClickListener, ViewTreeObserver.OnScrollChangedListener {
    LinearLayout linearLayout;
    String varToLoad , mode;

    ScrollView scrollView;
    Boolean didEND;
    ArrayList<String> paths = new ArrayList<String>();
    int index;
    boolean isloogedout = false;
    Button prev, next,gotoquals;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    LayoutBuilder autoLayoutBuilder;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter ifilter = new IntentFilter(
                Intent.ACTION_BATTERY_LOW);
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "YOUR BATTERY IS LOW PLEASE CHARGE YOUR PHONE", Toast.LENGTH_SHORT).show();
            }
        };
        getApplicationContext().registerReceiver(receiver,ifilter);
        setContentView(R.layout.activity_autonomous);
        linearLayout = findViewById(R.id.linearlayout);
        Intent intent = getIntent();
        scrollView = findViewById(R.id.autoScrollView);
       for(String path : intent.getExtras().getStringArrayList("paths")){
           paths.add(path);
       }
       mode = "practices";
       mode = intent.getExtras().getString("mode");
         varToLoad ="";
        if(mode.equals("Practices")){
            varToLoad = "Practice";
        } else if (mode.equals("Quals")) {
            varToLoad = "Qual";
        } else if (mode.equals("Playoffs")) {
            varToLoad="Match";
        }
        ArrayList<qual> qual = new ArrayList<>();
        for (String path:paths) {
            qual q1 = new qual(path,mode);
            qual.add(q1);
        }
        autoLayoutBuilder = new LayoutBuilder(qual,Constants.autostart,Constants.autoend,linearLayout,this,"autonomous",index);

        gotoquals = findViewById(R.id.fromautotoquals);
        index = intent.getExtras().getInt("index");
        TextView match = findViewById(R.id.qualauto);
       String quals = qual.get(index).getQualsName();
       String team = qual.get(index).getTeamNumber();
       String mode = "autonomous";
       match.setText(quals+" team: "+team+ " mode: "+mode);
       prev = findViewById(R.id.prevendgame);
       if(index == 0){
           prev.setText("Logout");
           isloogedout = true;
       }
        didEND=false;

       next = findViewById(R.id.nextteleop);
        gotoquals.setOnClickListener(this);
        prev.setOnClickListener(this);
       next.setOnClickListener(this);
       scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        auth = FirebaseAuth.getInstance();
        autoLayoutBuilder.addViewsToLinearLayout();
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
                            Toast.makeText(AutonomousActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }



    @Override
    public void onClick(View view) {

        autoLayoutBuilder.updateData();
        if(view == next){ DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            if(didEND || height >= linearLayout.getHeight()){
            Intent intent = new Intent(AutonomousActivity.this,TeleopActivity.class);
            intent.putExtra("paths",autoLayoutBuilder.getPaths());
            intent.putExtra("index",index);
            intent.putExtra("mode",mode);
            startActivity(intent);}else {
                Toast.makeText(this, "please scroll to the end of the screen", Toast.LENGTH_SHORT).show();
            }
        } else if (view==prev) {
            if(isloogedout){
                Intent intent = new Intent(AutonomousActivity.this,LoginActivity.class);
                intent.putExtra("reloaddata",true);

                startActivity(intent);
            }else {
            index--;
            Intent intent = new Intent(AutonomousActivity.this,EndGame.class);
            intent.putExtra("paths",autoLayoutBuilder.getPaths());
                intent.putExtra("mode",mode);
                intent.putExtra("index",index);
            startActivity(intent);}
        }else if(view == gotoquals){
            Intent intent = new Intent(AutonomousActivity.this,RecycleAct.class);
            intent.putExtra("paths",autoLayoutBuilder.getPaths());
            intent.putExtra("mode",mode);
            startActivity(intent);
        }
    }


    @Override
    public void onScrollChanged() {
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int topDetector = scrollView.getScrollY();
        int bottomDetector = view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
        if(bottomDetector == 0 ){
            didEND = true;
        }
    }

}