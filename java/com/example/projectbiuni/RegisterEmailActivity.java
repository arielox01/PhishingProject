/*
 * אריאל ליבשיץ - 324079631
 * גיא רג'ואן - 322985409
 */
package com.example.projectbiuni;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterEmailActivity extends AppCompatActivity {

    EditText etRegisterEmail;
    Button btnRegisterEmail, btnDeleteEmails;
    ProgressDialog proggressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_email);

        etRegisterEmail = findViewById(R.id.etRegisterEmail);
        btnRegisterEmail = findViewById(R.id.btnRegisterEmail);
        btnDeleteEmails = findViewById(R.id.btnDeleteEmails);

        proggressDialog = new ProgressDialog(this);
        proggressDialog.setMessage("Please wait...");

        btnRegisterEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerEmail();
            }
        });
        btnDeleteEmails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteEmails();
            }
        });
    }

    private void registerEmail(){
        final String username = SharedPrefManager.getInstance(this).getUsername();
        final String password = SharedPrefManager.getInstance(this).getUserPassword();
        final String myEmail = SharedPrefManager.getInstance(this).getUserEmail();
        final String otherEmail = etRegisterEmail.getText().toString().trim();
        proggressDialog.show();


        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_REGISTER_EMAIL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                params.put("username", username);
                params.put("password", password);
                params.put("myemail", myEmail);
                params.put("otheremail", otherEmail);
                return params;
            }
        };

        RequestHandler.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void deleteEmails(){
        final String email = SharedPrefManager.getInstance(this).getUserEmail();
        proggressDialog.show();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_DELETE_EMAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")){
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
}
