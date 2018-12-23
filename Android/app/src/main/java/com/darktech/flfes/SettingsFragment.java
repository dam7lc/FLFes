package com.darktech.flfes;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import io.socket.client.Socket;

public class SettingsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        Button btnCerrarSesion = view.findViewById(R.id.buttonCerrarSesion);
        btnCerrarSesion.setOnClickListener(listenerBtnCerrarSesion);
        return view;
    }

    private View.OnClickListener listenerBtnCerrarSesion = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
            sp.edit().putBoolean("logged", false).apply();
            TApplication app = (TApplication) getActivity().getApplication();
            Socket tsocket = app.getSocket();
            tsocket.disconnect();
            Intent intent = new Intent(getActivity().getApplicationContext(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    };
}
