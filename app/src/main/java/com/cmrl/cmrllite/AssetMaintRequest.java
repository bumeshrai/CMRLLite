package com.cmrl.cmrllite;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

public class AssetMaintRequest extends StringRequest {

    private static final String LOGIN_REQUEST_URL
            = "http://cmrlvent.co.in/assetMaint/api/web/";

    public AssetMaintRequest (String url, Response.Listener<String> listener, Response.ErrorListener errorListener){
        super(Request.Method.GET, LOGIN_REQUEST_URL + url, listener, errorListener);
        //Log.i("value1","Reached AssetMaintRequest");
    }

}

