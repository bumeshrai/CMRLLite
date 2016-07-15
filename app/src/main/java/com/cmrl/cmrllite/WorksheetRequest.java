package com.cmrl.cmrllite;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by umesh on 16/6/16.
 */
public class WorksheetRequest extends JsonObjectRequest {

    private static final String LOGIN_REQUEST_URL
            = "http://cmrlvent.co.in/assetMaint/api/web/";

    public WorksheetRequest (JSONObject parent, String urlParams, Response.Listener<JSONObject> responseListener, Response.ErrorListener errorListener){
        super(Request.Method.POST, LOGIN_REQUEST_URL + urlParams + "/create", parent, responseListener, errorListener);
        //Log.i("here", "Reached WSRequest");
    }
}
