package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import java.util.ArrayList;
import java.util.Map;

public class TeleopActivity extends AppCompatActivity implements View.OnClickListener, ViewTreeObserver.OnScrollChangedListener {
    LinearLayout linearLayout;
    String varToLoad , mode;

    ArrayList<String> paths = new ArrayList<String>();
    int index;
    ScrollView scrollView;
    Boolean didEND;
    Button prev , next , gotoquals;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    LayoutBuilder teleopLayoutBuilder;

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teleop);
        linearLayout = findViewById(R.id.linearlayoutteled);
//        addViewsToLinearLayout();
        scrollView = findViewById(R.id.TeleOpScrollView);
        Intent intent = getIntent();
        for(String path : intent.getExtras().getStringArrayList("paths")){
            paths.add(path);
        }
        didEND=false;
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
        teleopLayoutBuilder = new LayoutBuilder(qual,Constants.autoend,Constants.teleend,linearLayout,this,"teleop",index);

        gotoquals = findViewById(R.id.fromteletoquals);
        index = intent.getExtras().getInt("index");
        TextView match = findViewById(R.id.qualtele);
        String quals = qual.get(index).getQualsName();
        String team = qual.get(index).getTeamNumber();
        String mode = "teleop";
        match.setText(quals+" team: "+team+ " mode: "+mode);
        prev = findViewById(R.id.prevauto);
        next = findViewById(R.id.nextendgame);
        gotoquals.setOnClickListener(this);
        prev.setOnClickListener(this);
        next.setOnClickListener(this);
        scrollView.getViewTreeObserver().addOnScrollChangedListener(this);
        auth = FirebaseAuth.getInstance();
        teleopLayoutBuilder.addViewsToLinearLayout();
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
                            Toast.makeText(TeleopActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    @Override
    public void onClick(View view) {
        String datastring="";

        teleopLayoutBuilder.updateData();
        if(view == next){
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int height = displayMetrics.heightPixels;
            Log.e("screenheight",height+"");
            Log.e("ypp","ypal");
            Log.e("scrollheight", linearLayout.getHeight()+"");
            if(didEND || height>=linearLayout.getHeight()){
            Intent intent = new Intent(TeleopActivity.this,EndGame.class);
            intent.putExtra("paths",teleopLayoutBuilder.getPaths());
            intent.putExtra("index",index);
            intent.putExtra("mode",mode);
            startActivity(intent);}else {
                Toast.makeText(this, "please scroll to the end of the screen", Toast.LENGTH_SHORT).show();

            }
        } else if (view==prev) {
            Intent intent = new Intent(TeleopActivity.this,AutonomousActivity.class);
            intent.putExtra("paths",teleopLayoutBuilder.getPaths());
            intent.putExtra("index",index);
            intent.putExtra("mode",mode);
            startActivity(intent);
        }else if(view == gotoquals){
            Intent intent = new Intent(TeleopActivity.this,RecycleAct.class);
            intent.putExtra("paths",teleopLayoutBuilder.getPaths());
            intent.putExtra("mode",mode);
            Log.e("preeses","pressed");
            startActivity(intent);
        }
    }


    @Override
    public void onScrollChanged() {
        Log.e("scrolling","yes");
        View view = scrollView.getChildAt(scrollView.getChildCount() - 1);
        int topDetector = scrollView.getScrollY();
        Log.e("scrollheight",linearLayout.getHeight()+"");
        int bottomDetector = view.getBottom() - (scrollView.getHeight() + scrollView.getScrollY());
        if(bottomDetector == 0 ){
            didEND = true;
        }
    }
}