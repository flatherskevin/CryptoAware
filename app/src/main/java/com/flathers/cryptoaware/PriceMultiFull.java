package com.flathers.cryptoaware;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.util.Log;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by flath on 9/20/2017.
 */

public class PriceMultiFull extends Price {
    private ArrayList<String> coins = new ArrayList<String>();
    //private final String exchange;
    private static final String TAG = PriceMultiFull.class.getName();
    private static final String TSYM = "BTC";
    private Context mContext;
    private String url = "https://min-api.cryptocompare.com/data/pricemultifull?";
    private WatchingCoinsDb watchingCoinsDb;
    private SQLiteDatabase db;
    private String primaryKeyValue;
    private final String TABLE_NAME;

    public PriceMultiFull(Context mContext, ArrayList<String> coins){
        this.coins = coins;
        //this.exchange = exchange;
        this.mContext = mContext;
        this.setURL();
        this.watchingCoinsDb = new WatchingCoinsDb(mContext);
        this.db = watchingCoinsDb.getWritableDatabase();
        //this.contentValues = new ContentValues();
        this.primaryKeyValue = "";
        this.TABLE_NAME = watchingCoinsDb.TABLE_NAME;
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
        url += (coinsChunk + "&tsyms=" + TSYM);
        Log.i(TAG, "url: " + url);
    }

    public void sendRequest(){
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest(
                Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                //Parse JSON response and add to WatchingCoinsDB
                try {
                    JSONObject body = (JSONObject) new JSONTokener(response.toString()).nextValue();

                    //Loop through RAW and DISPLAY data of all coins
                    for (String coin : coins) {
                        ContentValues contentValues = new ContentValues();
                        ContentValues rawContentValues = manageDbRows(contentValues, body.getJSONObject("RAW").getJSONObject(coin), "RAW");
                        ContentValues totalContentValues = manageDbRows(rawContentValues, body.getJSONObject("DISPLAY").getJSONObject(coin), "DISPLAY");
                        int callback = (int) db.insertWithOnConflict(TABLE_NAME, null, totalContentValues, SQLiteDatabase.CONFLICT_IGNORE);
                        if(callback == -1){
                            db.update(TABLE_NAME, totalContentValues, WatchingCoinsDb.RAW_FROMSYMBOL + "='" + primaryKeyValue +"'", null);
                        }
                    }
                } catch (JSONException e){
                    Log.i(TAG, "JSONObjectRequest error: " + e.toString());
                    e.printStackTrace();
                }
                Log.i(TAG, "Successful Response: " + response);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //TODO: add toast when there is a network error
                toast(mContext, "Connection error");
                Log.i(TAG, "Error: " + error.toString());
            }
        });

        //Add request to the queue
        requestQueue.add(jsObjRequest);
    }

    public ContentValues manageDbRows(ContentValues contentValues, JSONObject jsonObject, String prefix){

        //Loop through JSON structure to gather data
        try {

            //Account for the TOSYMBOL nested JSON
            JSONObject tsym = jsonObject.getJSONObject(TSYM);
            Log.i(TAG, "tsym: " + tsym.toString());
            Iterator<String> innerIter = tsym.keys();
            while (innerIter.hasNext()) {
                String innerKey = (String) innerIter.next();
                try {
                    String thisData = tsym.get(innerKey).toString();

                    String compoundColumn = prefix + "_" + innerKey;

                    //Put in value proper column
                    contentValues.put(compoundColumn, thisData);
                    Log.i(TAG, "ContentValue added - " + compoundColumn + ": " + thisData);
                    if(compoundColumn.equals("RAW_FROMSYMBOL")){
                        primaryKeyValue = thisData;
                    }
                } catch (JSONException e) {
                    Log.i(TAG, "Error with inner coin loop: " + e.toString());
                    e.printStackTrace();
                }
            }
        } catch (JSONException e){
            Log.i(TAG, "Error with tsym: " + e.toString());
            e.printStackTrace();
        }
        return contentValues;
    }
}
