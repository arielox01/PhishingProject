/*
 * אריאל ליבשיץ - 324079631
 * גיא רג'ואן - 322985409
 */
package com.example.projectbiuni;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestHandler {
    private static RequestHandler requestHandler;
    private RequestQueue requestQueue;
    private static Context context;

    private RequestHandler(Context context){
        this.context = context;
        requestQueue = getRequestQueue();
    }
    public static synchronized RequestHandler getInstance(Context context){
        if (requestHandler == null)
            requestHandler = new RequestHandler(context);
        return requestHandler;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null)
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request){
        getRequestQueue().add(request);
    }
}
