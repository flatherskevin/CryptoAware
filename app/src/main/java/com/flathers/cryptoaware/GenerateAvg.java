package com.flathers.cryptoaware;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

/**
 * Created by flath on 9/17/2017.
 */

public class GenerateAvg extends Price {
    private final String coin;
    private final String[] exchanges;
    private static final String TAG = GenerateAvg.class.getName();
    private Context mContext;
    private String url = "https://min-api.cryptocompare.com/data/generateAvg?";

    public GenerateAvg(Context mContext, String coin, String[] exchanges){
        this.coin = coin;
        this.exchanges = exchanges;
        this.mContext = mContext;
        this.setURL();
    }

    public String getURL(){
        return url;
    }

    private void setURL(){
        String exchangeChunk = "markets=";
        for(String exchange : exchanges){
            exchangeChunk += (exchange + ",");
        }
        exchangeChunk = exchangeChunk.substring(0,exchangeChunk.length() - 1);
        url += (coin + "&tsym=BTC&" + exchangeChunk);
        Log.i(TAG, "url: " + url);
    }

    public void sendRequest(){
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                //TODO: it might be best to perform SQl additions here
                Log.i(TAG, "Successful Response: " + response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: add toast when there is a network error
                Log.i(TAG, "Error: " + error.toString());
            }
        });
        requestQueue.add(jsObjRequest);
    }
}
