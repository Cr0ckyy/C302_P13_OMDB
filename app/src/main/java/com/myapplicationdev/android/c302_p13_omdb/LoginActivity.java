package com.myapplicationdev.android.c302_p13_omdb;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    static final String TAG = "LoginActivity";
    EditText etLoginID, etPassword;
    Button btnSubmit;
    AsyncHttpClient client;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getLocalIpAddress();

        etLoginID = findViewById(R.id.editTextLoginID);
        etPassword = findViewById(R.id.editTextPassword);
        btnSubmit = findViewById(R.id.buttonSubmit);
        client = new AsyncHttpClient();
        sharedPreferences = getSharedPreferences("C302_P13", Context.MODE_PRIVATE);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etLoginID.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (username.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, "Login failed. Please enter username.", Toast.LENGTH_LONG).show();

                } else if (password.equalsIgnoreCase("")) {
                    Toast.makeText(LoginActivity.this, "Login failed. Please enter password.", Toast.LENGTH_LONG).show();

                } else {
                    RequestParams params = new RequestParams();
                    params.add("username", username);
                    params.add("password", password);

                    // http://10.0.2.2/C302_P13/dbFunctions.php
                    // http://10.0.2.2/C302_P13/doLogin.php
                    //   client.get("http://10.0.2.2/C302_sakila/getRentalLocationsByFilmId.php", params, new JsonHttpResponseHandler() {
                    client.post("http://10.0.2.2/C302_P13/doLogin.php", params, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);

                            try {
                                if (response.getBoolean("authenticated")) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putInt("user_id", response.getInt("id"));
                                    editor.putString("apikey", response.getString("apikey"));
                                    editor.apply();
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else
                                    Toast.makeText(LoginActivity.this, "Incorrect username or password.", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });


    }

    public void getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses(); addressEnumeration.hasMoreElements(); ) {
                    InetAddress inetAddress = addressEnumeration.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String localIpAddressMsg = String.format("%s: Android Emulator's IP address: %s", TAG, inetAddress.getHostAddress());
                        System.out.println(localIpAddressMsg);
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e(TAG, ex.toString());
        }
    }
}


