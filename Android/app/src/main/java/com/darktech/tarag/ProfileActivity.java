package com.darktech.flfes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.darktech.flfes.TApplication;
import com.darktech.flfes.signUpProfileActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import io.socket.client.Socket;
import io.socket.client.Url;
import io.socket.emitter.Emitter;

public class ProfileActivity extends Activity {

    private ImageView imgProfile;
    private TextView textNick;
    private TextView textEmail;
    private TextView textSex;
    private TextView textCareer;
    private TextView textAge;
    private TextView textDescription;
    private Button btnEditProfile;

    private String phone;
    private Socket tsocket;
    private TApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent inProfile = getIntent();
        phone = inProfile.getStringExtra("phone");

        imgProfile = findViewById(R.id.imageViewProfile);
        textNick = findViewById(R.id.textViewNick);
        textEmail = findViewById(R.id.textViewEmail);
        textSex = findViewById(R.id.textViewSex);
        textCareer = findViewById(R.id.textViewCareer);
        textAge = findViewById(R.id.textViewAge);
        textDescription = findViewById(R.id.textViewDescription);

        Button btnEditProfile = findViewById(R.id.buttonProfileEdit);

        app = (TApplication)getApplication();
        tsocket = app.getSocket();
        tsocket.emit("getProfileInfo", phone);
        tsocket.on("getProfileInfoResponse", ongetProfileInfoResponse);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(getApplicationContext(), signUpProfileActivity.class);
                edit.putExtra("phone", phone);
                edit.putExtra("first", false);
                startActivity(edit);
            }
        });
    }

    private Emitter.Listener ongetProfileInfoResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject)args[0];
            getProfileInfoResponse(response);
        }
    };

    private void getProfileInfoResponse(final JSONObject in){
        int res;
        try{
            res = in.getInt("response");
            String imgUrl = in.getString("img");
            final String nick = in.getString("nick");
            final String email = in.getString("email");
            final String sex = in.getString("sex");
            final String career = in.getString("career");
            final String age = in.getString("age");
            final String descr = in.getString("descr");

            switch(res){
                case 0:
                    StringBuilder url = new StringBuilder();
                    String server = app.getServer();
                    url.append(server);
                    url.append(imgUrl);
                    final Bitmap bm = getImgFromServer(url.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgProfile.setImageBitmap(bm);
                            textNick.setText(nick);
                            textEmail.setText(email);
                            textSex.setText(sex);
                            textCareer.setText(career);
                            textAge.setText(age);
                            textDescription.setText(descr);
                        }
                    });
                    break;
            }
        } catch (final JSONException e){
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
