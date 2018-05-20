package com.example.admin.courseforum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class createAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createaccount);
    }


    public void create(View view){
        String username = ((EditText)findViewById(R.id.user2)).getText().toString();
        String password = ((EditText)findViewById(R.id.pwd)).getText().toString();
        String name = ((EditText)findViewById(R.id.name)).getText().toString();
        String surname = ((EditText)findViewById(R.id.srn)).getText().toString();

        ContentValues params = new ContentValues();
        params.put("user",username);
        params.put("pword",password);
        params.put("fname",name);
        params.put("lname",surname);

        @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/create.php", params) {
            @Override
            protected void onPostExecute(String output) {
                if (!output.isEmpty()) {
                    Toast t1 = Toast.makeText(getApplicationContext(), "Account Was Successfully Created", Toast.LENGTH_SHORT);
                    t1.setGravity(Gravity.CENTER, 0, 0);
                    t1.show();
                }


                    if (!output.isEmpty()){
                        Toast t2 = Toast.makeText(getApplicationContext(), "Sign in to confirm",Toast.LENGTH_SHORT);
                        t2.setGravity(Gravity.CENTER,0,0);
                        t2.show();
                    Intent i = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(i);
                }
                else {
                    Toast.makeText(getApplicationContext(), "Account Was Not Able To Be Created", Toast.LENGTH_SHORT).show();}
            }
        };
        asyncHTTPPost.execute();

    }

}
