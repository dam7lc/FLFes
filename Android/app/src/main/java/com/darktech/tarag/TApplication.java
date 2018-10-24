package com.darktech.tarag;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class TApplication extends Application {
    String server = "http://192.168.0.9:3000";

    private Socket tsocket; {
        try {
            tsocket = IO.socket(server);
        } catch (URISyntaxException e){
            throw new RuntimeException(e);
        }
    }

    public void setServer(String in){
        StringBuilder serverBuilder = new StringBuilder();
        serverBuilder.append("http://");
        serverBuilder.append(in);
        serverBuilder.append(":3000");
        String servers = serverBuilder.toString();
        server = servers;

        try{
            tsocket = IO.socket(server);
        } catch(URISyntaxException e){
            throw new RuntimeException(e);
        }

    }



    public String getServer(){ return server;}

    public Socket getSocket() {
        return tsocket;
    }
}
