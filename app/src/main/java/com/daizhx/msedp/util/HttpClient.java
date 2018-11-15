package com.daizhx.msedp.util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpClient {

    private static final HttpClient instance = new HttpClient();
    private OkHttpClient client;
    //local server ip
    private String serverHost = "192.168.1.77";
    private int serverPort = 8099;


    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private HttpClient(){
        client = new OkHttpClient();
    }

    public static final HttpClient getInstance(){
        return instance;
    }


    public void setServerHost(String serverHost) {
        this.serverHost = serverHost;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    private String genUrl(String api){
        return "http://" + serverHost + ":" + serverPort +"/" + api;
    }

    public void post(String api, String json,Callback callback){
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(genUrl(api))
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public void get(String api){
        Request request = new Request.Builder().url(genUrl(api)).build();
    }

}
