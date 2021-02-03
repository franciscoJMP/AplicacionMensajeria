package com.example.aplicaciondemensajeria.Adaptadores;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicaciondemensajeria.Modelos.Mensajes;
import com.example.aplicaciondemensajeria.Modelos.Usuarios;
import com.example.aplicaciondemensajeria.R;

import java.util.ArrayList;
import java.util.Date;

public class AdaptadorMensaje extends RecyclerView.Adapter<AdaptadorMensaje.AdaptadorViewHolder> {
    ArrayList<Mensajes> listaMensajes = new ArrayList<>();
    private Usuarios receptor;
    private String emisor;
    private Context context;


    public AdaptadorMensaje(Context context, Usuarios receptor, String emisor) {
        this.context = context;
        this.receptor = receptor;
        this.emisor = emisor;
    }

    public void addMensaje(Mensajes m) {
        listaMensajes.add(m);
        notifyItemInserted(listaMensajes.size());
    }

    @NonNull
    @Override
    public AdaptadorMensaje.AdaptadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        //Este if es para cargar una vista dependiendo si el mensaje es recibido o enviado.
        if(viewType==1){
            itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receptor, parent, false);
        }else{
            itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emisor, parent, false);
        }

        AdaptadorViewHolder avh = new AdaptadorViewHolder(itemView);
        return avh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorViewHolder holder, int position) {
        Mensajes m = listaMensajes.get(position);
        holder.tx_mensaje.setText(m.getMensaje());
        Date fecha = new Date(m.getMensajeTime());
        String hora,minuto,horaTotal;
        //Obtengo la hora y los minutos del mensaje
        hora=String.valueOf(fecha.getHours());
        minuto=String.valueOf(fecha.getMinutes());
        //Si los minutos van del 0 al 9 les añado un 0 a la izquierda
        switch (minuto){
            case "0":
            case "1":
            case "2":
            case "3":
            case "4":
            case "5":
            case "6":
            case "7":
            case "8":
            case "9":
                minuto="0"+minuto;
                break;

        }
        horaTotal=hora+":"+minuto;
        holder.horas.setText(horaTotal);

    }



    @Override
    public int getItemCount() {
        return listaMensajes.size();
    }

    @Override
    public int getItemViewType(int position) {
        //Este metodo me permite devolver un numero en funcion de la vista que quiero cargar en el onCreatedViewHolder
        if(listaMensajes.get(position).getReceptor().equals(receptor.getUID())){
            return -1;
        }else{
            return 1;
        }
    }

    public class AdaptadorViewHolder extends RecyclerView.ViewHolder {
        private TextView tx_mensaje,horas;


        public AdaptadorViewHolder(@NonNull View itemView) {
            super(itemView);
            tx_mensaje = itemView.findViewById(R.id.tv_mensajes);
            horas = itemView.findViewById(R.id.horaMensaje);

        }
    }
}
