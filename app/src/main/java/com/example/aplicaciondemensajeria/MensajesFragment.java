package com.example.aplicaciondemensajeria;


import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.aplicaciondemensajeria.Adaptadores.AdaptadorUsuarios;
import com.example.aplicaciondemensajeria.Modelos.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class MensajesFragment extends Fragment {
    private RecyclerView rv;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter;
    private ArrayList<Usuarios> listaUsuarios = new ArrayList<>();
    private DatabaseReference bd, mensajes;
    private FirebaseAuth firebaseAuth;
    private ChildEventListener evento;
    private Usuarios usuariosNotificacion=null;



    public MensajesFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();
        bd = FirebaseDatabase.getInstance().getReference();
        mensajes = FirebaseDatabase.getInstance().getReference("Mensajes");
        //Este evento me sirve para las notificaciones.

        evento = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                bd.child("Usuarios").child(snapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        usuariosNotificacion = snapshot.getValue(Usuarios.class);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if(usuariosNotificacion!=null){
                    Toast.makeText(getContext(),"Mensaje nuevo de "+usuariosNotificacion.getNombre(),Toast.LENGTH_LONG).show();


                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mensajes");
        rv = root.findViewById(R.id.rv);
        layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        cargarUsuarios();
        notificaciones();
    }


    private void cargarUsuarios() {
        bd.child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listaUsuarios.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        Usuarios u = ds.getValue(Usuarios.class);
                        if (!firebaseAuth.getCurrentUser().getUid().equals(u.getUID())) {
                            listaUsuarios.add(u);
                        }

                    }
                    adapter = new AdaptadorUsuarios(listaUsuarios, getActivity());
                    rv.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void notificaciones() {
        mensajes.child(firebaseAuth.getCurrentUser().getUid()).addChildEventListener(evento);


    }




}