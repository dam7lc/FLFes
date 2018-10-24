package com.darktech.tarag;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FeedActivity extends Activity {

    Socket tsocket;
    String phone;
    String intrSex;
    TApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        Intent intentFrom = getIntent();
        phone = intentFrom.getStringExtra("phone");
        intrSex = intentFrom.getStringExtra("pref");

        app = (TApplication) getApplication();
        tsocket = app.getSocket();
        tsocket.on("populateFeedResponse", onpopulateFeedResponse);

        JSONObject info = new JSONObject();
        try {
            info.put("sexInteres", intrSex);
            info.put("phone", phone);

            tsocket.emit("populateFeed", info);
        } catch (final JSONException e){

        }


    }

    Emitter.Listener onpopulateFeedResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject)args[0];
            populateFeedResponse(response);
        }
    };

    private void populateFeedResponse(JSONObject in){
        try{
            int response = in.getInt("response");
            switch(response){
                case 0:
                    final String nickname = in.getString("nick");
                    final String imgurl = in.getString("img");
                    StringBuilder URL = new StringBuilder();
                    String server = app.getServer();
                    URL.append(server);
                    URL.append(imgurl);
                    final Bitmap bm = getImgFromServer(URL.toString());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CardView user = new CardView(getApplicationContext());
                            TextView nick = new TextView(getApplicationContext());
                            ImageView img = new ImageView(getApplicationContext());
                            nick.setText(nickname);
                            user.addView(nick);
                            img.setImageBitmap(bm);
                            user.addView(img);

                            CardView cv = findViewById(R.id.cardViewFeed);
                            LinearLayout cl = (LinearLayout)cv.getParent();
                            cl.addView(user);
                        }
                    });
                    break;
                default:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error, wrong populate feed response", Toast.LENGTH_SHORT).show();
                        }
                    });
            }
        } catch(final JSONException e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private Bitmap getImgFromServer(String in){
        try {
            URL urlConnection = new URL(in);
            HttpURLConnection connection = (HttpURLConnection) urlConnection
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
