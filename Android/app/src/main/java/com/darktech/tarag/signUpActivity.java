package com.darktech.tarag;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class signUpActivity extends Activity {

    private Socket tsocket;
    private String phone;
    private String password;
    private String password2;
    private EditText pass;
    private EditText phn;
    private EditText pass2;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        //TODO cambiar por email
        pass = (EditText)findViewById(R.id.editTextPass);
        pass2 = (EditText)findViewById(R.id.editTextPass2);
        phn = (EditText)findViewById(R.id.editTextUserName);
        btn = (Button)findViewById(R.id.buttonSendSignup);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSignUpData();
            }
        });
        TApplication app = (TApplication) getApplication();
        tsocket = app.getSocket();

        tsocket.on("signupResponse", onsignupResponse);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        tsocket.off("signupResponse", onsignupResponse);
    }

    private Emitter.Listener onsignupResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject) args[0];
            int resp;
            try{
                resp = response.getInt("response");
                if(resp == 0) {
                    signupresponse(true, "Registro exitoso");
                }
                else if(resp == 1)
                {
                    String error = response.getString("error");
                    signupresponse(false , error);
                }
            } catch (JSONException e)
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "wrong signup response", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };



    private void sendSignUpData(){
        phn.setError(null);
        pass.setError(null);
        pass2.setError(null);

        phone = phn.getText().toString().trim(); //TODO: validar caracteres en signup y que no este vacia
        password = pass.getText().toString().trim();
        password2 = pass2.getText().toString().trim();

        if(phone.contains(" ")){
            phn.setError(getString(R.string.phoneerror));
            phn.requestFocus();
            return;
        }

        if(!password2.equals(password)){
            pass2.setError(getString(R.string.pass2error));
            pass2.requestFocus();
            return;
        }
        else if(password.length() < 6)
        {
            pass.setError(getString(R.string.passerror));
            return;
        }

        JSONObject sender = new JSONObject();
        try{
            sender.put("phone", phone);
            sender.put("password", password);
        } catch(JSONException e)
        {
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
        tsocket.emit("attemptSignup", sender);
    }

    private void signupresponse(Boolean in, String Sin) {
        final String S = Sin;
        if(in) {
            /*Intent result = new Intent();
            result.putExtra("username", username);
            result.putExtra("password", password);
            setResult(RESULT_OK, result);*/
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), S,Toast.LENGTH_SHORT).show();
                }
            });
            Intent signup = new Intent(this, signUpProfileActivity.class);
            signup.putExtra("phone", phone);
            startActivity(signup);
            finish();
        }
        else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    phn.setError(S);
                    phn.requestFocus();
                }
            });
        }
    }
}
