package com.darktech.flfes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.darktech.flfes.TApplication;

import org.json.JSONException;
import org.json.JSONObject;


import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FeedActivity extends Activity {

    Socket tsocket;
    String email;
    TApplication app;
    LinearLayout llfeed;
    Button btnOffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        llfeed = findViewById(R.id.linearLayoutFeed);
        btnOffer = findViewById(R.id.buttonNuevaOferta);



        Intent intentFrom = getIntent();
        email = intentFrom.getStringExtra("email");

        app = (TApplication) getApplication();
        tsocket = app.getSocket();
        tsocket.on("populateOffersResponse", onPopulateOffersResponse);

        JSONObject info = new JSONObject();
        try {
            info.put("email", email);
            tsocket.emit("populateOffers", info);
        } catch (final JSONException e){
            Log.i("Error", e.toString());
        }

        btnOffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newOffer = new Intent(getApplicationContext(), NewOfferActivity.class);
                newOffer.putExtra("email", email);
                startActivity(newOffer);
                finish();
            }
        });
    }

    Emitter.Listener onPopulateOffersResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject)args[0];
            populateOffersResponse(response);
        }
    };

    private void populateOffersResponse(JSONObject in){
        try{
            int response = in.getInt("response");
            switch(response){
                case 0:
                    final String titulo = in.getString("titulo");
                    final String de = in.getString("de");
                    final String materia = in.getString("materia");
                    final String tema = in.getString("tema");
                    final String descripcion = in.getString("descripcion");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CardView offer = new CardView(getApplicationContext());
                            offer.setRadius(20);
                            offer.setElevation(20);
                            LinearLayout ll = new LinearLayout(getApplicationContext());
                            ll.setOrientation(LinearLayout.VERTICAL);
                            TextView tit = new TextView(getApplicationContext());
                            TextView mat = new TextView(getApplicationContext());
                            tit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                            tit.setText(titulo);
                            tit.setGravity(Gravity.CENTER);
                            tit.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            mat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                            mat.setText(materia);
                            mat.setGravity(Gravity.CENTER);
                            ll.addView(tit);
                            ll.addView(mat);
                            offer.addView(ll);
                            llfeed.addView(offer);
                        }
                    });
                    break;
                case 1:
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CardView info = new CardView(getApplicationContext());
                            TextView txtinfo = new TextView(getApplicationContext());
                            txtinfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                            txtinfo.setText("No hay ofertas disponibles");
                            info.addView(txtinfo);
                            llfeed.addView(info);
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
}
