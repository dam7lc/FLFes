package com.darktech.flfes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class NewOfferActivity extends Activity {

    String email;
    TApplication app;
    Socket tsocket;
    EditText etTitulo;
    EditText etMateria;
    EditText etTema;
    EditText etDescripcion;
    Button btnEnviarOferta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_offer);

        etTitulo = findViewById(R.id.editTextTituloOferta);
        etMateria = findViewById(R.id.editTextMateriaOferta);
        etTema = findViewById(R.id.editTextTemaOferta);
        etDescripcion = findViewById(R.id.editTextDescripcionOferta);
        btnEnviarOferta = findViewById(R.id.buttonSendOffer);

        app = (TApplication) getApplication();
        tsocket = app.getSocket();

        tsocket.on("publishOfferResponse", onPublishOfferResponse);

        Intent from = getIntent();
        email = from.getStringExtra("email");

        btnEnviarOferta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    JSONObject info = new JSONObject();
                    info.put("email", email);
                    info.put("titulo", etTitulo.getText().toString().trim());
                    info.put("materia", etMateria.getText().toString().trim());
                    info.put("tema", etTema.getText().toString().trim());
                    info.put("descripcion", etDescripcion.getText().toString().trim());
                    tsocket.emit("publishOffer", info);
                } catch(JSONException e){
                    Log.i("Error", e.toString());
                }
            }
        });

    }

    Emitter.Listener onPublishOfferResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject)args[0];
            publishOfferResponse(response);
        }
    };

    void publishOfferResponse(JSONObject in){
        try{
            int res = in.getInt("response");
            switch(res){
                case 0:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Oferta publicada exitosamente", Toast.LENGTH_SHORT).show();
                            Intent feed = new Intent(getApplicationContext(), FeedActivity.class);
                            feed.putExtra("email", email);
                            startActivity(feed);
                            finish();
                        }
                    });
                    break;
                default:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "default response", Toast.LENGTH_LONG).show();
                        }
                    });
                    break;
            }
        } catch (JSONException e){
            Log.i("Error", e.toString());
        }
    }
}
