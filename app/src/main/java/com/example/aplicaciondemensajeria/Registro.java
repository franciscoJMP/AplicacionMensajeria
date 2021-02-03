package com.example.aplicaciondemensajeria;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.aplicaciondemensajeria.Modelos.Usuarios;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



public class Registro extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference bd;
    private EditText correo,pass,name,telef;
    private Button regis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);


        firebaseAuth=FirebaseAuth.getInstance();
        bd= FirebaseDatabase.getInstance().getReference();

        correo=findViewById(R.id.et_emailR);
        pass=findViewById(R.id.et_passR);
        name=findViewById(R.id.et_nombre);
        telef=findViewById(R.id.et_telefono);

        regis=findViewById(R.id.bt_registrarR);


        regis.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_registrarR:
                if (comprobarCampos()) {
                    firebaseAuth.createUserWithEmailAndPassword(
                            correo.getText().toString(),
                            pass.getText().toString()).addOnCompleteListener(this,
                            new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        guardarUsuario();
                                        showOkMensaje("Usuario " + firebaseAuth.getCurrentUser().getEmail() + " registrado");
                                    } else {
                                        showFailMensaje("Este usuario ya existe");
                                    }


                                }
                            });


                }else{
                    showFailMensaje("Rellene los campos");
                }

                break;
        }
    }

    private boolean comprobarCampos() {
        if(!TextUtils.isEmpty(correo.getText().toString())
                && !TextUtils.isEmpty(pass.getText().toString())
                && !TextUtils.isEmpty(name.getText().toString())
                && !TextUtils.isEmpty(telef.getText().toString())){
            return true;

        }else{
            return false;
        }
    }

    private void guardarUsuario() {
        Usuarios u = new Usuarios();
        u.setEmail(correo.getText().toString());
        u.setNombre(name.getText().toString());
        u.setTelefono(telef.getText().toString());
        u.setUID(firebaseAuth.getCurrentUser().getUid());
        u.setProvider("FireBase");
        u.setFoto("https://firebasestorage.googleapis.com/v0/b/aplicaciondemensajeria-9f6e9.appspot.com/o/fotos%2Fdefaultuser.jpg?alt=media&token=c3ee4068-7b65-4c59-9d06-a608d480ca62");
        bd.child("Usuarios").child(firebaseAuth.getCurrentUser().getUid()).setValue(u);


    }
    private void showOkMensaje(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(s).setTitle("Registro");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent=new Intent(Registro.this,ventanaPrincipal.class);
                startActivity(intent);

            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();
    }

    private void showFailMensaje(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(s).setTitle("Error");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


            }
        });
        AlertDialog dialog=builder.create();
        dialog.show();

    }
}













