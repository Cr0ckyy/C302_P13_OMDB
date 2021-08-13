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

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Movie> list;
    MovieAdapter adapter;
    final String TAG = "MainActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listViewMovies);
        list = new ArrayList<>();
        adapter = new MovieAdapter(this, R.layout.movie_row, list);
        listView.setAdapter(adapter);
        sharedPreferences = getSharedPreferences("C302_P13", Context.MODE_PRIVATE);

        db.collection("movies")
                .addSnapshotListener((QuerySnapshot value, FirebaseFirestoreException e) -> {

                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e);
                        return;
                    }
                    list.clear();

                    assert value != null;
                    for (QueryDocumentSnapshot doc : value) {
                        Movie movie = doc.toObject(Movie.class);
                        movie.setMovieId(doc.getId());
                        list.add(movie);
                    }
                    adapter.notifyDataSetChanged();
                });

        listView.setOnItemClickListener((AdapterView<?> parent, View view, int position, long id) -> {

            Movie selectedContact = list.get(position);
            Intent i = new Intent(getBaseContext(), ViewMovieDetailsActivity.class);
            i.putExtra("movie_id", selectedContact.getMovieId());
            startActivity(i);

        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sharedPreferences.getString("apikey", "").isEmpty())
            finish();
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
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            Intent intent = new Intent(getApplicationContext(), CreateMovieActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}