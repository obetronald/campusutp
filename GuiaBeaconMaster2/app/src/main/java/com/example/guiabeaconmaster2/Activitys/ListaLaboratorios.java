package com.example.guiabeaconmaster2.Activitys;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.zip.Inflater;

public class ListaLaboratorios extends AppCompatActivity implements   Response.Listener<JSONObject>, Response.ErrorListener{

    Dialog myDialog;
    RecyclerView recyclerHorarios;
    ArrayList<Hora> listaHora;
    ArrayList<Horario> listaHorarios;
    RequestQueue rq;
    JsonRequest jrq;

    ImageView imageView;
    Matrix matrix = new Matrix();
    Float scale=1f;
    ScaleGestureDetector SGD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_laboratorios);

        listaHorarios = new ArrayList<>();
        recyclerHorarios = (RecyclerView) findViewById(R.id.idRecycler);
        recyclerHorarios.setLayoutManager(new LinearLayoutManager(this));
        recyclerHorarios.setHasFixedSize(true);

        rq = Volley.newRequestQueue(this);
        myDialog = new Dialog(this);

        imageView=(ImageView)findViewById(R.id.imageView3);
        SGD=new ScaleGestureDetector(this,new ScaleListener());


        cargarWebService();

    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener{

        @Override
        public boolean onScale(ScaleGestureDetector detector) {

            scale=scale*detector.getScaleFactor();
            scale=Math.max(0.1f,Math.min(scale,5f));
            matrix.setScale(scale,scale);
            imageView.setImageMatrix(matrix);
            return true;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        SGD.onTouchEvent(event);
        return true;
    }

    public void ShowPopup(View v) {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.custompopup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");
        //btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();

    }

    private void cargarWebService(){

        String url="http://134.209.237.96/WebService/consultalista.php";
        jrq= new JsonObjectRequest(Request.Method.GET, url,null,this,this);
        rq.add(jrq);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        Toast.makeText(this,"No se puede conectar"+error.toString(),Toast.LENGTH_LONG).show();

    }

    @Override
    public void onResponse(JSONObject response) {
        Horario horario = null;
        Hora hora = null;
        JSONArray json= response.optJSONArray("datos");
        try {
            for(int i=0;i<json.length();i++){
                horario = new Horario();
                hora = new Hora();
                JSONObject jsonObject = null;
                jsonObject= json.getJSONObject(i);

                horario.setId_horario(jsonObject.optInt("id_horario"));
                horario.setCod_beacon(jsonObject.optString("cod_beacon"));
                horario.setNombre_beacon(jsonObject.optString("nombre_beacon"));
                horario.setNombre_curso(jsonObject.optString("nombre_curso"));
                horario.setNombre_docente(jsonObject.optString("nombre_docente"));



                listaHorarios.add(horario);
            }
            HorarioAdapter adapter= new HorarioAdapter(listaHorarios);
            adapter.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"Selección: "+listaHorarios.get(recyclerHorarios.getChildAdapterPosition(v)).getNombre_beacon(),Toast.LENGTH_SHORT).show();

                    String id = String.valueOf(listaHorarios.get(recyclerHorarios.getChildAdapterPosition(v)).getId_horario());

                    Intent i = new Intent(ListaLaboratorios.this, DetalleHorario.class);
                    i.putExtra("id",id);
                    ListaLaboratorios.this.startActivity(i);


                }
            });
            recyclerHorarios.setAdapter(adapter);


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this,"No se puede conectar2"+response,Toast.LENGTH_LONG).show();

        }
    }
}