package com.myapplicationdev.android.c302_p13_omdb;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class CreateMovieActivity extends AppCompatActivity {

    EditText etTitle, etRated, etReleased, etRuntime, etGenre, etActors, etPlot, etLanguage, etPoster;
    Button btnCreate, btnSearch;
    ImageButton btnCamera;
    String apikey;
    final String TAG = "CreateMovieActivity";
    SharedPreferences sharedPreferences;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_movie);

        etTitle = findViewById(R.id.etTitle);
        etRated = findViewById(R.id.etRated);
        etReleased = findViewById(R.id.etReleased);
        etRuntime = findViewById(R.id.etRuntime);
        etGenre = findViewById(R.id.etGenre);
        etActors = findViewById(R.id.etActors);
        etPlot = findViewById(R.id.etPlot);
        etLanguage = findViewById(R.id.etLanguage);
        etPoster = findViewById(R.id.etPoster);
        btnCreate = findViewById(R.id.btnCreate);
        btnSearch = findViewById(R.id.btnSearch);
        btnCamera = findViewById(R.id.btnCamera);

        sharedPreferences = getSharedPreferences("C302_P13", Context.MODE_PRIVATE);
        apikey = sharedPreferences.getString("apikey", "");
        if (apikey.isEmpty()) {
            finish();
        }

        btnCreate.setOnClickListener(this::btnCreateOnClick);

        btnSearch.setOnClickListener(this::btnSearchOnClick);

        btnCamera.setOnClickListener(this::btnCameraOnClick);
    }

    void btnCreateOnClick(View v) {
        if (etTitle.getText().toString().trim().isEmpty()
                || etRated.getText().toString().trim().isEmpty()
                || etReleased.getText().toString().trim().isEmpty()
                || etRuntime.getText().toString().trim().isEmpty()
                || etGenre.getText().toString().trim().isEmpty()
                || etActors.getText().toString().trim().isEmpty()
                || etPlot.getText().toString().trim().isEmpty()
                || etLanguage.getText().toString().trim().isEmpty()
                || etPoster.getText().toString().trim().isEmpty()

        ) {
            Toast.makeText(CreateMovieActivity.this, "This field remains empty.", Toast.LENGTH_SHORT).show();
            return;
        }


        Movie movie = new Movie(
                etTitle.getText().toString().trim(),
                etRated.getText().toString().trim(),
                etReleased.getText().toString().trim(),
                etRuntime.getText().toString().trim(),
                etGenre.getText().toString().trim(),
                etActors.getText().toString().trim(),
                etPlot.getText().toString().trim(),
                etLanguage.getText().toString().trim(),
                etPoster.getText().toString().trim()
        );

        db.collection("movies")
                .add(movie)
                .addOnSuccessListener((DocumentReference documentReference)
                        -> Log.d(TAG, "Added"))
                .addOnFailureListener((Exception e)
                        -> Log.w(TAG, "Error adding document", e)
                );
        finish();
    }

    public void btnSearchOnClick(View v) {
        if (etTitle.getText().toString().trim().isEmpty()) {
            Toast.makeText(CreateMovieActivity.this, "The title remains empty.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams requestParams = new RequestParams();
        requestParams.add("apikey", apikey);
        requestParams.add("t", etTitle.getText().toString().trim());

        client.get("http://www.omdbapi.com/", requestParams, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    etTitle.setText(response.getString("Title"));
                    etRated.setText(response.getString("Rated"));
                    etReleased.setText(response.getString("Released"));
                    etRuntime.setText(response.getString("Runtime"));
                    etGenre.setText(response.getString("Genre"));
                    etActors.setText(response.getString("Actors"));
                    etPlot.setText(response.getString("Plot"));
                    etLanguage.setText(response.getString("Language"));
                    etPoster.setText(response.getString("Poster"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    static final int REQUEST_IMAGE_CAPTURE = 1;

    public void btnCameraOnClick(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            //TODO: feed imageBitmap into FirebaseVisionImage for text recognizing
            FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(imageBitmap);
            FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                    .getCloudTextRecognizer();

            Task<FirebaseVisionText> result = detector.processImage(image)
                    .addOnSuccessListener((FirebaseVisionText firebaseVisionText)
                            -> etTitle.setText(firebaseVisionText.getText()))
                    .addOnFailureListener((Exception e) ->
                            Toast.makeText(CreateMovieActivity.this,
                                    "Cannot find text", Toast.LENGTH_SHORT).show()
                    );
        }
    }
}