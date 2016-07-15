package com.cmrl.cmrllite;

/*
* This Activity gets the location coordinates from the preceding activity and presents a screen to
* user for inputing the username and password. It uses Volley module to connect to URL and send
* request and get response. It uses UserLoginRequest for POST array. The response array is passed to
* next program in chain.
* B. Umesh Rai
 */

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class UserActivity extends AppCompatActivity {

    private String latitude = "";
    private String longitude = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPassword);
        final TextView tvLatitude = (TextView) findViewById(R.id.tvLatitude);
        final TextView tvLongitude = (TextView) findViewById(R.id.tvLongitude);
        final Button btLogin = (Button) findViewById(R.id.btLogin);

        // Save values from predecessor
        Intent intent = getIntent();
        latitude = intent.getStringExtra("latitude");
        longitude = intent.getStringExtra("longitude");
        tvLatitude.setText("Latitude: " + latitude);
        tvLongitude.setText("Longitude: " + longitude);


        // Start Login. JSON Request and Response
        assert btLogin != null;
        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide keyboard
                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                // Store Username/Password
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();

                if (username.equals("")||password.equals(""))
                    Toast.makeText(getApplicationContext(),"Enter all Credentials!",Toast.LENGTH_SHORT).show();
                else {
                    // Response received from the server
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            boolean success = false;
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                //Log.i("value", jsonResponse.toString());
                                success = jsonResponse.getJSONObject("data").getBoolean("success");
                                String userid = jsonResponse.getJSONObject("data").getString("id");
                                String organisation = jsonResponse.getJSONObject("data").getString("organisation");
                                String auth_key = jsonResponse.getJSONObject("data").getString("auth_key");
                                //Log.i("value",  "success: " + String.valueOf(success) + ", auth_key: "+ auth_key);

                                if (success) {
                                    SaveData.ORGANISATION = organisation;
                                    SaveData.USER_ID = userid;
                                    //Pass to successor program
                                    Intent intent = new Intent(UserActivity.this, AssetMaintActivity.class);
                                    intent.putExtra("auth_key", auth_key);
                                    UserActivity.this.startActivity(intent);
                                } /*else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                                    builder.setMessage("Login Failed")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }*/
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if(!success) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);
                                builder.setMessage("Login Failed")
                                        .setNegativeButton("Retry", null)
                                        .create()
                                        .show();
                            }
                        }
                    };
                    // Volley Request
                    UserLoginRequest loginRequest = new UserLoginRequest(username, password, latitude, longitude, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(UserActivity.this);
                    queue.add(loginRequest);
                    //Log.i("values", "username: "+username+"/"+password+" location: "+latitude+"/"+longitude );
                }
            }
        });
    }
}

