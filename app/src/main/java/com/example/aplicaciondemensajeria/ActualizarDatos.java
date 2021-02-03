package com.example.aplicaciondemensajeria;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.example.aplicaciondemensajeria.Modelos.Usuarios;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActualizarDatos extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView imagen;
    private EditText nombre,telefono;
    private Usuarios usuario;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference db;
    private StorageReference mStoragae;
    private Button save,actuImagen;
    private final int GALLERY_INTENT=1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_datos);

        firebaseAuth=FirebaseAuth.getInstance();
        mStoragae= FirebaseStorage.getInstance().getReference();
        db= FirebaseDatabase.getInstance().getReference();

        imagen=findViewById(R.id.img_actuImg);
        nombre=findViewById(R.id.et_nombreAct);
        telefono=findViewById(R.id.et_telefonoAct);
        save=findViewById(R.id.bt_save);
        actuImagen=findViewById(R.id.bt_actualizarImagen);
        save.setOnClickListener(this);
        actuImagen.setOnClickListener(this);

        recuperarDatos();




    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_actualizarImagen:
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i,GALLERY_INTENT);
                break;
            case R.id.bt_save:
                usuario.setTelefono(telefono.getText().toString());
                usuario.setNombre(nombre.getText().toString());
                db.child("Usuarios").child(usuario.getUID()).setValue(usuario);
                Intent ivolver = new Intent(this,ventanaPrincipal.class);
                startActivity(ivolver);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_INTENT && resultCode==RESULT_OK){
            Uri uri = data.getData();
            StorageReference filePath = mStoragae.child("fotos").child(uri.getLastPathSegment());
            //Obtengo la URL de la imagen subida a Firebase
            filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri uriUser = task.getResult();
                        usuario.setFoto(uriUser.toString());
                        db.child("Usuarios").child(usuario.getUID()).setValue(usuario);
                    }
                }
            });


        }
    }
    private void recuperarDatos(){
        //Leo todos los usaurios del Nodo Usuarios y los cargo en el recyclerView
        db.child("Usuarios").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    usuario=dataSnapshot.getValue(Usuarios.class);
                    nombre.setText(usuario.getNombre());
                    telefono.setText(usuario.getTelefono());
                    if(!usuario.getFoto().equals("")){
                        Glide.with(ActualizarDatos.this)
                                .load(usuario.getFoto())
                                .into(imagen);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}