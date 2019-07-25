package com.example.guiabeaconmaster2.Activitys;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.guiabeaconmaster2.R;
import com.example.guiabeaconmaster2.Requests.RegistrarRequest;

import org.json.JSONException;
import org.json.JSONObject;


public class Registrar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        final EditText nombreT     = (EditText)findViewById(R.id.nombreRegistro);
        final EditText apellidoT   = (EditText)findViewById(R.id.apellidoRegistro);
        final EditText telefonoT     = (EditText)findViewById(R.id.telefonoRegistro);
        final EditText correoT    = (EditText)findViewById(R.id.correoRegistro);
        final EditText passwordT      = (EditText)findViewById(R.id.passwordRegistro);

        Button btnregistro = (Button)findViewById(R.id.btnRegistro);

        btnregistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre     = nombreT.getText().toString() ;
                String apellido   = apellidoT.getText().toString() ;
                String telefono   = telefonoT.getText().toString();
                String correo     = correoT.getText().toString() ;
                String password   = passwordT.getText().toString() ;
                Response.Listener<String> respuesta = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonRespuesta = new JSONObject(response);
                            boolean ok = jsonRespuesta.getBoolean("success");
                            if (ok == true){
                                Intent i = new Intent(Registrar.this, Login.class);
                                Registrar.this.startActivity(i);
                                AlertDialog.Builder alerta = new AlertDialog.Builder(Registrar.this);
                                alerta.setMessage("Usuario Registrado");
                                Registrar.this.finish();
                            }else{
                                AlertDialog.Builder alerta = new AlertDialog.Builder(Registrar.this);
                                alerta.setMessage("Registro fallido")
                                        .setNegativeButton("intenta nuevamente", null)
                                        .create()
                                        .show();
                            }

                        }catch (JSONException e){
                            e.getMessage();
                        }
                    }
                };

                RegistrarRequest r = new RegistrarRequest(nombre,apellido,telefono,correo,password,respuesta);
                RequestQueue cola = Volley.newRequestQueue(Registrar.this);
                cola.add(r);
            }
        });
    }
}

