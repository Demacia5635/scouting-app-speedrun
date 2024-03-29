package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;


import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
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

import org.w3c.dom.Text;

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
    ArrayList<EditText> numberInputs = new ArrayList<>();
    ArrayList<SeekBar> seekBars = new ArrayList<>();
    ArrayList<TextView> sliders = new ArrayList<>();
    ArrayList<EditText> editTexts = new ArrayList<>();
    ArrayList<CheckBox> checkBoxes = new ArrayList<>();

    ArrayList<String> paths = new ArrayList<String>();
    int index;
    boolean isloogedout = false;
    Button prev, next,gotoquals;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String databegin = "data:";
    String valueend = "/de";
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
        gotoquals = findViewById(R.id.fromautotoquals);
        index = intent.getExtras().getInt("index");
        TextView mode = findViewById(R.id.qualauto3);
        TextView team = findViewById(R.id.qualauto2);
        TextView match = findViewById(R.id.qualauto1);
       String qualssubpath = paths.get(index);
       String quals = "Quals";
       qualssubpath = qualssubpath.substring(qualssubpath.indexOf(quals)+quals.length()+1);
       qualssubpath = qualssubpath.substring(0,qualssubpath.indexOf("/"));
       String teamS = paths.get(index).substring(paths.get(index).indexOf(qualssubpath)+qualssubpath.length()+1,paths.get(index).indexOf(qualssubpath)+qualssubpath.length()+5);
       String modeS = "autonomous";
       match.setText(qualssubpath);
       mode.setText(modeS);
       team.setText("Team: "+teamS);
       prev = findViewById(R.id.prevendgame);
       if(index == 0){
           prev.setText("Logout");
           isloogedout = true;
       }
       next = findViewById(R.id.nextteleop);
        gotoquals.setOnClickListener(this);
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
                t1.setPadding(0,100,0,0);
                slider.setPadding(75,0,75,0);
                slider.incrementProgressBy(Integer.parseInt(map.get("min").toString()));
                t1.setLayoutParams(params);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                slider.setLayoutParams(params2);
                TextView slidervalue = new TextView(getApplicationContext());
                slidervalue.setTextColor(Color.parseColor(map.get("color")+""));
                slidervalue.setLayoutParams(params);
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
                if(map.get("name").toString().equals("placed_game_piece")){
                    Log.e("wtf","wtf");
                }



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
                t2.setPadding(0,100,0,0);
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
                t3.setPadding(0,100,0,0);
                cb.setLayoutParams(params4);
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
                LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params52 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params52.weight = 5;
                TextView t4 = new TextView(getApplicationContext());
                LinearLayout l = new LinearLayout(getApplicationContext());
                l.setWeightSum(5);
                l.setLayoutParams(params6);
                t4.setText(map.get("displayName").toString());
                t4.setTextColor(Color.parseColor(map.get("color")+""));
                t4.setLayoutParams(params5);
                EditText np = new EditText(getApplicationContext());
                np.setGravity(Gravity.CENTER_HORIZONTAL);
                np.setLayoutParams(params52);
                np.setText("1");
                np.setInputType(InputType.TYPE_CLASS_NUMBER);
                t4.setPadding(0,100,0,0);
                l.setPadding(50,0,50,0);
                Button minus =  new Button(getApplicationContext());
                minus.setText("-");
                minus.setLayoutParams(params5);
                Button plus = new Button(getApplicationContext());
                plus.setText("+");
                plus.setTextColor(Color.parseColor(map.get("color")+""));
                minus.setTextColor(Color.parseColor(map.get("color")+""));
                np.setTextColor(Color.parseColor(map.get("color")+""));                l.addView(plus);
                l.addView(np);
                l.addView(minus);
                linearLayout.addView(t4);
                linearLayout.addView(l);
                numberInputs.add(np);
                numbernames.add(map.get("name").toString());
                plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Integer.parseInt(np.getText()+"") < Integer.parseInt(map.get("max").toString())){
                            int num = Integer.parseInt(np.getText().toString())+1;
                            Log.e("deez",num+"");
                            np.setText(""+num);
                        }
                    }
                });
                plus.setLayoutParams(params5);

                minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(Integer.parseInt(np.getText()+"") > 0){

                            np.setText((Integer.parseInt(np.getText()+"")-1)+"");
                        }
                    }
                });
                np.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        if(!charSequence.toString().equals("")){
                            int value = Integer.parseInt(charSequence.toString());
                            if(value>Integer.parseInt(map.get("max").toString())){
                                np.setText(map.get("max").toString());
                            } else if (value<Integer.parseInt(map.get("min").toString())) {
                                np.setText(map.get("min").toString());
                            }
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });

                break;
        }


    }


    @Override
    public void onClick(View view) {
        String datastring="";

        for (int i = 0; i < numbernames.size(); i++) {
            datastring+=numbernames.get(i)+"//://";
            datastring+=numberInputs.get(i).getText() + "/de";
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
            if(slidernames.get(i).equals("om")){
                Log.e("maybe","aaaa");
            }
            datastring+=slidernames.get(i)+"//://";
            Log.e("size",slidernames.size()+"");
            Log.e("index",i+"");
            Log.e("names",slidernames.get(i));
            datastring+=sliders.get(i).getText()+ "/de";
        }
        Log.e("who",datastring);
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
        }else if(view == gotoquals){
            Intent intent = new Intent(AutonomousActivity.this,RecycleAct.class);
            intent.putExtra("paths",paths);
            startActivity(intent);
        }
    }
    private void getpreviousdata(){
        String datalength = databegin;
        String colondash = "//://";
        String data = "/de"+paths.get(index).substring(paths.get(index).indexOf(datalength)+datalength.length(),paths.get(index).indexOf(dataEnd));
//         data = data.replaceFirst(databegin,data+valueend);
         Log.e("dodod",data);
        Log.e("dodosize",data.length()+"");

        if(data.length()>0){
            Log.e("loop","entered");
            Log.e("loop numbersize",numbernames.size()+"");
        for (int i = 0; i < numbernames.size(); i++) {
            Log.e("number",numbernames.get(i));
            if(data.contains(numbernames.get(i))){
                String temp = data.substring(data.indexOf(valueend+numbernames.get(i)+colondash)+numbernames.get(i).length()+colondash.length()+valueend.length());
                Log.e("datavalue",temp);
                try {
                    numberInputs.get(i).setText(Integer.parseInt(temp.substring(0,temp.indexOf("/de")))+"");

                }catch (Exception e){}
            }
        }
        for (int i = 0; i < checkboxesnames.size(); i++) {
            if(data.contains(checkboxesnames.get(i))){
                String temp = data.substring(data.indexOf(valueend+checkboxesnames.get(i)+colondash)+checkboxesnames.get(i).length()+colondash.length()+valueend.length());
                checkBoxes.get(i).setChecked(Boolean.parseBoolean(temp.substring(0,temp.indexOf("/de"))));
            }
        }
        for (int i = 0; i < edittextsnames.size(); i++) {
            if(data.contains(edittextsnames.get(i))){
                String temp = data.substring(data.indexOf(valueend+edittextsnames.get(i)+colondash)+edittextsnames.get(i).length()+colondash.length()+valueend.length());
                editTexts.get(i).setText(temp.substring(0,temp.indexOf("/de")));
            }
        }
        for (int i = 0; i < slidernames.size(); i++) {
            if(data.contains(slidernames.get(i))){
                String temp = data.substring(data.indexOf(valueend+slidernames.get(i)+colondash)+slidernames.get(i).length()+colondash.length()+valueend.length());
                if(temp.equals("om//://0")){
                    Log.e("stam",temp);
                }
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