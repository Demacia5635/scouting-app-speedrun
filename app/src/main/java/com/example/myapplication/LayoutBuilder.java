package com.example.myapplication;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Map;

public class LayoutBuilder {
    private ArrayList<qual> paths;
    private String dataBegin;
    private String dataEnd;
    private LinearLayout screenLayout;
    private Context ActivityContext;
    private int pathindex;

    private ArrayList<String> counterViewNames;
    private ArrayList<EditText> counterView;

    private ArrayList<String> edittextViewNames;
    private ArrayList<EditText> editTexts ;

    private ArrayList<String> sliderViewNames;
    private ArrayList<SeekBar> seekBars;
    private ArrayList<TextView> sliders;

    private ArrayList<String> checkboxeViewNames;
    private ArrayList<CheckBox> checkBoxes;
    private String gameStage;

    private FirebaseFirestore db;

    public LayoutBuilder(ArrayList<qual> paths, String dataBegin, String dataEnd, LinearLayout screenLayout, Context ActivityContext,String gameStage, int pathindex) {
        this.gameStage = gameStage;
        this.dataBegin = dataBegin;
        this.dataEnd = dataEnd;
        this.ActivityContext = ActivityContext;
        this.paths = new ArrayList<>(paths);
        this.screenLayout = screenLayout;
        this.pathindex = pathindex;
        this.counterViewNames = new ArrayList<>();
        this.counterView = new ArrayList<>();
        this.edittextViewNames = new ArrayList<>();
        this.editTexts = new ArrayList<>();
        this.sliderViewNames = new ArrayList<>();
        this.seekBars = new ArrayList<>();
        this.sliders = new ArrayList<>();
        this.checkboxeViewNames = new ArrayList<>();
        this.checkBoxes = new ArrayList<>();
        this.db = FirebaseFirestore.getInstance();
    }






