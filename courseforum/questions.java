package com.example.admin.courseforum;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.zip.Inflater;

public class questions extends Activity {
    public static LayoutInflater inflater;
    public static RelativeLayout rel;
    public static String usrID;
    public static LinearLayout lin;
    private View rl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions);


        inflater = getLayoutInflater();
        rel = (RelativeLayout) findViewById(R.id.qrl);
        lin = (LinearLayout) findViewById(R.id.linLay);
        System.out.println("pizza");
        //refreshScrollView(lin, inflater, usrID, rel);
        ContentValues params = new ContentValues();

        @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/questionlist.php", params) {
            @Override
            protected void onPostExecute(String output) {
                process(output);
            }
        };

        asyncHTTPPost.execute();


    }

    public void refreshScrollView(){
        ContentValues params = new ContentValues();

        @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/questionlist.php", params) {
            @Override
            protected void onPostExecute(String output) {
                process(output);
            }
        };

        asyncHTTPPost.execute();

    }


   public void process(String output){
       System.out.println(output);
       try {
           JSONArray arr = new JSONArray(output);
           lin.removeAllViews();
           for (int i = 0; i < arr.length(); i++) {
               RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.questionlayout, rel);
               JSONObject j = arr.getJSONObject(i);

               //final int numVotes = j.getInt("Votes");
               final String username = j.getString("USER_NAME");
               final String body = j.getString("QUESTION");
               final int id = j.getInt("QUESTION_ID");

               ContentValues params = new ContentValues();
               params.put("user",username);
               params.put("question",body);
               params.put("question_id",id);

               TextView t = (TextView)rl.findViewById(R.id.textView15);
               t.setText(body);
               t = (TextView)rl.findViewById(R.id.textView16);
             //  t.setText(Integer.toString(numVotes));
               t = (TextView)rl.findViewById(R.id.userText);
               t.setText(username);


               rl.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       Intent i = new Intent(v.getContext(), ViewQuestion.class);
                       i.putExtra("user", username);
                       i.putExtra("question", body);
                       i.putExtra("question_id", id);
                     //  i.putExtra("votes", numVotes);
                       //i.putExtra("userID", userID);
                       v.getContext().startActivity(i);
                   }
               });

               lin.addView(rl);
           }
       }
       catch(JSONException e){
           e.printStackTrace();
       }


   }


    public void newQuestion(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogue, null);
        final EditText title = (EditText) dialogView.findViewById(R.id.qTitle);
        final EditText body = (EditText) dialogView.findViewById(R.id.body);
        Bundle extras = getIntent().getExtras();
        if (getIntent().getStringExtra("USER_NAME") != null) {
            usrID = extras.getString("USER_NAME");
        }

        builder.setTitle("Ask a question");
        builder.setView(dialogView);
        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            @SuppressLint("SetTextI18n")
            public void onClick(DialogInterface dialog, int id) {
                ContentValues params = new ContentValues();
                //System.out.println(usrID);
                params.put("user", usrID);
                params.put("question",body.getText().toString());

                //params.put("title",title.getText().toString());

                @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHttpPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/newquestion.php", params) {
                    @Override
                    protected void onPostExecute(String output) {
                        if (output.charAt(output.length()-1) == '0'){
                            Toast.makeText(getApplicationContext(), "Your question has been posted", Toast.LENGTH_SHORT).show();
                        }
                        else {Toast.makeText(getApplicationContext(), "Your question was not able to be posted.", Toast.LENGTH_SHORT).show();}
                    }
                };
                asyncHttpPost.execute();
                refreshScrollView();
                //refreshScrollView(lin, inflater, usrID, rel);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog d = builder.create();
        d.show();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Do you want to sign out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        questions.this.finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}