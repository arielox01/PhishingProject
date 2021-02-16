/*
 * אריאל ליבשיץ - 324079631
 * גיא רג'ואן - 322985409
 */
package com.example.projectbiuni;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    TextView txtUsername, txtUserEmail;
    Button btnRegisterEmail, btnSendEmail, btnGetStatistics;
    LinearLayout linearLayout1, linearLayout2;
    ListView lv;
    List<String> emailsList;

    ProgressDialog proggressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        if (!SharedPrefManager.getInstance(this).isLoggedIn()){
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }

        proggressDialog = new ProgressDialog(this);
        proggressDialog.setMessage("Please wait...");

        linearLayout1 = findViewById(R.id.linearLayout1);
        linearLayout2 = findViewById(R.id.linearLayout2);

        Log.d("AAA", "AAA " + SharedPrefManager.getInstance(this).getType());
        if (SharedPrefManager.getInstance(this).getType().equals("0")){
            linearLayout1.setVisibility(View.VISIBLE);
            linearLayout2.setVisibility(View.GONE);
        } else{
            linearLayout1.setVisibility(View.GONE);
            linearLayout2.setVisibility(View.VISIBLE);
        }

        txtUserEmail = findViewById(R.id.txtUserEmail);
        txtUsername = findViewById(R.id.txtUsername);
        btnRegisterEmail = findViewById(R.id.btnRegisterEmail);
        btnSendEmail = findViewById(R.id.btnSendEmail);
        btnGetStatistics = findViewById(R.id.btnGetStatistics);
        lv = findViewById(R.id.lv);

        emailsList = new ArrayList<>();
        getEmails();

        txtUsername.setText(SharedPrefManager.getInstance(this).getUsername());
        txtUserEmail.setText(SharedPrefManager.getInstance(this).getUserEmail());

        btnRegisterEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegisterEmailActivity.class));
            }
        });

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), SendEmailActivity.class));
            }
        });

        btnGetStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmailStatisticsActivity.class);
                intent.putExtra("email", SharedPrefManager.getInstance(getApplicationContext()).getUserEmail());
                startActivity(intent);
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Dialog dialog = new Dialog(ProfileActivity.this);
                dialog.setContentView(R.layout.custom);

                Button btnGetStatistics = dialog.findViewById(R.id.btnGetStatistics);
                Button btnDeleteUser = dialog.findViewById(R.id.btnDeleteUser);

                btnGetStatistics.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getApplicationContext(), EmailStatisticsActivity.class);
                        intent.putExtra("email", emailsList.get(position));
                        startActivity(intent);
                    }
                });
                
                btnDeleteUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteUser(emailsList.get(position));
                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                        finish();
                    }
                });

                dialog.show();
            }
        });
    }

    private void deleteUser(final String email){

        proggressDialog.setMessage("Registering user...");
        proggressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                Constants.URL_DELETE_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        proggressDialog.dismiss();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        proggressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogout:
                SharedPrefManager.getInstance(this).logout();
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                break;
        }
        return true;
    }

    private void getEmails(){
        proggressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_GET_USERS_EMAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){

                                JSONArray jsonArray = new JSONArray(jsonObject.getString("emailsArray"));
                                int numOfEmails = jsonObject.getInt("numOfEmails");
                                for (int i = 0; i < numOfEmails; i++){
                                    emailsList.add(jsonArray.getJSONArray(i).getString(3));
                                }
                                updateListView(emailsList);
                                proggressDialog.dismiss();
                            }else{
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                proggressDialog.dismiss();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            proggressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                proggressDialog.dismiss();
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateListView(List<String> list) {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(arrayAdapter);

    }
}
