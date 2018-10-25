package com.darktech.flfes;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.JsonReader;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darktech.flfes.TApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class signUpProfileActivity extends Activity {

    private ImageView uploadImg;
    private EditText nick;
    private EditText phonetext;
    private Spinner spinnerCarrera;
    private EditText habilidades;
    private Button uploadBtn;
    private Button nextBtn;
    private Button nextQBtn;

    private TApplication app;
    private String email;
    private int RESULT_LOAD_IMAGE = 1;
    private Socket tsocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_profile);

        uploadImg = findViewById(R.id.imageViewUpload);
        nick = findViewById(R.id.editTextNick);
        phonetext = findViewById(R.id.editTextPhone);
        spinnerCarrera = findViewById(R.id.spinnerCareer);
        habilidades = findViewById(R.id.editTextDescr);
        uploadBtn = findViewById(R.id.buttonUploadImg);
        nextBtn = findViewById(R.id.buttonProfileNext);


        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendProfileInfo();
            }
        });


        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        app = (TApplication) getApplication();
        tsocket = app.getSocket();
        tsocket.on("uploadImgResponse", onUploadResponse);
        tsocket.on("profileInfoResponse", onProfileInfoResponse);

        Intent signup = getIntent();
        email = signup.getStringExtra("email");
        if(!signup.getBooleanExtra("first", true)){
            tsocket.emit("getProfileInfo", email);
            tsocket.on("getProfileInfoResponse", ongetProfileInfoResponse);
        }

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        tsocket.off("uploadImgResponse", onUploadResponse);
    }

    private void sendProfileInfo(){
        nick.setError(null);
        phonetext.setError(null);


        String nname = nick.getText().toString().trim();
        if(nname.isEmpty()){
            nick.setError("Por favor ingresa un nickname");
            nick.requestFocus();
            return;
        }
        String phone = phonetext.getText().toString().trim();
        if(phone.isEmpty()){
            phonetext.setError("Por favor ingresa un email");
            phonetext.requestFocus();
            return;
        }

        String career = spinnerCarrera.getSelectedItem().toString().trim();
        if(career.equals("Carrera")){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Error, no se ha seleccionado carrera", Toast.LENGTH_LONG).show();
                }
            });
            return;
        }

        String desc = habilidades.getText().toString().trim();
        if(desc.isEmpty()){
            habilidades.setError("Por favor ingresa tus habilidades");
            habilidades.requestFocus();
            return;
        }


        JSONObject info = new JSONObject();
        try {
            info.put("name", nname);
            info.put("email", email);
            info.put("career", career);
            info.put("desc", desc);
            info.put("tel", phone);
            tsocket.emit("profileInfo", info);
        } catch (final JSONException e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    private Emitter.Listener onProfileInfoResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject) args[0];
            profileInfoResponse(response);
        }
    };

    private Emitter.Listener onUploadResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject) args[0];
            uploadImgResponse(response);
        }
    };

    private Emitter.Listener ongetProfileInfoResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject) args[0];
            fillProfileInfo(response);
        }
    };

    private void fillProfileInfo(JSONObject in){
        try {
            int res = in.getInt("response");
            switch (res){
                case 0:
                    final String imgUrl = in.getString("img");
                    final String name = in.getString("name");
                    final String phone = in.getString("tel");
                    final String sex = in.getString("sexo");
                    final String career = in.getString("carrera");
                    //final String descr = in.getString("descr");

                    StringBuilder IMGURL = new StringBuilder();
                    String server = app.getServer();
                    IMGURL.append(server);
                    IMGURL.append(imgUrl);
                    final Bitmap bm = getImgFromServer(IMGURL.toString());

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            uploadImg.setImageBitmap(bm);
                            nick.setText(name);
                            phonetext.setText(phone);

                           /* if(sex.equals("Hombre")){
                                sexo.check(R.id.radioButtonMale);
                            }
                            else if(sex.equals("Mujer")){
                                sexo.check(R.id.radioButtonFemale);
                            }
                            else{
                                sexo.check(R.id.radioButtonOther);
                            }*/
                            boolean selected = false;
                            int count = 0;
                            while(!selected){
                                if(career.equals(spinnerCarrera.getItemAtPosition(count).toString().trim())){
                                    selected = true;
                                    spinnerCarrera.setSelection(count);
                                }
                                count++;
                            }
                            //habilidades.setText();
                        }
                    });
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

    private void profileInfoResponse(JSONObject in){
        try {
            int res;
            res = in.getInt("response");
            switch(res){
                case 0:
                    Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                    profile.putExtra("email", email);
                    startActivity(profile);


                    break;
                default:

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

    private void uploadImgResponse(JSONObject in){
        try{
            int res;
            res = in.getInt("response");
            if(res == 0){
                String imgUrl = in.getString("img");
                StringBuilder URL = new StringBuilder();
                String server = app.getServer();
                URL.append(server);
                URL.append(imgUrl);
                final Bitmap bm = getImgFromServer(URL.toString());
                if(bm != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploadImg.setImageBitmap(bm);
                        }
                    });
                }
            }

        } catch (JSONException e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            "wrong login response", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            try{

                Bitmap bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(),selectedImage);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                byte[] b = baos.toByteArray();
                String encoded = Base64.encodeToString(b, Base64.DEFAULT);

                JSONObject sender = new JSONObject();
                try {
                    sender.put("email", email);
                    sender.put("img", encoded);
                } catch (JSONException e){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "error sending", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                tsocket.emit("uploadImg", sender);
                return;

            } catch (IOException e) {
                e.printStackTrace();
            }
            String failed = "failed";
            tsocket.emit("uploadImg", failed);
        }
    }
}
