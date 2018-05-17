package com.example.admin.courseforum;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    private View rl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void signIn(View view) {

        ContentValues params = new ContentValues();
        params.put("", "");
        final String enteredPassword = ((EditText) findViewById(R.id.psswd)).getText().toString();

        @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost(
                "http://lamp.ms.wits.ac.za/~s1619336/signin.php?username=" + ((EditText) findViewById(R.id.user)).getText().toString(), params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray arr = new JSONArray(output);
                    String name = arr.getJSONObject(0).getString("USER_FNAME");
                    String surname = arr.getJSONObject(0).getString("USER_LNAME");
                    String password = arr.getJSONObject(0).getString("USER_PWORD");
                    String username = arr.getJSONObject(0).getString("USER_NAME");


                    if (password.equals(enteredPassword)) {
                        Toast.makeText(getBaseContext(), "SIGN IN SUCCESSFUL", Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getBaseContext(), questions.class);
                        i.putExtra("USER_FNAME", name);
                        i.putExtra("USER_LNAME", surname);
                        i.putExtra("USER_NAME", username);
                        startActivity(i);
                    } else {
                        Toast.makeText(getBaseContext(), "PASSWORD IS INCORRECT", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getBaseContext(), "USERNAME DOES NOT EXIST. PLEASE CREATE AN ACCOUNT", Toast.LENGTH_SHORT).show();
                }

            }
        };
        asyncHTTPPost.execute();

    }

    public void newUserClick(View v) {
        Intent i = new Intent(v.getContext(), createAccount.class);
        startActivity(i);

    }
}
