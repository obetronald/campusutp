package com.example.guiabeaconmaster2.Util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.example.guiabeaconmaster2.Entitys.Horario;
import com.example.guiabeaconmaster2.R;

import java.util.List;

public class HorarioAdapter extends RecyclerView.Adapter<HorarioAdapter.HorarioHolder>
        implements View.OnClickListener{
    private View.OnClickListener listener;
    List<Horario> listaHorarios;


    public HorarioAdapter(List<Horario> listaHorarios){

        this.listaHorarios = listaHorarios;
    }

    @Override
    public HorarioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder= LayoutInflater.from(parent.getContext()) .inflate(R.layout.horario_list, null, false);
        viewHolder.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        viewHolder.setOnClickListener(this);
        return new HorarioHolder(viewHolder);

    }

    @Override
    public void onBindViewHolder( HorarioHolder holder, int position) {
        //holder.txtcodigo.setText(listaHorarios.get(position).getCod_beacon());
        holder.txtnombre.setText(listaHorarios.get(position).getNombre_beacon());

    }

    @Override
    public int getItemCount() {
        return listaHorarios.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;

    }
    @Override
    public void onClick(View v) {
        if(listener!=null){
            listener.onClick(v);
        }
    }

    public class HorarioHolder extends RecyclerView.ViewHolder {

        TextView txtcodigo,txtnombre;

        public HorarioHolder(View itemView) {
            super(itemView);
            //txtcodigo=(TextView) itemView.findViewById(R.id.txtCod);
            txtnombre=(TextView)itemView.findViewById(R.id.txtNomb);
        }
    }
}