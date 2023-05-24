package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

public class RecycleAct extends AppCompatActivity implements AdapterView.OnItemClickListener, View.OnClickListener {
    ListView listView;
    private Button changeToPractice , changeToQuals , changeToPlayoffs;
    ArrayList<String> paths = new ArrayList<String>();

    private qualAdapter adapter;
    private String mode;
    private ArrayList<qual> qualArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);
        listView = findViewById(R.id.rcycleView);
        qualArrayList = new ArrayList<>();
        changeToPlayoffs = findViewById(R.id.Toplayoffs);
        changeToPractice = findViewById(R.id.Topractice);
        changeToQuals = findViewById(R.id.ToQuals);
        changeToPlayoffs.setOnClickListener(this);changeToQuals.setOnClickListener(this);changeToPractice.setOnClickListener(this);
        adapter = new qualAdapter(this,0,0,qualArrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        Intent intent = getIntent();
        mode = intent.getExtras().getString("mode");
        Log.e("movedact","true");
        for(String path : intent.getExtras().getStringArrayList("paths")){
            paths.add(path);
        }
        adddata();

    }

    private void adddata() {
        for(String qual : paths){
            Log.e("path:",qual);
            qual qualdat = new qual(qual,mode);
            adapter.add(qualdat);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(RecycleAct.this,AutonomousActivity.class);
        intent.putExtra("paths",paths);
        intent.putExtra("index",i);
        intent.putExtra("mode",mode);
        startActivity(intent);

    }

    @Override
    public void onClick(View view) {
        if(view.equals(changeToPractice)){
            Intent intent = new Intent(RecycleAct.this,LoginActivity.class);
            intent.putExtra("prevmode",mode);
            intent.putExtra("reloaddata",false);
            intent.putExtra("mode","Practices");
            intent.putExtra("paths",paths);

            startActivity(intent);
        }else if(view.equals(changeToQuals)){
            Intent intent = new Intent(RecycleAct.this,LoginActivity.class);
            intent.putExtra("prevmode",mode);

            intent.putExtra("reloaddata",false);
            intent.putExtra("mode","Quals");
            intent.putExtra("paths",paths);

            startActivity(intent);
        } else if(view.equals(changeToPlayoffs)){
            Intent intent = new Intent(RecycleAct.this,LoginActivity.class);
            intent.putExtra("prevmode",mode);

            intent.putExtra("reloaddata",false);
            intent.putExtra("mode","Playoffs");
            intent.putExtra("paths",paths);


            startActivity(intent);
        }


    }
}