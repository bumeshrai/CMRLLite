package com.cmrl.cmrllite;

/*
* This prepares the POST array for the Volley request
* B. Umesh Rai
 */
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class UserLoginRequest  extends StringRequest {

    private static final String LOGIN_REQUEST_URL = "http://cmrlvent.co.in/assetMaint/api/web/user/login";
    private Map<String, String> params;

    public UserLoginRequest(String username, String password,  String latitude, String longitude ,Response.Listener<String> listener) {
        // Request Post and pass on URL
        super(Request.Method.POST, LOGIN_REQUEST_URL, listener, null);
        //POST Array
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("latitude", latitude);
        params.put("longitude", longitude);
    }

    @Override
    public Map<String, String> getParams() { //Overide method
        return params;
    }
}
