package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
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

import java.util.ArrayList;
import java.util.Map;

public class TeleopActivity extends AppCompatActivity implements View.OnClickListener {
    LinearLayout linearLayout;
    ArrayList<String> numbernames = new ArrayList<>();
    ArrayList<String> slidernames = new ArrayList<>();
    ArrayList<String> edittextsnames = new ArrayList<>();
    ArrayList<String> checkboxesnames = new ArrayList<>();
    ArrayList<NumberPicker> numberInputs = new ArrayList<>();
    ArrayList<Slider> sliders = new ArrayList<>();
    ArrayList<EditText> editTexts = new ArrayList<>();
    ArrayList<CheckBox> checkBoxes = new ArrayList<>();
    ArrayList<String> paths = new ArrayList<String>();
    int index;
    Button prev;
    Button next;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String databegin = "/endauto/";
    String dataEnd = "/endtele/";

    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teleop);
        linearLayout = findViewById(R.id.linearlayoutteled);
        addViewsToLinearLayout();
//        getpreviousdata();
        Intent intent = getIntent();
        for(String path : intent.getExtras().getStringArrayList("paths")){
            paths.add(path);
        }
        index = intent.getExtras().getInt("index");
        TextView match = findViewById(R.id.qualtele);
        String qualssubpath = paths.get(index).substring(paths.get(index).indexOf("ISDE2"),paths.get(index).length());
        qualssubpath = qualssubpath.substring(qualssubpath.indexOf("/")+1,qualssubpath.length());
        qualssubpath = qualssubpath.substring(qualssubpath.indexOf("/")+1,qualssubpath.length());
        qualssubpath = qualssubpath.substring(0,qualssubpath.indexOf("data:"));
        match.setText(qualssubpath);
        prev = findViewById(R.id.prevauto);
        next = findViewById(R.id.nextendgame);
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
                            Toast.makeText(TeleopActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void addViewsToLinearLayout(){
        DocumentReference docRef = db.collection("seasons/2022/data-params").document("teleop");
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
                Slider slider = new Slider(getApplicationContext());
                slider.setThumbTintList(ColorStateList.valueOf(Color.parseColor(map.get("color")+"")));
                slider.setTrackTintList(ColorStateList.valueOf(Color.parseColor(map.get("color")+"")));
                slider.setValueFrom((Float) map.get("min"));
                slider.setValueTo((Float) map.get("max"));
                t1.setLayoutParams(params);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
                slider.setLayoutParams(params2);
                linearLayout.addView(t1);
                linearLayout.addView(slider);
                sliders.add(slider);
                try {slider.setValue((Float) map.get("defaultValue"));
                }catch (Exception e){
                }
                slidernames.add(map.get("name").toString());


                break;
            case "text":
                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView t2 = new TextView(getApplicationContext());
                t2.setText(map.get("displayName").toString());
                t2.setLayoutParams(params3);
                EditText editText = new EditText(getApplicationContext());
                editText.setHint("enter text");
                editText.setBackgroundColor(Color.parseColor(map.get("color")+""));
                editText.setLayoutParams(params3);
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
                CheckBox cb = new CheckBox(getApplicationContext());
                cb.setButtonTintList(ColorStateList.valueOf(Color.parseColor(map.get("color")+"")));
                cb.setTextColor(Color.parseColor(map.get("color")+""));
                cb.setText(map.get("displayName").toString());
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
                t4.setLayoutParams(params5);
                NumberPicker np = new NumberPicker(getApplicationContext());
                np.setLayoutParams(params5);
                np.setMaxValue(Integer.parseInt(map.get("max").toString()));
                np.setMinValue(Integer.parseInt(map.get("min").toString()));
                linearLayout.addView(np);
                numberInputs.add(np);
                numbernames.add(map.get("name").toString());

                break;
        }


    }
    private void getpreviousdata(){
        String datalength = databegin;
        String data = paths.get(index).substring(paths.get(index).indexOf(datalength)+datalength.length(),paths.get(index).indexOf(dataEnd));
        if(data.length()>=0){
            for (int i = 0; i < numbernames.size(); i++) {
                if(data.contains(numbernames.get(i))){
                    String temp = data.substring(data.indexOf(numbernames.get(i))+numbernames.get(i).length()+1);
                    numberInputs.get(i).setValue(Integer.parseInt(temp.substring(0,temp.indexOf("/de"))));
                }
            }
            for (int i = 0; i < checkboxesnames.size(); i++) {
                if(data.contains(checkboxesnames.get(i))){
                    String temp = data.substring(data.indexOf(checkboxesnames.get(i))+checkboxesnames.get(i).length()+1);
                    checkBoxes.get(i).setChecked(Boolean.parseBoolean(temp.substring(0,temp.indexOf("/de"))));
                }
            }
            for (int i = 0; i < edittextsnames.size(); i++) {
                if(data.contains(edittextsnames.get(i))){
                    String temp = data.substring(data.indexOf(edittextsnames.get(i))+edittextsnames.get(i).length()+1);
                    editTexts.get(i).setText(temp.substring(0,temp.indexOf("/de")));
                }
            }
            for (int i = 0; i < slidernames.size(); i++) {
                if(data.contains(slidernames.get(i))){
                    String temp = data.substring(data.indexOf(slidernames.get(i))+slidernames.get(i).length()+1);
                    sliders.get(i).setValue(Integer.parseInt(temp.substring(0,temp.indexOf("/de"))));
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        String datastring="";

        for (int i = 0; i < numbernames.size(); i++) {
            datastring+=numbernames.get(i)+":";
            datastring+=numberInputs.get(i).getValue() + "/de";
        }
        for (int i = 0; i < checkboxesnames.size(); i++) {
            datastring+=checkboxesnames.get(i)+":";
            datastring+=checkBoxes.get(i).isChecked()+ "/de";
        }
        for (int i = 0; i < edittextsnames.size(); i++) {
            datastring+=edittextsnames.get(i)+":";
            datastring+=editTexts.get(i).getText().toString()+ "/de";
        }
        for (int i = 0; i < slidernames.size(); i++) {
            datastring+=slidernames.get(i)+":";
            datastring+=sliders.get(i).getValue()+ "/de";
        }
        String data = databegin;
        int dataindex = paths.get(index).indexOf(data)+data.length();
        String pathwithdata = paths.get(index).substring(0,dataindex)+datastring+paths.get(index).substring(paths.get(index).indexOf(dataEnd));
        paths.set(index,pathwithdata);
        if(view == next){
            Intent intent = new Intent(TeleopActivity.this,EndGame.class);
            intent.putExtra("paths",paths);
            intent.putExtra("index",index);
            startActivity(intent);
        } else if (view==prev) {
            Intent intent = new Intent(TeleopActivity.this,AutonomousActivity.class);
            intent.putExtra("paths",paths);
            intent.putExtra("index",index);
            startActivity(intent);
        }
    }
}