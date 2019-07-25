package com.example.guiabeaconmaster2.Requests;

import com.android.volley.toolbox.StringRequest;
import android.app.DownloadManager;
import android.support.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

public class RegistrarRequest extends StringRequest {


    private static final String ruta = "http://beaconsobetay.000webhostapp.com/register.php";
    private Map<String, String> parametros;

    public RegistrarRequest(String nombre, String apellido, String telefono, String correo, String password, Response.Listener<String> listener){
        super(Request.Method.POST, ruta, listener, null);

        parametros = new HashMap<>();
        parametros.put("nombre",nombre+"");
        parametros.put("apellido",apellido+"");
        parametros.put("telefono",telefono+"");
        parametros.put("correo",correo+"");
        parametros.put("password",password+"");
    }

    @Override
    protected Map<String, String> getParams() {
        return parametros;
    }
}