    private void addCheckboxView(Map<String, Object> map){
        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView t3 = new TextView(this.ActivityContext.getApplicationContext());
        t3.setText(map.get("displayName").toString());
        t3.setLayoutParams(params4);
        t3.setTextColor(Color.parseColor(map.get("color")+""));
        CheckBox cb = new CheckBox(this.ActivityContext.getApplicationContext());
        cb.setButtonTintList(ColorStateList.valueOf(Color.parseColor(map.get("color")+"")));
        t3.setPadding(0,100,0,0);
        cb.setLayoutParams(params4);
        this.screenLayout.addView(t3);
        this.screenLayout.addView(cb);
        try {
            cb.setChecked(map.get("defaultValue").toString() == "1");
        }catch (Exception e){

        }
        this.checkboxeViewNames.add(map.get("name").toString());

        this.checkBoxes.add(cb);
    }
    private void addCounterView(Map<String, Object> map){
        LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params52 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params52.weight = 5;
        TextView t4 = new TextView(this.ActivityContext.getApplicationContext());
        LinearLayout l = new LinearLayout(this.ActivityContext.getApplicationContext());
        l.setWeightSum(5);
        l.setLayoutParams(params6);
        t4.setText(map.get("displayName").toString());
        t4.setTextColor(Color.parseColor(map.get("color")+""));
        t4.setLayoutParams(params5);
        EditText np = new EditText(this.ActivityContext.getApplicationContext());
        np.setGravity(Gravity.CENTER_HORIZONTAL);
        np.setLayoutParams(params52);
        np.setText("1");
        np.setInputType(InputType.TYPE_CLASS_NUMBER);
        t4.setPadding(0,100,0,0);
        l.setPadding(50,0,50,0);
        Button minus =  new Button(this.ActivityContext.getApplicationContext());
        minus.setText("-");
        minus.setLayoutParams(params5);
        Button plus = new Button(this.ActivityContext.getApplicationContext());
        plus.setText("+");
        plus.setTextColor(Color.parseColor(map.get("color")+""));
        minus.setTextColor(Color.parseColor(map.get("color")+""));
        np.setTextColor(Color.parseColor(map.get("color")+""));                l.addView(plus);
        l.addView(np);
        l.addView(minus);
        this.screenLayout.addView(t4);
        this.screenLayout.addView(l);
        this.counterView.add(np);
        this.counterViewNames.add(map.get("name").toString());
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
    }
    private void addEditTextView(Map<String, Object> map){
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView t2 = new TextView(this.ActivityContext.getApplicationContext());
        t2.setText(map.get("displayName").toString());
        t2.setLayoutParams(params3);
        t2.setTextColor(Color.parseColor(map.get("color")+""));
        EditText editText = new EditText(this.ActivityContext.getApplicationContext());
        editText.setHint("enter text");
        editText.setBackgroundColor(Color.parseColor(map.get("color")+""));
        editText.setLayoutParams(params3);
        t2.setPadding(0,100,0,0);
        this.screenLayout.addView(t2);
        this.screenLayout.addView(editText);
        try {editText.setText(map.get("defaultValue")+"");
        }catch (Exception e){
        }
        this.edittextViewNames.add(map.get("name").toString());

        this.editTexts.add(editText);
    }
    private void addSliderView(Map<String, Object> map){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TextView t1 = new TextView(this.ActivityContext.getApplicationContext());
        t1.setText(map.get("displayName").toString());
        t1.setTextColor(Color.parseColor(map.get("color")+""));
        SeekBar slider = new SeekBar(this.ActivityContext.getApplicationContext());
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
        TextView slidervalue = new TextView(this.ActivityContext.getApplicationContext());
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
        this.screenLayout.addView(t1);
        this.screenLayout.addView(slidervalue);
        this.screenLayout.addView(slider);
        this.sliders.add(slidervalue);
        this.seekBars.add(slider);
        try {slider.setProgress((Integer.parseInt(map.get("defaultValue").toString())));
            slidervalue.setText(map.get("defaultValue")+"");
        }catch (Exception e){
        }
        this.sliderViewNames.add(map.get("name").toString());
        if(map.get("name").toString().equals("placed_game_piece")){
            Log.e("wtf","wtf");
        }
    }
    private void getpreviousdata(){
        String data = Constants.valueseprator+paths.get(pathindex).getPath().substring(paths.get(pathindex).getPath().indexOf(this.dataBegin)+this.dataBegin.length(),paths.get(pathindex).getPath().indexOf(this.dataEnd));
        Log.e("dodod",data);
        Log.e("dodosize",data.length()+"");

        if(data.length()>0){
            Log.e("loop","entered");
            Log.e("loop numbersize",this.counterViewNames.size()+"");
            for (int i = 0; i < this.counterViewNames.size(); i++) {
                Log.e("number",this.counterViewNames.get(i));
                if(data.contains(this.counterViewNames.get(i))){
                    String temp = data.substring(data.indexOf(Constants.valueseprator+this.counterViewNames.get(i)+Constants.nameseprator)+this.counterViewNames.get(i).length()+Constants.nameseprator.length()+Constants.valueseprator.length());
                    Log.e("datavalue",temp);
                    try {
                        this.counterView.get(i).setText(Integer.parseInt(temp.substring(0,temp.indexOf(Constants.valueseprator)))+"");

                    }catch (Exception e){}
                }
            }
            for (int i = 0; i < this.checkboxeViewNames.size(); i++) {
                if(data.contains(this.checkboxeViewNames.get(i))){
                    String temp = data.substring(data.indexOf(Constants.valueseprator+this.checkboxeViewNames.get(i)+Constants.nameseprator)+this.checkboxeViewNames.get(i).length()+Constants.nameseprator.length()+Constants.valueseprator.length());
                    this.checkBoxes.get(i).setChecked(Boolean.parseBoolean(temp.substring(0,temp.indexOf(Constants.valueseprator))));
                }
            }
            for (int i = 0; i < this.edittextViewNames.size(); i++) {
                if(data.contains(this.edittextViewNames.get(i))){
                    String temp = data.substring(data.indexOf(Constants.valueseprator+this.edittextViewNames.get(i)+Constants.nameseprator)+this.edittextViewNames.get(i).length()+Constants.nameseprator.length()+Constants.valueseprator.length());
                    this.editTexts.get(i).setText(temp.substring(0,temp.indexOf(Constants.valueseprator)));
                }
            }
            for (int i = 0; i < this.sliderViewNames.size(); i++) {
                if(data.contains(this.sliderViewNames.get(i))){
                    String temp = data.substring(data.indexOf(Constants.valueseprator+this.sliderViewNames.get(i)+Constants.nameseprator)+this.sliderViewNames.get(i).length()+Constants.nameseprator.length()+Constants.valueseprator.length());
                    this.sliders.get(i).setText(temp.substring(0,temp.indexOf(Constants.valueseprator)));
                    try {
                        this.seekBars.get(i).setProgress(Integer.parseInt(temp.substring(0,temp.indexOf(Constants.valueseprator))));
                    }catch (Exception e){}
                }
            }
        }
    }
    public void addViewsToLinearLayout(){
        DocumentReference docRef = db.collection("seasons/2023/data-params").document(this.gameStage);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document = task.getResult();
                    if(document.exists()){
                        Map<String,Object> params = document.getData();
                        ArrayList<Map<String,Object>> FilteredByWeight = new ArrayList<>();
                        for(Map.Entry<String, Object> entry : params.entrySet()){
                            FilteredByWeight.add(((Map<String,Object>) entry.getValue()));
                        }
                        for(Map.Entry<String, Object> entry : params.entrySet()){
                            FilteredByWeight.set(Integer.parseInt(((Map<String, Object>) entry.getValue()).get("weight").toString())-1,((Map<String,Object>) entry.getValue()));
                        }
                        for (Map<String,Object> data:FilteredByWeight) {
                            addviewfrommap((data));
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
                addSliderView(map);
                break;
            case "text":
                addEditTextView(map);
                break;
            case "checkbox":
                addCheckboxView(map);
                break;
            case "number":
                addCounterView(map);
                break;
        }


    }
    private void writeToInternal(String filename){
        String thingsTosave = "";
        for (qual thingtoSave : this.paths ){
            thingsTosave += thingtoSave.getPath();
        }
        try {
            FileOutputStream fOut = ActivityContext.openFileOutput(this.paths.get(pathindex).getMode()+"scoutersavedata.txt", Context.MODE_PRIVATE);
            fOut.write(thingsTosave.getBytes());
            fOut.close();
        }catch (Exception e){
            Toast.makeText(this.ActivityContext, "Internal storage error please contact suprevisor error details: "+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    public void updateData(){
        String datastring="";

        for (int i = 0; i < this.counterViewNames.size(); i++) {
            datastring+=this.counterViewNames.get(i)+Constants.nameseprator;
            datastring+=this.counterView.get(i).getText() + Constants.valueseprator;
        }
        for (int i = 0; i < this.checkboxeViewNames.size(); i++) {
            datastring+=this.checkboxeViewNames.get(i)+Constants.nameseprator;
            datastring+=this.checkBoxes.get(i).isChecked()+ Constants.valueseprator;
        }
        for (int i = 0; i < this.edittextViewNames.size(); i++) {
            datastring+=this.edittextViewNames.get(i)+Constants.nameseprator;
            datastring+=this.editTexts.get(i).getText().toString()+ Constants.valueseprator;
        }
        for (int i = 0; i < this.sliderViewNames.size(); i++) {
            datastring+=this.sliderViewNames.get(i)+Constants.nameseprator;
            datastring+=this.sliders.get(i).getText()+ Constants.valueseprator;
        }
        int dataindex = paths.get(this.pathindex).getPath().indexOf(this.dataBegin)+this.dataBegin.length();
        String pathwithdata = paths.get(pathindex).getPath().substring(0,dataindex)+datastring+
                paths.get(pathindex).getPath().substring(paths.get(pathindex).getPath().indexOf(this.dataEnd));
        qual updatequal = new qual(pathwithdata,paths.get(pathindex).getMode());
        paths.set(pathindex,updatequal);
        writeToInternal("scoutersavedata.txt");
    }
    public ArrayList<String> getPaths(){
        ArrayList<String> updatedPaths = new ArrayList<>();
        for (qual q1: this.paths){
            updatedPaths.add(q1.getPath());
        }

        return updatedPaths;
    }



}
