package com.darktech.tarag;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MainActivity extends Activity {

    private Socket tsocket;
    private TextView Txtserver;
    private Button connectBtn;
    private TextView txtIntro;
    private TextView txtDescr;
    private Button btnLogin;
    private Button btnSignUp;
    private Animation animfadein;
    private SharedPreferences sp;
    private boolean isConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Txtserver = findViewById(R.id.editTextServer);
        connectBtn = findViewById(R.id.buttonConnect);

        connectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = Txtserver.getText().toString().trim();

                TApplication app = (TApplication) getApplication();
                app.setServer(ip);

                tsocket = app.getSocket();
                tsocket.on(Socket.EVENT_CONNECT, onConnect);
                tsocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
                tsocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
                tsocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
                tsocket.on("login", onLogin);
                tsocket.on("loginResponse", onLoginResponse);
                tsocket.connect();

                sp = getSharedPreferences("login", MODE_PRIVATE);

                if(sp.getBoolean("logged", false))
                {
                    String username = sp.getString("user", "");
                    String password = sp.getString("pass", "");
                    JSONObject sender = new JSONObject();
                    try{
                        sender.put("username", username);
                        sender.put("password", password);
                    } catch(JSONException e)
                    {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    //tsocket.emit("attemptLogin", sender); //TODO: descomentar para iniciar sesion automaticamente
                }
            }
        });




        animfadein = AnimationUtils.loadAnimation(this, R.anim.fadein);

        txtIntro = findViewById(R.id.textIntro);
        txtIntro.startAnimation(animfadein);

        txtDescr = findViewById(R.id.textDescription);
        txtDescr.startAnimation(animfadein);

        btnSignUp = findViewById(R.id.btnRegister);
        btnSignUp.startAnimation(animfadein);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signup = new Intent(getApplicationContext(), signUpActivity.class);
                startActivityForResult(signup,0);
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.startAnimation(animfadein);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent login = new Intent(getApplicationContext(), loginActivity.class);
                startActivityForResult(login,1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            String user = data.getStringExtra("username");
            String pass = data.getStringExtra("password");
            sp.edit().putString("user", user).apply();
            sp.edit().putString("pass", pass).apply();
            sp.edit().putBoolean("logged", true).apply();
        }
    }


    private Emitter.Listener onLoginResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject) args[0];
            int res;
            try{
                res = response.getInt("response");
                if(res == 0) {
                    loginresponse(true);
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
    };

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(!isConnected) {
                        Toast.makeText(getApplicationContext(), R.string.connect, Toast.LENGTH_SHORT).show();
                        isConnected = true;
                    }
                }
            });
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isConnected = false;
                    Toast.makeText(getApplicationContext(),
                            R.string.disconnect, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            tsocket.disconnect();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),
                            R.string.error_connect, Toast.LENGTH_SHORT).show();
                }
            });
        }
    };



    private Emitter.Listener onLogin = new Emitter.Listener() {

        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            String recTxt;
            try{
                recTxt = data.getString("txt");
            } catch (JSONException e) {
                return;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tsocket.disconnect();
        tsocket.off("login", onLogin);
        tsocket.off(Socket.EVENT_CONNECT, onConnect);
        tsocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        tsocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        tsocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
    }

    private void loginresponse(Boolean in){
        //Intent logged = new Intent(this)
    }
}
