package com.example.luchadoreswwe_definitivo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText txtid, txtnom;
    private Button btnbus, btnmod, btnreg, btneli;
    private ListView lvDatos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //---------------enlace componentes--------------

        txtid   = (EditText) findViewById(R.id.txtid);
        txtnom  = (EditText) findViewById(R.id.txtnom);
        btnbus  = (Button)   findViewById(R.id.btnbus);
        btnmod  = (Button)   findViewById(R.id.btnmod);
        btnreg  = (Button)   findViewById(R.id.btnreg);
        btneli  = (Button)   findViewById(R.id.btneli);
        lvDatos = (ListView) findViewById(R.id.lvDatos);


        //--------------eventos botones--------------------------
        botonBuscar();
        botonModificar();
        botonRegistrar();
        botonEliminar();
        listarLuchadores();

    } // Cierra el onCreate.




    private void botonBuscar(){
        btnbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //-----------comprobacion vacios-------------------------------
                if(txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Digite El ID del Luchador a Buscar!!", Toast.LENGTH_SHORT).show();
                }


                //----------------buscar-------------------------------------
                else{

                    int id = Integer.parseInt(txtid.getText().toString());

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Luchador.class.getSimpleName());
                    //DatabaseReference dbref = db.getReference().child("Luchador");

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            //---------------------encuentra----------------------------
                            String aux = Integer.toString(id);
                            boolean res = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(aux.equalsIgnoreCase(x.child("id").getValue().toString())){
                                    res = true;
                                    ocultarTeclado();
                                    txtnom.setText(x.child("nombre").getValue().toString());
                                    break;
                                }
                            }


                            //-----------------no encuentra----------------------
                            if(res == false){
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "ID ("+aux+") No Encontrado!!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    } // Cierra el método botonBuscar.




    private void botonModificar(){
        btnmod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //------------------------comprobacion vacios------------------------------
                if(txtid.getText().toString().trim().isEmpty()
                        || txtnom.getText().toString().trim().isEmpty()){

                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Complete Los Campos Faltantes Para Actualizar!!", Toast.LENGTH_SHORT).show();

                }


                //--------------modificar----------------------
                else{

                    int id = Integer.parseInt(txtid.getText().toString());
                    String nom = txtnom.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Luchador.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            //-------------si encuentra similar nombre----------------------
                            boolean res2 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("nombre").getValue().toString().equalsIgnoreCase(nom)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "El Nombre ("+nom+") Ya Existe.\nImposible Modificar!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }


                            //------------si no lo modifica---------------------
                            if(res2 == false){
                                String aux = Integer.toString(id);
                                boolean res = false;
                                for(DataSnapshot x : snapshot.getChildren()){
                                    if(x.child("id").getValue().toString().equalsIgnoreCase(aux)){
                                        res = true;
                                        ocultarTeclado();
                                        x.getRef().child("nombre").setValue(nom);
                                        txtid.setText("");
                                        txtnom.setText("");
                                        listarLuchadores();
                                        break;
                                    }
                                }


                                //-------------------si no encuentra el id-------------------
                                if(res == false){
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "ID ("+aux+") No Encontrado.\nImposible Modificar!!!!", Toast.LENGTH_SHORT).show();
                                    txtid.setText("");
                                    txtnom.setText("");
                                }
                            }




                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    } // Cierra el método botonModificar.




    private void botonRegistrar(){
        btnreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                //------------------------comprobacion vacio-----------------------
                if(txtid.getText().toString().trim().isEmpty()
                        || txtnom.getText().toString().trim().isEmpty()){

                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Complete Los Campos Faltantes!!", Toast.LENGTH_SHORT).show();

                }

                //-------------registrar----------------------
                else{

                    int id = Integer.parseInt(txtid.getText().toString());
                    String nom = txtnom.getText().toString();

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Luchador.class.getSimpleName());

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            //----------------------encuentra similar por id-------------------
                            String aux = Integer.toString(id);
                            boolean res = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("id").getValue().toString().equalsIgnoreCase(aux)){
                                    res = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "Error. El ID ("+aux+") Ya Existe!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }


                            //----------------------encuentra similar por nombre-------------------
                            boolean res2 = false;
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(x.child("nombre").getValue().toString().equalsIgnoreCase(nom)){
                                    res2 = true;
                                    ocultarTeclado();
                                    Toast.makeText(MainActivity.this, "Error. El Nombre ("+nom+") Ya Existe!!", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }


                            //----------------inserta si no hay similar-------------------
                            if(res == false && res2 == false){
                                Luchador luc = new Luchador(id, nom);
                                dbref.push().setValue(luc);
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "Luchador Registrado Correctamente!!", Toast.LENGTH_SHORT).show();
                                txtid.setText("");
                                txtnom.setText("");
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.


            }
        });
    } // Cierra el método botonRegistrar.




    private void listarLuchadores(){
        //---------------creas adaptador-----------------------
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference dbref = db.getReference(Luchador.class.getSimpleName());

        ArrayList <Luchador> lisluc = new ArrayList <Luchador> ();
        ArrayAdapter <Luchador> ada = new ArrayAdapter <Luchador> (MainActivity.this, android.R.layout.simple_list_item_1, lisluc);
        lvDatos.setAdapter(ada);

        dbref.addChildEventListener(new ChildEventListener() {

            //---------------------cuando se anada alguno-------------------------
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                Luchador luc = snapshot.getValue(Luchador.class);
                lisluc.add(luc);
                ada.notifyDataSetChanged();

            }


            //------------------------cuando se cambie alguno---------------------
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                ada.notifyDataSetChanged();

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

        //----------------cuando se clique sobre alguno-------------
        lvDatos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                //------------mostrar ventana con sus datos-------------------
                Luchador luc = lisluc.get(i);
                AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                a.setCancelable(true);
                a.setTitle("Luchador Seleccionado");

                String msg = "ID : " + luc.getId() +"\n\n";
                msg += "NOMBRE : " + luc.getNombre();

                a.setMessage(msg);
                a.show();

            }
        });

    } // Cierra el método listarLuchadores.





    private void botonEliminar(){
        btneli.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //----------------comprobacion vacios---------------------
                if(txtid.getText().toString().trim().isEmpty()){
                    ocultarTeclado();
                    Toast.makeText(MainActivity.this, "Digite El ID del Luchador a Eliminar!!", Toast.LENGTH_SHORT).show();
                }

                //-----------eliminar----------------------------
                else{

                    int id = Integer.parseInt(txtid.getText().toString());

                    FirebaseDatabase db = FirebaseDatabase.getInstance();
                    DatabaseReference dbref = db.getReference(Luchador.class.getSimpleName());
                    //DatabaseReference dbref = db.getReference().child("Luchador");

                    dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {


                            //------------busca similar y elimina o no segun dialog------------------------
                            String aux = Integer.toString(id);
                            final boolean[] res = {false};
                            for(DataSnapshot x : snapshot.getChildren()){
                                if(aux.equalsIgnoreCase(x.child("id").getValue().toString())){


                                    //----------------mostrar dialog-----------------------
                                    AlertDialog.Builder a = new AlertDialog.Builder(MainActivity.this);
                                    a.setCancelable(false);
                                    a.setTitle("Pregunta");
                                    a.setMessage("¿Está Seguro(a) De Querer Eliminar El Registro?");


                                    //-------------------cancelar--------------------------
                                    a.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });


                                    //---------------------eliminar--------------------------
                                    a.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            res[0] = true;
                                            ocultarTeclado();
                                            x.getRef().removeValue();
                                            listarLuchadores();
                                        }
                                    });

                                    a.show();

                                    break;

                                }
                            }


                            //--------------operacion no realizada------------------
                            if(res[0] == false){
                                ocultarTeclado();
                                Toast.makeText(MainActivity.this, "ID ("+aux+") No Encontrado.\nImposible Eliminar!!", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                } // Cierra el if/else inicial.

            }
        });
    } // Cierra el método botonEliminar.





    private void ocultarTeclado(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    } // Cierra el método ocultarTeclado.


} // Cierra la clase.