package com.darktech.flfes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.darktech.flfes.TApplication;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class loginActivity extends Activity {

    private Socket tsocket;
    private EditText em;
    private EditText pass;
    private Button btn;
    private String email;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        em = findViewById(R.id.editTextLEmail);
        pass = findViewById(R.id.editTextLPass);
        btn = findViewById(R.id.buttonSendLogin);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendloginData();
            }
        });

        TApplication app = (TApplication) getApplication();
        tsocket = app.getSocket();
        tsocket.on("loginResponse", onloginResponse);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tsocket.off("loginResponse", onloginResponse);
    }

    private void sendloginData(){
        em.setError(null);
        pass.setError(null);

        email = em.getText().toString().trim(); //TODO: validar caracteres en login y que no este vacia
        password = pass.getText().toString().trim();

        if(email.contains(" ")) {
            em.setError(getString(R.string.phoneerror));
            return;
        }
        if(password.length() < 6){
            pass.setError(getString(R.string.passerror));
            return;
        }

        JSONObject sender = new JSONObject();
        try{
            sender.put("email", email);
            sender.put("password", password);
        } catch(JSONException e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        tsocket.emit("attemptLogin", sender);
    }

    private void loginresponse(final JSONObject in){
        int res;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                em.setError(null);
                pass.setError(null);
            }
        });
        try{
            res = in.getInt("response");
            switch(res) {
                case 0:
                    Intent profile = new Intent(getApplicationContext(), com.darktech.flfes.ProfileActivity.class);
                    profile.putExtra("email", email);
                    startActivity(profile);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Inicio de sesion correcto", Toast.LENGTH_SHORT).show();
                        }
                    });

                    finish();
                    break;
                case 1:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            em.setError("Usuario no existe");
                            em.requestFocus();
                        }
                    });
                    break;
                case 2:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            pass.setError("Contraseña incorrecta");
                            pass.requestFocus();
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

    private Emitter.Listener onloginResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject) args[0];
            loginresponse(response);
        }
    };
}
