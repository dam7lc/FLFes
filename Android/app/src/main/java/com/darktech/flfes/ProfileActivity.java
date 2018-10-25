package com.darktech.flfes;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darktech.flfes.TApplication;
import com.darktech.flfes.signUpProfileActivity;

import org.json.JSONArray;
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
    private TextView textPhone;
    private TextView textSex;
    private TextView textCareer;
    private TextView textAge;
    private TextView textHabs;
    private Button btnEditProfile;
    private LinearLayout llhabsprofile;

    private String email;
    private Socket tsocket;
    private TApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent inProfile = getIntent();
        email = inProfile.getStringExtra("email");

        imgProfile = findViewById(R.id.imageViewProfile);
        textNick = findViewById(R.id.textViewNick);
        textPhone = findViewById(R.id.textViewPhone);
        textCareer = findViewById(R.id.textViewCareer);
        llhabsprofile = findViewById(R.id.llhabsprofile);

        Button btnEditProfile = findViewById(R.id.buttonProfileEdit);

        app = (TApplication)getApplication();
        tsocket = app.getSocket();
        tsocket.emit("getProfileInfo", email);
        tsocket.on("getProfileInfoResponse", ongetProfileInfoResponse);

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(getApplicationContext(), signUpProfileActivity.class);
                edit.putExtra("email", email);
                edit.putExtra("first", false);
                startActivity(edit);
                finish();
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
            final String name = in.getString("name");
            final String phone = in.getString("tel");
            final String career = in.getString("career");
            final JSONArray habs = in.getJSONArray("habs");

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
                            for(int i = 0; i < habs.length(); i++){
                                try {
                                    TextView newHab = new TextView(getApplicationContext());
                                    newHab.setText(habs.getString(i));
                                    newHab.setTextColor(getResources().getColor(R.color.textColor));
                                    newHab.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                    llhabsprofile.addView(newHab);
                                } catch(JSONException e){
                                    Log.i("error", e.toString());
                                }
                            }
                            imgProfile.setImageBitmap(bm);
                            textNick.setText(name);
                            textPhone.setText(phone);
                            textCareer.setText(career);
                            //textHabs.setText();
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
