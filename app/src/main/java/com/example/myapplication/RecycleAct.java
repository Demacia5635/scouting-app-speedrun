package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class RecycleAct extends AppCompatActivity implements AdapterView.OnItemClickListener {
    ListView listView;
    ArrayList<String> paths = new ArrayList<String>();

    private qualAdapter adapter;
    private ArrayList<qual> qualArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycle);
        listView = findViewById(R.id.rcycleView);
        qualArrayList = new ArrayList<>();
        adapter = new qualAdapter(this,0,0,qualArrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        Intent intent = getIntent();
        Log.e("movedact","true");
        for(String path : intent.getExtras().getStringArrayList("paths")){
            paths.add(path);
        }
        adddata();

    }

    private void adddata() {
        for(String qual : paths){
            Log.e("path:",qual);
            qual qualdat = new qual(qual);
            adapter.add(qualdat);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(RecycleAct.this,AutonomousActivity.class);
        intent.putExtra("paths",paths);
        intent.putExtra("index",i);
        startActivity(intent);

    }
}