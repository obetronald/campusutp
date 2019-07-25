package com.example.guiabeaconmaster2.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.example.guiabeaconmaster2.Entitys.Hora;
import com.example.guiabeaconmaster2.Entitys.Horario;
import com.example.guiabeaconmaster2.R;
import com.example.guiabeaconmaster2.Util.HoraAdapter;
import com.example.guiabeaconmaster2.Util.HorarioAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DetalleHorario extends AppCompatActivity implements   Response.Listener<JSONObject>, Response.ErrorListener{
    RecyclerView recyclerHora;
    RequestQueue rq;
    JsonRequest jrq;
    ArrayList<Hora> listaHora;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_horario);
        recyclerHora = (RecyclerView) findViewById(R.id.idRecyclerHora);
        recyclerHora.setLayoutManager(new LinearLayoutManager(this));
        recyclerHora.setHasFixedSize(true);
        rq = Volley.newRequestQueue(this);
        listaHora  = new ArrayList<>();


        recibirdatos();
    }

    public void recibirdatos(){
        Bundle extras = getIntent().getExtras();
        String d1 = getIntent().getExtras().getString("id");

        cargarwebservice(d1);
    }

    private void cargarwebservice(String d1) {
        String url="http://134.209.237.96/WebService/consultadetallelista.php?id_horario="+d1;
        jrq= new JsonObjectRequest(Request.Method.GET, url,null,this,this);
        rq.add(jrq);
    }


    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this,"No se puede conectar"+error.toString(),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONObject response) {
        Hora hora = null;
        JSONArray json= response.optJSONArray("datos");
        try {
            for(int i=0;i<json.length();i++){
                hora = new Hora();
                JSONObject jsonObject = null;
                jsonObject= json.getJSONObject(i);
                hora.setHora_inicio( jsonObject.optString("hora_inicio"));
                hora.setHora_fin(jsonObject.optString("hora_fin"));
                hora.setCodigo(jsonObject.optString("codigo"));
                hora.setUbicacion(jsonObject.optString("ubicacion"));
                hora.setCurso(jsonObject.optString("nombre_curso"));
                hora.setDocente(jsonObject.optString("nombre_docente"));

                listaHora.add(hora);
            }
            HoraAdapter adapter= new HoraAdapter(listaHora);
            recyclerHora.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this,"No se puede conectar2"+response,Toast.LENGTH_LONG).show();

        }
    }
}