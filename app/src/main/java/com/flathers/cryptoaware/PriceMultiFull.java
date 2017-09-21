package com.flathers.cryptoaware;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
/**
 * Created by flath on 9/20/2017.
 */

public class PriceMultiFull extends Price {
    private final String[] coins;
    private final String exchange;
    private static final String TAG = PriceMultiFull.class.getName();
    private Context mContext;
    private String url = "https://min-api.cryptocompare.com/data/pricemultifull?";

    public PriceMultiFull(Context mContext, String[] coins, String exchange){
        this.coins = coins;
        this.exchange = exchange;
        this.mContext = mContext;
        this.setURL();
    }

    public String getURL(){
        return url;
    }

    private void setURL(){
        String coinsChunk = "fsyms=";
        for(String coin : coins){
            coinsChunk += (coin + ",");
        }
        coinsChunk = coinsChunk.substring(0,coinsChunk.length() - 1);
        url += (coinsChunk + "&tsyms=BTC&e=" + exchange);
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
