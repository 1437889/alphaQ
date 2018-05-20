package com.example.admin.courseforum;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.example.admin.courseforum.questions.inflater;
import static com.example.admin.courseforum.questions.lin;
import static com.example.admin.courseforum.questions.rel;
import static com.example.admin.courseforum.questions.usrID;

public class ViewQuestion extends Activity {
    int Voted;
    String username, usr, answer;
    EditText body;
    int q_id, q_id_vote;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewquestion);

        Bundle extras = getIntent().getExtras();
       // ((TextView)findViewById(R.id.body)).setText(extras.getString("title"));
        ((TextView)findViewById(R.id.answeredBy)).setText(extras.getString("user"));
        ((TextView)findViewById(R.id.qTitle)).setText(extras.getString("question"));
        //((TextView)findViewById(R.id.votesText)).setText(Integer.toString(extras.getInt("votes")));

        checkVote();
        listAnswers(extras.getInt("question_id"), false, -1);
    }

    @SuppressLint("StaticFieldLeak")
    public void updateVote(View v){
        ContentValues params = new ContentValues();
        Bundle extras = getIntent().getExtras();

        if (getIntent().getStringExtra("user") != null) {
            usr = extras.getString("user");
        }

        if (getIntent().getStringExtra("question_id") != null) {
            q_id_vote = extras.getInt("question_id");
        }

        params.put("user",usr);
        params.put("question_id",q_id_vote);


        @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/updateVote.php?output=" + Integer.toString(Voted), params) {
            @Override
            protected void onPostExecute(String output) {
            }
        };

        asyncHTTPPost.execute();
        checkVote();

        asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/getVotesQ.php?qid=" + extras.getInt("id"), params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray j = new JSONArray(output);
                    int i = j.getJSONObject(0).getInt("Votes");
                    ((TextView) findViewById(R.id.votesText)).setText(Integer.toString(i));
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };
        asyncHTTPPost.execute();

        listAnswers(extras.getInt("id"), false, -1);
    }

    public void checkVote(){
        ContentValues params = new ContentValues();
        params.put("", "");
        Bundle extras = getIntent().getExtras();

        @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/checkVoteQ.php?userid=" + extras.getInt("userID")
                + "&qid=" + extras.getInt("id"), params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray arr = new JSONArray(output);
                    Voted = arr.getJSONObject(0).getInt("Voted");
                    if (Voted==1){
                        ImageView img = (ImageView)findViewById(R.id.votedImage);
                        img.setImageDrawable(getResources().getDrawable(R.drawable.filled));
                    }
                    else {
                        ImageView img = (ImageView)findViewById(R.id.votedImage);
                        img.setImageDrawable(getResources().getDrawable(R.drawable.unfilled));
                    }

                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        asyncHTTPPost.execute();

    }

    @Override
    public void onBackPressed() {
        Bundle extras = getIntent().getExtras();
        //questions.refreshScrollView(questions.lin, questions.inflater , extras.getInt("userID"), questions.rel);
        //questions.refreshScrollView();
        super.onBackPressed();
    }

    public void listAnswers(int question, final Boolean clicked, final int aidclicked){
        ContentValues params = new ContentValues();
        params.put("question_id", question);

        @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/answerlist.php", params) {
            @Override
            protected void onPostExecute(String output) {
                LinearLayout l = (LinearLayout) findViewById(R.id.answerLinLayout);
                l.removeAllViews();
                Toast.makeText(getBaseContext(), output, Toast.LENGTH_LONG);
                try {
                    JSONArray arr = new JSONArray(output);

                    for (int i = 0; i < arr.length(); i++) {
                        RelativeLayout rl = (RelativeLayout) getLayoutInflater().inflate(R.layout.answerlayout, null);

                        JSONObject j = arr.getJSONObject(i);
                        int aid = j.getInt("ANSWER_ID");

                        updateAnswerVote((ImageView)rl.findViewById(R.id.votedimg), aid,
                                getResources().getDrawable(R.drawable.filled), getResources().getDrawable(R.drawable.unfilled),
                                clicked, aidclicked, (TextView)rl.findViewById(R.id.voteText));

                        String user = j.getString("USER_NAME");
                        String ans = j.getString("ANSWER");

                       // int correct = j.getInt("Correct");
                        //int votes = j.getInt("Votes");

                        TextView t = ((TextView)rl.findViewById(R.id.answeredBy));
                        t.setText(user);
                        t = ((TextView)rl.findViewById(R.id.fullanswer));
                        t.setText(ans);
                        t = ((TextView)rl.findViewById(R.id.voteText));
                       // t.setText(Integer.toString(votes));
                        rl.findViewById(R.id.votedimg).setTag(aid);

                        /*if (correct == 1){
                            rl.findViewById(R.id.correctimg).setVisibility(View.VISIBLE);
                        } else{
                            rl.findViewById(R.id.correctimg).setVisibility(View.INVISIBLE);
                        }
                        */
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

    @SuppressLint("StaticFieldLeak")
    public void checkAnswerVotes(final ImageView img, int aid, final Drawable d1, final Drawable d2, final TextView t){
        ContentValues params = new ContentValues();
        params.put("", "");
        Bundle extras = getIntent().getExtras();

        @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/checkVoteA.php?userid=" + extras.getInt("userID")
                + "&aid=" + aid, params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray arr = new JSONArray(output);
                    if (arr.getJSONObject(0).getInt("Voted") == 1){
                        img.setImageDrawable(d1);
                    } else {
                        img.setImageDrawable(d2);
                    }
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        asyncHTTPPost.execute();

        asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/getVotesA.php?aid=" + aid, params) {
            @Override
            protected void onPostExecute(String output) {
                try {
                    JSONArray arr = new JSONArray(output);
                    t.setText(arr.getJSONObject(0).getString("Votes"));
                }
                catch (JSONException e){
                    e.printStackTrace();
                }
            }
        };

        asyncHTTPPost.execute();

    }

    public void updateAnswerVote(final ImageView img, int aid, Drawable d1, Drawable d2, Boolean clicked, int aidclicked, TextView t){
        if (clicked) {
            if (aid == aidclicked) {
                ContentValues params = new ContentValues();
                params.put("", "");
                Bundle extras = getIntent().getExtras();

                @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/updateAnswerVote.php?&userid="
                        + extras.getInt("userID") + "&aid=" + aid, params) {
                    @Override
                    protected void onPostExecute(String output) {
                        Toast.makeText(getBaseContext(), output, Toast.LENGTH_SHORT);
                    }
                };
                asyncHTTPPost.execute(); //Runs query to insert/delete vote
            }
        }
        checkAnswerVotes(img, aid, d1, d2, t);
    }

    public void doList(View v) {
        Bundle extras  = getIntent().getExtras();
        int q = extras.getInt("question_id");
        listAnswers(q, false, Integer.parseInt(v.getTag().toString()));
    }


    public void process(String output){
        System.out.println(output);
        try {
            JSONArray arr = new JSONArray(output);
            lin.removeAllViews();
            for (int i = 0; i < arr.length(); i++) {
                RelativeLayout rl = (RelativeLayout) inflater.inflate(R.layout.questionlayout, rel);
                JSONObject j = arr.getJSONObject(i);

                answer = j.getString("ANSWER");



                ContentValues params = new ContentValues();
                params.put("answer",answer);

                lin.addView(rl);
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }

    }

    public void postAnswer(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dView = inflater.inflate(R.layout.answerdialogue, null);
        final EditText e = (EditText) dView.findViewById(R.id.answerEdit);

        Bundle extras = getIntent().getExtras();
            username = extras.getString("user");

            q_id = extras.getInt("question_id");


        builder.setTitle("Enter your answer");
        builder.setView(dView);


        builder.setPositiveButton("Post answer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ContentValues params = new ContentValues();

                params.put("user", username);
                params.put("answer",e.getText().toString());
                params.put("question_id",q_id);


                @SuppressLint("StaticFieldLeak") AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://lamp.ms.wits.ac.za/~s1619336/newanswer.php", params) {
                    @Override
                    protected void onPostExecute(String output) {
                        Bundle extras = getIntent().getExtras();

                        listAnswers(extras.getInt("question_id"), false, -1);
                    }
                };
                asyncHTTPPost.execute();
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

}