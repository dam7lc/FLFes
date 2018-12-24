package com.darktech.flfes;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;

import androidx.annotation.AnimatorRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class OffersFeedFragment extends Fragment {


    //LinearLayout llfeed;
    Button btnOffer;
    Socket tsocket;
    String email;
    TApplication app;
    private LinkedList<String[]> Offers = new LinkedList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_offers_feed, container, false);
        //llfeed = view.findViewById(R.id.linearLayoutFeed);
        btnOffer = view.findViewById(R.id.buttonNuevaOferta);
        Intent intentFrom = getActivity().getIntent();
        email = intentFrom.getStringExtra("email");

        app = (TApplication) getActivity().getApplication();
        tsocket = app.getSocket();
        tsocket.on("populateOffersResponse", onPopulateOffersResponse);
        tsocket.on("finishPopulatingOffers", onFinishPopulatingOffers);

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
                Intent newOffer = new Intent(getActivity().getApplicationContext(), NewOfferActivity.class);
                newOffer.putExtra("email", email);
                startActivity(newOffer);
            }
        });
        return view;
    }

    Emitter.Listener onPopulateOffersResponse = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject)args[0];
            populateOffersResponse(response);
        }
    };

    Emitter.Listener onFinishPopulatingOffers = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject response = (JSONObject) args[0];
            finishPopulatingOffers(response);
        }
    };

    private void finishPopulatingOffers(JSONObject in){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RecyclerView offersRecView;
                OfferAdapter offersAdapter;

                offersRecView = getActivity().findViewById(R.id.recyclerViewOffers);
                offersAdapter = new OfferAdapter(getActivity(), Offers);
                offersRecView.setAdapter(offersAdapter);
                offersRecView.setLayoutManager(new LinearLayoutManager(getActivity()));
            }
        });

    }

    private void populateOffersResponse(JSONObject in){
        try{
            int response = in.getInt("response");
            switch(response){
                case 0:
                    final String titulo = in.getString("titulo");
                    final String materia = in.getString("materia");
                    if(isAdded()){
                        String[] info = {titulo, materia};
                        Offers.addLast(info);

                        /*getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final CardView offer = new CardView(getActivity().getApplicationContext());
                                offer.setRadius(20);
                                offer.setElevation(20);
                                offer.setCardBackgroundColor(getResources().getColor(R.color.cardBackground));
                                LinearLayout ll = new LinearLayout(getActivity().getApplicationContext());
                                ll.setOrientation(LinearLayout.VERTICAL);
                                com.darktech.flfes.MaliFontTextView tit = new com.darktech.flfes.MaliFontTextView(getActivity().getApplicationContext());
                                com.darktech.flfes.MaliFontTextView mat = new com.darktech.flfes.MaliFontTextView(getActivity().getApplicationContext());
                                tit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                tit.setText(titulo);
                                tit.setGravity(Gravity.CENTER);
                                tit.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                                mat.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                                mat.setText(materia);
                                mat.setGravity(Gravity.CENTER);
                                mat.setTextColor(getResources().getColor(R.color.textCardMate));
                                ll.addView(tit);
                                ll.addView(mat);
                                offer.addView(ll);
                                llfeed.addView(offer);

                                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) offer.getLayoutParams();
                                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                                int pixels = Math.round(10 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

                                layoutParams.setMargins(pixels , pixels, pixels, 0);
                                offer.requestLayout();

                                offer.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                offer.setStateListAnimator(AnimatorInflater.loadStateListAnimator(getActivity().getApplicationContext(), R.animator.on_card_click));
                                            }
                                        });
                                    }
                                });
                            }
                        });*/
                    }
                    break;
                case 1:
                    if(isAdded()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               /* CardView info = new CardView(getActivity().getApplicationContext());
                                TextView txtinfo = new TextView(getActivity().getApplicationContext());
                                txtinfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);
                                txtinfo.setText("No hay ofertas disponibles");
                                info.addView(txtinfo);
                                llfeed.addView(info);*/
                            }
                        });
                    }
                    break;
                default:
                    if(isAdded()){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getActivity().getApplicationContext(), "Error, wrong populate feed response", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
            }
        } catch(final JSONException e){
            if(isAdded()){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity().getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
