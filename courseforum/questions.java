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
    public static int usrID;
    public static LinearLayout lin;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Bundle extras = getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions);


        inflater = getLayoutInflater();
        rel = (RelativeLayout) findViewById(R.id.qrl);
        lin = (LinearLayout) findViewById(R.id.linLay);

        refreshScrollView(lin, inflater, usrID, rel);
    }

    public static void refreshScrollView(final LinearLayout l, final LayoutInflater inflater, final int userID, final RelativeLayout qrl){
        ContentValues params = new ContentValues();
        params.put("", "");

        @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/questionlist.php", params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray arr = new JSONArray(output);
                    l.removeAllViews();
                    for (int i = 0; i < arr.length(); i++) {
                        RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.questionlayout, qrl);
                        JSONObject j = arr.getJSONObject(i);

                        final int numVotes = j.getInt("Votes");
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
                        t.setText(Integer.toString(numVotes));
                        t = (TextView)rl.findViewById(R.id.userText);
                        t.setText(username);


                        rl.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent i = new Intent(v.getContext(), ViewQuestion.class);
                                i.putExtra("user", username);
                                i.putExtra("QUESTION", body);
                                i.putExtra("QUESTION_ID", id);
                                i.putExtra("votes", numVotes);
                                i.putExtra("userID", userID);
                                v.getContext().startActivity(i);
                            }
                        });

                        l.addView(rl);
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        };

        asyncHTTPPost.execute();
    }

    public void newQuestion(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialogue, null);
        final EditText title = (EditText) dialogView.findViewById(R.id.qTitle);
        final EditText body = (EditText) dialogView.findViewById(R.id.textView19);

        builder.setTitle("Ask a question");
        builder.setView(dialogView);
        builder.setPositiveButton("Post", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                ContentValues params = new ContentValues();
                params.put("", "");

                @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHttpPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/newQuestion.php?user=" + usrID + "&title="
                        + title.getText().toString() + "&body=" + body.getText().toString(), params) {
                    @Override
                    protected void onPostExecute(String output) {
                        if (output.charAt(output.length()-1) == '0'){
                            Toast.makeText(getApplicationContext(), "Your question has been posted", Toast.LENGTH_SHORT).show();
                        }
                        else {Toast.makeText(getApplicationContext(), "Your question was not able to be posted.", Toast.LENGTH_SHORT).show();}
                    }
                };
                asyncHttpPost.execute();

                refreshScrollView(lin, inflater, usrID, rel);
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