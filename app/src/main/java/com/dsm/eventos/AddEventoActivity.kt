package com.dsm.eventos

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar

class AddEventoActivity: AppCompatActivity()  {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addevento)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true) // Mostrar el botón "Atrás"
            setHomeAsUpIndicator(android.R.drawable.ic_menu_revert) // Icono atras
        }
        val fecha:EditText=findViewById(R.id.fecha)
        val hora: EditText = findViewById(R.id.hora)
        val ubicacion: EditText = findViewById(R.id.ubicacion)
        val descripcion: EditText = findViewById(R.id.descripcion)
        val btnRegistrar: Button = findViewById(R.id.btnNewEvento)
        val nombre:EditText=findViewById(R.id.nombre)
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Configurar el listener para abrir el TimePickerDialog
        hora.setOnClickListener {
            // Obtener la hora actual
            val calendario = Calendar.getInstance()
            val horaNow = calendario.get(Calendar.HOUR_OF_DAY)
            val minuto = calendario.get(Calendar.MINUTE)

            // Crear y mostrar el TimePickerDialog
            val timePickerDialog = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    // Formatear la hora seleccionada
                    val horaFormateada = String.format("%02d:%02d", hourOfDay, minute)
                    hora.setText(horaFormateada) // Mostrar la hora seleccionada en el EditText
                },
                horaNow,
                minuto,
                false // Usa formato de 24 horas (cambia a false para formato de 12 horas)
            )
            timePickerDialog.show()
        }
        fecha.setOnClickListener {
            // Obtener la fecha actual
            val calendario = Calendar.getInstance()
            val anio = calendario.get(Calendar.YEAR)
            val mes = calendario.get(Calendar.MONTH)
            val dia = calendario.get(Calendar.DAY_OF_MONTH)

            // Crear y mostrar el DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Formatear la fecha seleccionada
                    val fecha2 = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year)
                    fecha.setText(fecha2) // Mostrar la fecha seleccionada en el EditText
                },
                anio,
                mes,
                dia
            )
            datePickerDialog.show()
        }
        btnRegistrar.setOnClickListener()
        {
            if(nombre.text.isEmpty() || descripcion.text.isEmpty() || fecha.text.isEmpty() || hora.text.isEmpty() || ubicacion.text.isEmpty() )
            {
                Toast.makeText(baseContext, "Error llena todos los campos", Toast.LENGTH_SHORT).show()
            }else {
                db = FirebaseFirestore.getInstance()
                val event = hashMapOf(
                    "nombre" to nombre.text.toString(),
                    "descripcion" to descripcion.text.toString(),
                    "fecha" to fecha.text.toString(),
                    "hora" to hora.text.toString(),
                    "ubicacion" to ubicacion.text.toString(),
                    "organizador" to currentUser?.email
                )
                db.collection("events").add(event)
                    .addOnSuccessListener {
                        Toast.makeText(baseContext, "Exito Evento registrado", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(baseContext, "Ha ocurrido un error", Toast.LENGTH_SHORT).show()
                    }
            }
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}