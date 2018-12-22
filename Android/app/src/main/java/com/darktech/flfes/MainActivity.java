package com.darktech.flfes;

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

import com.darktech.flfes.TApplication;

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
                tsocket.on("loginResponse", onLoginResponse);
                tsocket.connect();

                sp = getSharedPreferences("login", MODE_PRIVATE);

                if(sp.getBoolean("logged", false))
                {
                    String username = sp.getString("email", "");
                    String password = sp.getString("password", "");
                    JSONObject sender = new JSONObject();
                    try{
                        sender.put("email", username);
                        sender.put("password", password);
                    } catch(JSONException e)
                    {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                    tsocket.emit("attemptLogin", sender); //TODO: descomentar para iniciar sesion automaticamente
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
                Intent signup = new Intent(getApplicationContext(), com.darktech.flfes.signUpActivity.class);
                startActivityForResult(signup,0);
                //finish();
            }
        });

        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.startAnimation(animfadein);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent login = new Intent(getApplicationContext(), com.darktech.flfes.loginActivity.class);
                startActivityForResult(login,1);
                //finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            String email = data.getStringExtra("email");
            String pass = data.getStringExtra("password");
            sp.edit().putString("email", email).apply();
            sp.edit().putString("password", pass).apply();
            sp.edit().putBoolean("logged", true).apply();
        }
        switch(requestCode){
            case 0:
                break;
            case 1:
                String email = data.getStringExtra("email");
                Intent profile = new Intent(getApplicationContext(), FeedActivity.class);
                profile.putExtra("email", email);
                startActivity(profile);
                finish();
                break;
            default:
                break;
        }
    }


    private Emitter.Listener onLoginResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject) args[0];
                    loginresponse(response);
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
        tsocket.off(Socket.EVENT_CONNECT, onConnect);
        tsocket.off(Socket.EVENT_DISCONNECT, onDisconnect);
        tsocket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
        tsocket.off(Socket.EVENT_CONNECT_TIMEOUT, onConnectError);
        tsocket.off("loginResponse", onLoginResponse);
    }

    private void loginresponse(JSONObject response){
        int res;
        String email = sp.getString("email", "");
        try{
            res = response.getInt("response");
            switch(res) {
                case 0:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent profile = new Intent(getApplicationContext(), FeedActivity.class);
                    profile.putExtra("email", email);
                    startActivity(profile);
                    finish();
                    break;
                case 1:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "NombreIncorrecto", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 2:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "ContraseñaIncorrecta", Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case 3:
                    Intent signup = new Intent(this, com.darktech.flfes.signUpProfileActivity.class);
                    signup.putExtra("email", email);
                    startActivity(signup);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Falta completar el perfil", Toast.LENGTH_LONG).show();
                        }
                    });
                    finish();
                    break;
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
}
