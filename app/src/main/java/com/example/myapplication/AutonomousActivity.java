package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.Slider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class AutonomousActivity extends AppCompatActivity {
    LinearLayout linearLayout;
    ArrayList<Button> numberinputs;
    ArrayList<Slider> sliders;
    ArrayList<EditText> Text;
    ArrayList<CheckBox> checkBoxes;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autonomous);
        linearLayout = findViewById(R.id.linearlayout);

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

                        }
                    }
                }
            }
        });
    }
    private void addviewfrommap(Map<String, Object> map){
         switch (map.get("name")+""){
             case "slider":

                 Slider slider = new Slider(getApplicationContext());
                 slider.setBackgroundColor(Color.parseColor(map.get("color")+""));
                 slider.setValueFrom((Float) map.get("minvalue"));

                 break;
             case "text":
                 break;
             case "checkbox":
                 break;
             case "number":
                 break;
         }


    }


}