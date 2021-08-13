package com.myapplicationdev.android.c302_p13_omdb;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewMovieDetailsActivity extends AppCompatActivity {

    EditText etTitle, etRated, etReleased, etRuntime, etGenre, etActors, etPlot, etLanguage, etPoster;
    Button btnUpdate, btnDelete;
    String movieId;
    static final String TAG = "ViewMovieDetailsActivity";
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_movie_details);

        etTitle = findViewById(R.id.etTitle);
        etRated = findViewById(R.id.etRated);
        etReleased = findViewById(R.id.etReleased);
        etRuntime = findViewById(R.id.etRuntime);
        etGenre = findViewById(R.id.etGenre);
        etActors = findViewById(R.id.etActors);
        etPlot = findViewById(R.id.etPlot);
        etLanguage = findViewById(R.id.etLanguage);
        etPoster = findViewById(R.id.etPoster);
        btnUpdate = findViewById(R.id.btnUpdate);
        btnDelete = findViewById(R.id.btnDelete);

        Intent intent = getIntent();
        movieId = intent.getStringExtra("movie_id");
        DocumentReference documentReference = db.collection("movies").document(movieId);

        documentReference.get().addOnCompleteListener((Task<DocumentSnapshot> task) -> {
            if (task.isSuccessful()) {

                DocumentSnapshot document = task.getResult();

                if (document.exists()) {
                    etTitle.setText(document.getString("Title"));
                    etRated.setText(document.getString("Rated"));
                    etReleased.setText(document.getString("Released"));
                    etRuntime.setText(document.getString("Runtime"));
                    etGenre.setText(document.getString("Genre"));
                    etActors.setText(document.getString("Actors"));
                    etPlot.setText(document.getString("Plot"));
                    etLanguage.setText(document.getString("Language"));
                    etPoster.setText(document.getString("Poster"));
                } else {
                    Log.d(TAG, "No such document");
                }
            } else {
                Log.d(TAG, "get failed with ", task.getException());
            }
        });

        btnUpdate.setOnClickListener(this::btnUpdateOnClick);

        btnDelete.setOnClickListener(this::btnDeleteOnClick);
    }


    public void btnUpdateOnClick(View v) {
        Movie movie = new Movie(
                etTitle.getText().toString().trim(),
                etRated.getText().toString().trim(),
                etReleased.getText().toString().trim(),
                etRuntime.getText().toString().trim(),
                etGenre.getText().toString().trim(),
                etActors.getText().toString().trim(),
                etPlot.getText().toString().trim(),
                etLanguage.getText().toString().trim(),
                etPoster.getText().toString().trim());

        db.collection("movies").document(movieId).set(movie)
                .addOnSuccessListener((Void unused) ->
                        Toast.makeText(ViewMovieDetailsActivity.this,
                                "This movie has been updated.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener((Exception e) ->
                        Toast.makeText(
                                ViewMovieDetailsActivity.this,
                                "Fail to update this movie.", Toast.LENGTH_SHORT).show());
        finish();
    }

    public void btnDeleteOnClick(View v) {
        db.collection("movies").document(movieId).delete()
                .addOnSuccessListener((Void unused) ->
                        Toast.makeText(ViewMovieDetailsActivity.this,
                                "This movie has been deleted.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener((Exception e) ->
                        Toast.makeText(ViewMovieDetailsActivity.this,
                                "Fail to delete this movie.", Toast.LENGTH_SHORT).show());
        finish();
    }

}