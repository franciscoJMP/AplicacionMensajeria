package com.example.aplicaciondemensajeria.Adaptadores;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aplicaciondemensajeria.Modelos.Usuarios;
import com.example.aplicaciondemensajeria.R;
import com.google.firebase.database.ChildEventListener;

import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdaptadorUsuarios extends RecyclerView.Adapter<AdaptadorUsuarios.AdaptadorViewHolder> {
    private ArrayList<Usuarios> listaUsuarios=new ArrayList<>();
    private Context context;


    public AdaptadorUsuarios(ArrayList<Usuarios> listaUsuarios, Context context) {
        this.listaUsuarios = listaUsuarios;
        this.context = context;

    }


    @NonNull
    @Override
    public AdaptadorUsuarios.AdaptadorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_usuarios,parent,false);
        AdaptadorViewHolder avh=new AdaptadorViewHolder(itemView);
        return avh;
    }

    @Override
    public void onBindViewHolder(@NonNull AdaptadorViewHolder holder, int position) {
        Usuarios u = listaUsuarios.get(position);
        holder.nombre.setText(u.getNombre());

        Glide.with(context).load(u.getFoto()).into(holder.imagen);

        //Añado un click listener al recyclerview para poder navegar a la venta de chat,
        // pasando a ese fragment el usuario con el que vamos a conversar.

        holder.rows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putSerializable("user",u);
                Navigation.findNavController(v).navigate(R.id.action_nav_home_to_chatFragment,b);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }
    public class AdaptadorViewHolder extends RecyclerView.ViewHolder{
        private TextView nombre;
        private CircleImageView imagen;
        private ConstraintLayout rows;

        public AdaptadorViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre=itemView.findViewById(R.id.tx_nombre);
            imagen=itemView.findViewById(R.id.img_cliente);
            rows=itemView.findViewById(R.id.rows);
        }
    }
}
