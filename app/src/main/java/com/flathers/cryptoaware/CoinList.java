package com.flathers.cryptoaware;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by flath on 9/24/2017.
 */

public class CoinList extends CryptoCompare {
    private static final String TAG = CoinList.class.getName();
    private String url = "https://www.cryptocompare.com/api/data/coinlist/";
    private Context mContext;
    public ArrayList<String> coinArrayList = new ArrayList<String>();

    public CoinList(Context mContext){
        this.mContext = mContext;
        sendRequest();
    }

    private String getUrl(){ return url; }

    public void sendRequest(){
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                //Log that response is successful
                Log.i(TAG, "Successful Response: " + response);

                //Generate the coinArrayList from the response
                try {
                    JSONObject body = (JSONObject) new JSONTokener(response.toString()).nextValue();
                    setCoinArrayList(body.getJSONObject("Data"));
                } catch(JSONException e){
                    Log.i(TAG, "Response body parsing eror");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                toast(mContext, "Connection error");
                Log.i(TAG, "Error: " + error.toString());
            }
        });
        requestQueue.add(jsObjRequest);
    }

    public ArrayList<String> getCoinArrayList(){ return coinArrayList; }

    private void setCoinArrayList(JSONObject jsonObject){
        /*
        * jsonObject: "Data" from API coinlist
        */
        Iterator<String> iter = jsonObject.keys();
        while(iter.hasNext()){
            String key = (String) iter.next();
            try{
                JSONObject nextCoin = (JSONObject) jsonObject.getJSONObject(key);
                coinArrayList.add((String) nextCoin.get("Name"));
            }catch(JSONException e){
                Log.i(TAG, "Error generating coinList");
                e.printStackTrace();
            }
        }
    }
}
