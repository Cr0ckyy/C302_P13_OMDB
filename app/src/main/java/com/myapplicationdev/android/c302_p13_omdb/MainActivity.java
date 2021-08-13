package com.myapplicationdev.android.c302_p13_omdb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ListView movieListView;
    ArrayList<Movie> movies;
    MovieAdapter movieAdapter;
    final String TAG = "MainActivity";
    FirebaseFirestore Firestore = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        movieListView = findViewById(R.id.listViewMovies);

        movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, R.layout.movie_row, movies);
        movieListView.setAdapter(movieAdapter);

        sharedPreferences = getSharedPreferences("C302_P13", Context.MODE_PRIVATE);

        Firestore.collection("movies")
                .addSnapshotListener((QuerySnapshot value, FirebaseFirestoreException e) -> {

                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    movies.clear();


                    for (QueryDocumentSnapshot doc : Objects.requireNonNull(value)) {
                        Movie movie = doc.toObject(Movie.class);
                        movie.setMovieId(doc.getId());
                        movies.add(movie);
                    }
                    movieAdapter.notifyDataSetChanged();
                });

        movieListView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {

            Movie selectedMovie = movies.get(position);
            intent = new Intent(getBaseContext(), ViewMovieDetailsActivity.class);
            intent.putExtra("movie_id", selectedMovie.getMovieId());
            startActivity(intent);

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getString("apikey", "").isEmpty()) {
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int itemId = item.getItemId();


        if (itemId == R.id.menu_add) {
            Intent intent = new Intent(getApplicationContext(), CreateMovieActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}