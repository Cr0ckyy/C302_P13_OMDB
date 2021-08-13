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

import cz.msebera.android.httpclient.util.TextUtils;

public class MovieAdapter extends ArrayAdapter<Movie> {

    TextView tvTitle, tvReleased;
    ArrayList<Movie> list;
    Context context;
    ImageView imageView;
    Movie movie;
    LayoutInflater inflater;

    public MovieAdapter(Context context, int resource, ArrayList<Movie> objects) {
        super(context, resource, objects);
        list = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        @SuppressLint("ViewHolder") View rowView = inflater.inflate(R.layout.movie_row, parent, false);

        tvTitle = rowView.findViewById(R.id.tvTitle);
        tvReleased = rowView.findViewById(R.id.tvReleased);
        imageView = rowView.findViewById(R.id.imageView);

        movie = list.get(position);

        tvTitle.setText(movie.getTitle());
        tvReleased.setText(movie.getReleased());

        if (TextUtils.isEmpty(movie.getPoster())) {
            // Load default image
            imageView.setImageResource(R.drawable.click_poster);
        } else {
            Picasso.get().load(movie.getPoster()).resize(50, 50).into(imageView);
        }


        return rowView;
    }

}