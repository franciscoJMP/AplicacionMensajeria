package com.example.aplicaciondemensajeria;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.aplicaciondemensajeria.Modelos.Usuarios;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseaut;
    private DatabaseReference baseDatos;
    private Button bt_registrar, bt_acceder, bt_accederGoogle;
    private EditText et_email, et_pass;
    private GoogleSignInClient gCliente;
    private int RC_SIGIN=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceso);

        firebaseaut = FirebaseAuth.getInstance();
        baseDatos = FirebaseDatabase.getInstance().getReference();

        bt_registrar = findViewById(R.id.bt_registrar);
        bt_acceder = findViewById(R.id.bt_acceso);
        bt_accederGoogle = findViewById(R.id.bt_accesoGoogle);
        bt_registrar.setOnClickListener(this);
        bt_acceder.setOnClickListener(this);
        bt_accederGoogle.setOnClickListener(this);

        et_email = findViewById(R.id.et_email);
        et_pass = findViewById(R.id.et_pass);
        //Aqui si el usuario ya ha se ha logueado por primera vez en la aplicacion lo redirecciono a la ventana de contactos
        
        try {
            if (firebaseaut.getCurrentUser().getEmail() != null) {
                Intent intent = new Intent(MainActivity.this, ventanaPrincipal.class);
                startActivity(intent);
            }
        } catch (Exception e) {

        }
        requestGoogle();


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_registrar:
                Intent intent = new Intent(this, Registro.class);
                startActivity(intent);
                break;
            case R.id.bt_acceso:
                if (!TextUtils.isEmpty(et_email.getText().toString()) && !TextUtils.isEmpty(et_email.getText().toString())) {
                    firebaseaut.signInWithEmailAndPassword(et_email.getText().toString(), et_pass.getText().toString())
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Intent intent = new Intent(MainActivity.this, ventanaPrincipal.class);
                                        startActivity(intent);

                                    } else {
                                        showFailMensaje("Error, este usuario no esta registrado");
                                    }
                                }
                            });
                } else {
                    showFailMensaje("Error, rellene los campos");
                }


                break;
            case R.id.bt_accesoGoogle:
                    logGoogle();
                break;

        }


    }

    private void showFailMensaje(String s) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(s).setTitle("Error");
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }
    private void requestGoogle(){
        GoogleSignInOptions gOptions = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        gCliente= GoogleSignIn.getClient(MainActivity.this,gOptions);

    }
    private void logGoogle(){
        Intent i = gCliente.getSignInIntent();
        startActivityForResult(i,RC_SIGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGIN){
            Task tarea = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount gSingAcount = (GoogleSignInAccount) tarea.getResult(ApiException.class);
                autorizarGoogleFirebase(gSingAcount);
            }catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

    }

    private void autorizarGoogleFirebase(GoogleSignInAccount gSingAcount) {
        AuthCredential ath= GoogleAuthProvider.getCredential(gSingAcount.getIdToken(),null);
        firebaseaut.signInWithCredential(ath).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    baseDatos.child("Usuario").child(firebaseaut.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                Intent intent = new Intent(MainActivity.this, ventanaPrincipal.class);
                                startActivity(intent);
                            }else{
                                guardarUsuario();
                                Intent intent = new Intent(MainActivity.this, ventanaPrincipal.class);
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }
        });
    }
    private void guardarUsuario() {
        Usuarios u = new Usuarios();
        u.setEmail(firebaseaut.getCurrentUser().getEmail());
        u.setNombre(firebaseaut.getCurrentUser().getDisplayName());
        u.setTelefono("");
        u.setUID(firebaseaut.getCurrentUser().getUid());
        u.setProvider("Google");
        u.setFoto(firebaseaut.getCurrentUser().getPhotoUrl().toString());
        baseDatos.child("Usuarios").child(firebaseaut.getCurrentUser().getUid()).setValue(u);


    }

    @Override
    public void onBackPressed() {
        
    }
}
