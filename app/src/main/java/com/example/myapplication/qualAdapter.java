package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class qualAdapter extends ArrayAdapter<qual> {

    private Context context;
    private List<qual> quals;


    public qualAdapter(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<qual> objects) {
        super(context, resource, textViewResourceId, objects);
        this.context = context;
        this.quals = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.quallayout,parent,false);
        TextView qualnum = view.findViewById(R.id.qualnum);
        TextView teamnum = view.findViewById(R.id.teamnum);
        qual qual = quals.get(position);
        qualnum.setText(qual.getQualsName());
        teamnum.setText("Team Number: "+qual.getTeamNumber());
        return  view;
    }
}
