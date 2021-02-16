/*
 * אריאל ליבשיץ - 324079631
 * גיא רג'ואן - 322985409
 */
package com.example.projectbiuni;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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

public class SendEmailActivity extends AppCompatActivity {

    Button btnSendEmail;
    ListView lv;
    ProgressDialog proggressDialog;
    List<String> emailsList;
    List<String> emailsIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        btnSendEmail = findViewById(R.id.btnSendEmail);
        lv = findViewById(R.id.lv);

        proggressDialog = new ProgressDialog(this);
        proggressDialog.setMessage("Please wait...");

        emailsList = new ArrayList<>();
        emailsIdList = new ArrayList<>();
        getEmails();

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmails();
            }
        });
    }

    private void sendEmails() {
        proggressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_SEND_EMAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                Toast.makeText(getApplicationContext(), "The operation was successfully", Toast.LENGTH_LONG).show();
                                proggressDialog.dismiss();
                            } else {
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
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("numOfEmails", String.valueOf(emailsList.size()));
                params.put("table", SharedPrefManager.getInstance(getApplicationContext()).getUserEmail());
                for (int i = 0; i < emailsList.size(); i++){
                    params.put("emails_"+i, emailsList.get(i));
                    params.put("emails_id_"+i, emailsIdList.get(i));
                }
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getEmails(){
        final String email = SharedPrefManager.getInstance(this).getUserEmail();
        proggressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_GET_EMAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){

                                JSONArray jsonArray = new JSONArray(jsonObject.getString("emailsArray"));
                                int numOfEmails = jsonObject.getInt("numOfEmails");
                                for (int i = 0; i < numOfEmails; i++){
                                    emailsList.add(jsonArray.getJSONArray(i).getString(1));
                                    emailsIdList.add(jsonArray.getJSONArray(i).getString(0));
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
                params.put("email", email);
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
