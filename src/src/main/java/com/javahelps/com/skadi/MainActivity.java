package com.javahelps.com.skadi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

package com.example.seven13;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ContentValues params = new ContentValues();
        params.put("username","pravesh");
        AsyncHTTPPost asyncHTTPPost = new AsyncHTTPPost("http://courses.ms.wits.ac.za/test3.php", params) {
            @Override
            protected void onPostExecute(String output) {
                final TextView t = findViewById(R.id.textView1);
                t.setText(output);
            }
        };

        asyncHTTPPost.execute();


    }


    public abstract class AsyncHTTPPost extends AsyncTask<String, String, String> {
        String address;
        ContentValues parameters;

        public AsyncHTTPPost(String address, ContentValues parameters) {
            this.address = address;
            this.parameters = parameters;
        }


        @Override
        protected String doInBackground(String... params){

            try {
                URL url = new URL(address);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                connection.setDoOutput(true);
                connection.setDoInput(true);
                if (parameters.size() > 0) {
                    Uri.Builder builder = new Uri.Builder();
                    for (String s : parameters.keySet()) {
                        builder.appendQueryParameter(s, parameters.getAsString(s));
                    }
                    String query = builder.build().getEncodedQuery();
                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    writer.close();
                    os.close();
                }
                BufferedReader br = new BufferedReader( new InputStreamReader(connection.getInputStream()));
                String response = br.readLine();
                br.close();
                return response;
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "";

            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
        }
        @Override
        protected abstract void onPostExecute(String output);
    }
}


}
