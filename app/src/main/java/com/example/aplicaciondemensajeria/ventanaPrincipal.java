package com.example.aplicaciondemensajeria;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.aplicaciondemensajeria.Adaptadores.AdaptadorUsuarios;
import com.example.aplicaciondemensajeria.Modelos.Mensajes;
import com.example.aplicaciondemensajeria.Modelos.Usuarios;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class ventanaPrincipal extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth firebaseAuth;
    private StorageReference mStoragae;
    private ImageView imagenUsu;
    private DatabaseReference bd;
    private Usuarios usuario = new Usuarios();


    TextView email, nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_principal);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController;navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        firebaseAuth = FirebaseAuth.getInstance();
        mStoragae = FirebaseStorage.getInstance().getReference();
        bd = FirebaseDatabase.getInstance().getReference();


        View navHeader = navigationView.getHeaderView(0);
        email = navHeader.findViewById(R.id.emailUsu);
        nombre = navHeader.findViewById(R.id.nombreUsu);
        imagenUsu = navHeader.findViewById(R.id.img_usu);

        recuperarDatos();



    }




    private void recuperarDatos() {

        bd.child("Usuarios").child(firebaseAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usuario = dataSnapshot.getValue(Usuarios.class);
                    email.setText(usuario.getEmail());
                    nombre.setText(usuario.getNombre());
                    if (!usuario.getFoto().equals("")) {
                        Glide.with(ventanaPrincipal.this)
                                .load(usuario.getFoto())
                                .into(imagenUsu);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.ventana_principal, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                switch (usuario.getProvider()) {
                    case "FireBase":
                        firebaseAuth.signOut();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case "Google":
                        firebaseAuth.signOut();
                        GoogleSignIn.getClient(
                                this,
                                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                        ).signOut();
                        Intent igoogle = new Intent(this, MainActivity.class);
                        startActivity(igoogle);
                        break;
                }


                break;
            case R.id.action_update:
                Intent i = new Intent(this, ActualizarDatos.class);
                startActivity(i);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

    }


}