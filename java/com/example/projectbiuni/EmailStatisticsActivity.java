/*
 * אריאל ליבשיץ - 324079631
 * גיא רג'ואן - 322985409
 */
package com.example.projectbiuni;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmailStatisticsActivity extends AppCompatActivity {

    ListView lv;
    PieChart pieChart;

    ProgressDialog proggressDialog;
    int howManyPhished = 0;
    int howManyNotPhished = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_statistics);

        lv = findViewById(R.id.lv);
        pieChart = findViewById(R.id.pieChart);

        proggressDialog = new ProgressDialog(this);
        proggressDialog.setMessage("Please wait...");

        getEmails(getIntent().getStringExtra("email"));
    }

    private void getEmails(final String email){
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
                                List<String> list = new ArrayList<>();
                                int numOfEmails = jsonObject.getInt("numOfEmails");

                                for (int i = 0; i < numOfEmails; i++){
                                    if (jsonArray.getJSONArray(i).getString(2).equals("0")){
                                        howManyNotPhished++;
                                        list.add(jsonArray.getJSONArray(i).getString(1) + " - Not Phished");
                                    }
                                    else if (jsonArray.getJSONArray(i).getString(2).equals("1")){
                                        howManyPhished++;
                                        list.add(jsonArray.getJSONArray(i).getString(1) + " - Got Phished");
                                    }
                                }
                                updateListView(list);
                                updatePieChart();
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

    private void updatePieChart() {
        pieChart.setUsePercentValues(true);

        Description description = new Description();
        description.setText("This is the phishing statistics");
        description.setTextSize(15f);
        pieChart.setDescription(description);

        pieChart.setHoleRadius(25f);
        pieChart.setTransparentCircleRadius(25f);

        List<PieEntry> values = new ArrayList<>();
        values.add(new PieEntry(howManyNotPhished, "Not Phished"));
        values.add(new PieEntry(howManyPhished, "Got Phished"));

        PieDataSet pieDataSet = new PieDataSet(values, "/ Phished Statistics");

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);

        pieDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        pieChart.animateXY(1400, 1400);
    }

    private void updateListView(List<String> list) {

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        lv.setAdapter(arrayAdapter);

    }
}
