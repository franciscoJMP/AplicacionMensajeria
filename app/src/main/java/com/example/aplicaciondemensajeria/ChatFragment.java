package com.example.aplicaciondemensajeria;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.aplicaciondemensajeria.Adaptadores.AdaptadorMensaje;
import com.example.aplicaciondemensajeria.Modelos.Mensajes;
import com.example.aplicaciondemensajeria.Modelos.Usuarios;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatFragment extends Fragment implements View.OnClickListener {
    private CircleImageView fotoPerfil;
    private TextView nombreUsuario;
    private RecyclerView rvMensaje;
    private EditText txMensaje;
    private ImageButton enviar;
    private AdaptadorMensaje adaptador;
    private Usuarios u;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference bdEmisor,bdReceptor;
    private CircleImageView img_chat;





    public ChatFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            u = (Usuarios) getArguments().getSerializable("user");

        }
        firebaseAuth = FirebaseAuth.getInstance();




        //Cargo el nodo del emisor
        bdEmisor = FirebaseDatabase.getInstance().getReference("Mensajes")
                .child(firebaseAuth.getCurrentUser().getUid()).child(u.getUID());
        //Cargo el nodo del receptor
        bdReceptor=FirebaseDatabase.getInstance().getReference("Mensajes")
                .child(u.getUID())
                .child(firebaseAuth.getCurrentUser().getUid());



        //Añado un child event listener al nodo emisor para obtener los mensajes que haya entre el emisor y el receptor
        // y posteriormente los cargo en el recycler view

        bdEmisor.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Mensajes m = snapshot.getValue(Mensajes.class);
                adaptador.addMensaje(m);


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
        });






    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        fotoPerfil = v.findViewById(R.id.img_chat);
        nombreUsuario = v.findViewById(R.id.nombreChat);
        rvMensaje = v.findViewById(R.id.rvMensajes);
        txMensaje = v.findViewById(R.id.et_mensajeChat);
        enviar = v.findViewById(R.id.bt_mandarMensaje);
        img_chat = v.findViewById(R.id.img_chat);
        enviar.setOnClickListener(this);

        LinearLayoutManager l = new LinearLayoutManager(getActivity());
        rvMensaje.setLayoutManager(l);

        adaptador = new AdaptadorMensaje(getActivity(), u, firebaseAuth.getCurrentUser().getUid());
        rvMensaje.setAdapter(adaptador);


        Glide.with(getContext()).load(u.getFoto()).into(img_chat);
        nombreUsuario.setText(u.getNombre());
        //Esta funcion me sirve para que los mensajes vayan bajando automaticamente
        adaptador.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setScrollbar();
            }
        });




        return v;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_mandarMensaje:
                if(!TextUtils.isEmpty(txMensaje.getText().toString())){
                    bdEmisor.push().setValue(new Mensajes(firebaseAuth.getCurrentUser().getUid(), u.getUID(), txMensaje.getText().toString()));
                    bdReceptor.push().setValue(new Mensajes(firebaseAuth.getCurrentUser().getUid(), u.getUID(), txMensaje.getText().toString()));
                    txMensaje.setText("");
                }

                break;

        }


    }

    private void setScrollbar() {
        rvMensaje.scrollToPosition(adaptador.getItemCount() - 1);
    }
}