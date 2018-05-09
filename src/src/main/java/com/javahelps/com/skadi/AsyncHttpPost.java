//This is the class that allows us to connect database


package com.javahelps.com.skadi;

import android.content.ContentValues;
import android.os.AsyncTask;


public abstract class AsyncHttpPost extends AsyncTask<String, String, String> {
    String address;
    ContentValues parameters;

    public AsyncHttpPost(String address, ContentValues parameters) {
        this.address = address;
        this.parameters = parameters;
    }

}