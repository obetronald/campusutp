package com.example.guiabeaconmaster2.Util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guiabeaconmaster2.Entitys.Hora;
import com.example.guiabeaconmaster2.R;

import java.util.List;

public class HoraAdapter extends RecyclerView.Adapter<HoraAdapter.HoraHolder>
        implements View.OnClickListener{
    private View.OnClickListener listener;
    List<Hora> listaHora;


    public HoraAdapter(List<Hora> listaHora){

        this.listaHora = listaHora;
    }

    @Override
    public HoraHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder= LayoutInflater.from(parent.getContext()) .inflate(R.layout.hora_list, null, false);
        viewHolder.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));

        viewHolder.setOnClickListener(this);
        return new HoraHolder(viewHolder);

    }

    @Override
    public void onBindViewHolder( HoraHolder holder, int position) {
        holder.txtCurso.setText(listaHora.get(position).getCurso());
        holder.txtDoce.setText(listaHora.get(position).getDocente());
        holder.txtCodigo.setText(listaHora.get(position).getCodigo());
        holder.txtHorai.setText(listaHora.get(position).getHora_inicio());
        holder.txtHoraf.setText(listaHora.get(position).getHora_fin());


    }

    @Override
    public int getItemCount() {
        return listaHora.size();
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

    public class HoraHolder extends RecyclerView.ViewHolder {

        TextView txtubicacion,txtCurso,txtHorai,txtHoraf,txtCodigo,txtDoce;


        public HoraHolder(View itemView) {
            super(itemView);
            txtCurso=(TextView) itemView.findViewById(R.id.txtCurso);
            txtCodigo=(TextView) itemView.findViewById(R.id.txtCod);

            txtHorai=(TextView) itemView.findViewById(R.id.txtHorai);
            txtHoraf=(TextView) itemView.findViewById(R.id.txtHoraf);

            txtDoce=(TextView) itemView.findViewById(R.id.txtDoc);

        }
    }
}
