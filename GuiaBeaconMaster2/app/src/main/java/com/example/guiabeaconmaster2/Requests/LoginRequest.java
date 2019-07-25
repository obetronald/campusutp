package com.example.guiabeaconmaster2.Requests;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;


public class LoginRequest extends StringRequest {

    private static final String ruta = "http://beaconsobetay.000webhostapp.com/session.php";
    private Map<String, String> parametros;

    public LoginRequest(String correo, String password,  Response.Listener<String> listener){
        super(Request.Method.POST, ruta, listener, null);
        parametros = new HashMap<>();
        parametros.put("correo",correo+"");
        parametros.put("password",password+"");
    }

    @Override
    protected Map<String, String> getParams() {
        return parametros;
    }
}

