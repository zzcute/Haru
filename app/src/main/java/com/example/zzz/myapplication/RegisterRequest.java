package com.example.zzz.myapplication;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zzz on 2017-09-02.
 */

public class RegisterRequest extends StringRequest {
    final static private String URL = "http://zzcute.cafe24.com/Register.php";
    private Map<String, String> parameters;

    public RegisterRequest(String userEmail, String userID, String userPassword, String Verify, String imageSource, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);
        parameters = new HashMap<>();
        parameters.put("userEmail",userEmail);
        parameters.put("userID",userID);
        parameters.put("userPassword",userPassword);
        parameters.put("Verify",Verify);
        parameters.put("imageSource",imageSource);
    }

    @Override
    public Map<String, String> getParams() {
        return parameters;
    }
}
