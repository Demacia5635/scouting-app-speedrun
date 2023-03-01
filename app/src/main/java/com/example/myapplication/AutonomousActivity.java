package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;


import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
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

public class AutonomousActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linearLayout;
    ArrayList<String> numbernames = new ArrayList<>();
    ArrayList<String> slidernames = new ArrayList<>();
    ArrayList<String> edittextsnames = new ArrayList<>();
    ArrayList<String> checkboxesnames = new ArrayList<>();
    ArrayList<NumberPicker> numberInputs = new ArrayList<>();
    ArrayList<SeekBar> seekBars = new ArrayList<>();
    ArrayList<TextView> sliders = new ArrayList<>();
    ArrayList<EditText> editTexts = new ArrayList<>();
    ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    ArrayList<String> paths = new ArrayList<String>();
    int index;
    boolean isloogedout = false;
    Button prev;
    Button next;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String databegin = "data:";
    Boolean finisheddata = false;
    String dataEnd = "/endauto/";


    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonomous);
        linearLayout = findViewById(R.id.linearlayout);
        addViewsToLinearLayout();
        Intent intent = getIntent();

       for(String path : intent.getExtras().getStringArrayList("paths")){
           paths.add(path);
       }

        index = intent.getExtras().getInt("index");
        Log.e("INDEX", paths.get(index));
       TextView match = findViewById(R.id.qualauto);
       String qualssubpath = paths.get(index);
       String quals = "Quals";
       qualssubpath = qualssubpath.substring(qualssubpath.indexOf(quals)+quals.length()+1);
       qualssubpath = qualssubpath.substring(0,qualssubpath.indexOf("/"));
       String team = paths.get(index).substring(paths.get(index).indexOf(qualssubpath)+qualssubpath.length()+1,paths.get(index).indexOf(qualssubpath)+qualssubpath.length()+5);
       String mode = "autonomous";
       match.setText(qualssubpath+" team: "+team+ " mode: "+mode);
       prev = findViewById(R.id.prevendgame);
       if(index == 0){
           prev.setText("Logout");
           isloogedout = true;
       }
       next = findViewById(R.id.nextteleop);
       prev.setOnClickListener(this);
       next.setOnClickListener(this);
        auth = FirebaseAuth.getInstance();
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
    private void addViewsToLinearLayout(){
        DocumentReference docRef = db.collection("seasons/2023/data-params").document("autonomous");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Map<String,Object> params = document.getData();
                        for(Map.Entry<String, Object> entry : params.entrySet()){
                            addviewfrommap((Map<String, Object>) entry.getValue());
                        }
                        getpreviousdata();
                    }
                }
            }
        });
    }
    private void addviewfrommap(Map<String, Object> map){
         switch (map.get("type")+""){
             case "slider":
                 LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                 TextView t1 = new TextView(getApplicationContext());
                 t1.setText(map.get("displayName").toString());
                 t1.setTextColor(Color.parseColor(map.get("color")+""));
                 SeekBar slider = new SeekBar(getApplicationContext());
                 slider.setThumbTintList(ColorStateList.valueOf(Color.parseColor(map.get("color")+"")));
                 slider.setProgressTintList(ColorStateList.valueOf(Color.parseColor(map.get("color")+"")));
                 slider.setMin(Integer.parseInt(map.get("min").toString()));
                 slider.setMax(Integer.parseInt(map.get("max").toString()));
                 slider.setPadding(0,0,0,10);
                 slider.incrementProgressBy(Integer.parseInt(map.get("min").toString()));
                 t1.setLayoutParams(params);
                 LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                 slider.setLayoutParams(params2);
                 TextView slidervalue = new TextView(getApplicationContext());
                 slidervalue.setTextColor(Color.parseColor(map.get("color")+""));
                 SeekBar.OnSeekBarChangeListener abc = new SeekBar.OnSeekBarChangeListener() {

                     @Override
                     public void onStopTrackingTouch(SeekBar seekBar) {
                     }

                     @Override
                     public void onStartTrackingTouch(SeekBar seekBar) {
                     }

                     @Override
                     public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                         //Executed when progress is changed
                         slidervalue.setText(progress+"");
                     }
                 };
                 slider.setOnSeekBarChangeListener(abc);
                 linearLayout.addView(t1);
                 linearLayout.addView(slidervalue);
                 linearLayout.addView(slider);
                 sliders.add(slidervalue);
                 seekBars.add(slider);
                 try {slider.setProgress((Integer.parseInt(map.get("defaultValue").toString())));
                     slidervalue.setText(map.get("defaultValue")+"");
                 }catch (Exception e){
                 }
                 slidernames.add(map.get("name").toString());




                 break;
             case "text":
                 LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                 TextView t2 = new TextView(getApplicationContext());
                 t2.setText(map.get("displayName").toString());
                 t2.setLayoutParams(params3);
                 t2.setTextColor(Color.parseColor(map.get("color")+""));
                 EditText editText = new EditText(getApplicationContext());
                 editText.setHint("enter text");
                 editText.setBackgroundColor(Color.parseColor(map.get("color")+""));
                 editText.setLayoutParams(params3);
                 editText.setPadding(0,0,0,10);
                 linearLayout.addView(t2);
                 linearLayout.addView(editText);
                 try {editText.setText(map.get("defaultValue")+"");
                 }catch (Exception e){
                 }
                 edittextsnames.add(map.get("name").toString());

                 editTexts.add(editText);
                 break;
             case "checkbox":
                 LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                 TextView t3 = new TextView(getApplicationContext());
                 t3.setText(map.get("displayName").toString());
                 t3.setLayoutParams(params4);
                 t3.setTextColor(Color.parseColor(map.get("color")+""));
                 CheckBox cb = new CheckBox(getApplicationContext());
                 cb.setButtonTintList(ColorStateList.valueOf(Color.parseColor(map.get("color")+"")));
                 cb.setTextColor(Color.parseColor(map.get("color")+""));
                 cb.setText(map.get("displayName").toString());
                 cb.setPadding(0,0,0,10);
                 linearLayout.addView(t3);
                 linearLayout.addView(cb);
                 try {
                     cb.setChecked(map.get("defaultValue").toString() == "1");
                 }catch (Exception e){

                 }
                 checkboxesnames.add(map.get("name").toString());

                 checkBoxes.add(cb);
                 break;
             case "number":
                 LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                 TextView t4 = new TextView(getApplicationContext());
                 t4.setText(map.get("displayName").toString());
                 t4.setTextColor(Color.parseColor(map.get("color")+""));
                 t4.setLayoutParams(params5);
                 NumberPicker np = new NumberPicker(getApplicationContext());
                 np.setLayoutParams(params5);
                 np.setMaxValue(Integer.parseInt(map.get("max").toString()));
                 np.setMinValue(Integer.parseInt(map.get("min").toString()));
                 np.setPadding(0,0,0,10);
                 linearLayout.addView(t4);
                 linearLayout.addView(np);
                 String temp = map.get("name").toString();
                 Log.e("numbername: ",temp);
                 numberInputs.add(np);
                 numbernames.add(temp);

                 break;
         }


    }


    @Override
    public void onClick(View view) {
        String datastring="";

        for (int i = 0; i < numbernames.size(); i++) {
            datastring+=numbernames.get(i)+"//://";
            datastring+=numberInputs.get(i).getValue() + "/de";
        }
        for (int i = 0; i < checkboxesnames.size(); i++) {
            datastring+=checkboxesnames.get(i)+"//://";
            datastring+=checkBoxes.get(i).isChecked()+ "/de";
        }
        for (int i = 0; i < edittextsnames.size(); i++) {
            datastring+=edittextsnames.get(i)+"//://";
            datastring+=editTexts.get(i).getText().toString()+ "/de";
        }
        for (int i = 0; i < slidernames.size(); i++) {
            datastring+=slidernames.get(i)+"//://";
            datastring+=sliders.get(i).getText()+ "/de";
        }
        String data = databegin;
        int dataindex = paths.get(index).indexOf(data)+data.length();
        String pathwithdata = paths.get(index).substring(0,dataindex)+datastring+paths.get(index).substring(paths.get(index).indexOf(dataEnd));
        paths.set(index,pathwithdata);
        writeToInternal("scoutersavedata.txt");
        if(view == next){
            Intent intent = new Intent(AutonomousActivity.this,TeleopActivity.class);
            intent.putExtra("paths",paths);
            intent.putExtra("index",index);
            startActivity(intent);
        } else if (view==prev) {
            if(isloogedout){
                Intent intent = new Intent(AutonomousActivity.this,LoginActivity.class);
                try {
                    FileOutputStream fOut = openFileOutput("scoutersavedata.txt", Context.MODE_PRIVATE);
                    String thingsToDelete = "";
                    fOut.write(thingsToDelete.getBytes());
                    fOut.close();
                }catch (Exception e){
                    Toast.makeText(this, "Internal storage error please contact suprevisor error details: "+e.getMessage(), Toast.LENGTH_LONG).show();
                }
                startActivity(intent);
            }else {
            index--;
            Intent intent = new Intent(AutonomousActivity.this,EndGame.class);
            intent.putExtra("paths",paths);
            intent.putExtra("index",index);
            startActivity(intent);}
        }
    }
    private void getpreviousdata(){
        String datalength = databegin;
        String colondash = "//://";
        String data = paths.get(index).substring(paths.get(index).indexOf(datalength)+datalength.length(),paths.get(index).indexOf(dataEnd));
        Log.e("dodosize",data.length()+"");

        if(data.length()>0){
            Log.e("loop","entered");
            Log.e("loop numbersize",numbernames.size()+"");
        for (int i = 0; i < numbernames.size(); i++) {
            Log.e("number",numbernames.get(i));
            if(data.contains(numbernames.get(i))){
                String temp = data.substring(data.indexOf(numbernames.get(i))+numbernames.get(i).length()+colondash.length());
                Log.e("datavalue",temp);
                try {
                    numberInputs.get(i).setValue(Integer.parseInt(temp.substring(0,temp.indexOf("/de"))));

                }catch (Exception e){}
            }
        }
        for (int i = 0; i < checkboxesnames.size(); i++) {
            if(data.contains(checkboxesnames.get(i))){
                String temp = data.substring(data.indexOf(checkboxesnames.get(i))+checkboxesnames.get(i).length()+colondash.length());
                checkBoxes.get(i).setChecked(Boolean.parseBoolean(temp.substring(0,temp.indexOf("/de"))));
            }
        }
        for (int i = 0; i < edittextsnames.size(); i++) {
            if(data.contains(edittextsnames.get(i))){
                String temp = data.substring(data.indexOf(edittextsnames.get(i))+edittextsnames.get(i).length()+colondash.length());
                editTexts.get(i).setText(temp.substring(0,temp.indexOf("/de")));
            }
        }
        for (int i = 0; i < slidernames.size(); i++) {
            if(data.contains(slidernames.get(i))){
                String temp = data.substring(data.indexOf(slidernames.get(i))+slidernames.get(i).length()+colondash.length());
                sliders.get(i).setText(temp.substring(0,temp.indexOf("/de")));
                try {
                    seekBars.get(i).setProgress(Integer.parseInt(temp.substring(0,temp.indexOf("/de"))));
                }catch (Exception e){}
            }
        }
        }
    }
    public void writeToInternal(String filename){
        String thingsTosave = "";
        for (String thingtoSave : paths ){
            thingsTosave += thingtoSave;
        }
        try {
            FileOutputStream fOut = openFileOutput("scoutersavedata.txt", Context.MODE_PRIVATE);
            fOut.write(thingsTosave.getBytes());
            fOut.close();
        }catch (Exception e){
            Toast.makeText(this, "Internal storage error please contact suprevisor error details: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
}