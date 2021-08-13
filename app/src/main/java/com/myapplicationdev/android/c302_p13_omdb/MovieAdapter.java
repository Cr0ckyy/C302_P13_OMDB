package com.myapplicationdev.android.c302_p13_omdb;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MovieAdapter extends ArrayAdapter<Movie> {

    ArrayList<Movie> list;
     Context context;

    public MovieAdapter(Context context, int resource, ArrayList<Movie> objects){
        super(context, resource, objects);
        list = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.movie_row, parent, false);

        TextView tvTitle = rowView.findViewById(R.id.tvTitle);
        TextView tvReleased = rowView.findViewById(R.id.tvReleased);
        ImageView img = rowView.findViewById(R.id.img);

        Movie movie = list.get(position);
        tvTitle.setText(movie.getTitle());
        tvReleased.setText(movie.getReleased());
        Picasso.get().load(movie.getPoster()).resize(50,50).into(img);


        return rowView;
    }

}