package com.dsm.eventos

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dsm.eventos.adapters.ComentarioAdapter
import com.dsm.eventos.model.Comentario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date

class ComentarioActivity : AppCompatActivity() {

    private lateinit var etComentario: EditText
    private lateinit var txtEvento: TextView
    private lateinit var btnEnviarComentario: Button
    private lateinit var rvComentarios: RecyclerView
    private lateinit var comentarioAdapter: ComentarioAdapter

    private lateinit var db: FirebaseFirestore
    private var currentUserUid: String? = null
    private val comentarioList = mutableListOf<Comentario>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comentario)
        val eventId = intent.getStringExtra("eventId") ?: return
        db = FirebaseFirestore.getInstance()
        currentUserUid = FirebaseAuth.getInstance().currentUser?.email

        // Obtener referencias
        etComentario = findViewById(R.id.etComentario)
        btnEnviarComentario = findViewById(R.id.btnEnviarComentario)
        rvComentarios = findViewById(R.id.rvComentarios)
        rvComentarios = findViewById(R.id.rvComentarios)
        txtEvento=findViewById(R.id.txtEvento)
        txtEvento.text="Evento: "+intent.getStringExtra("event") ?: return
        // Configurar RecyclerView
        comentarioAdapter = ComentarioAdapter(comentarioList) { updatedComment->
            val nuevosDatos = mapOf(
                "calificacion" to updatedComment.calificacion
            )
            db.collection("comentarios").document(updatedComment.id).update(nuevosDatos).addOnSuccessListener {
                Toast.makeText(this,"rating actualizado "+updatedComment.id,Toast.LENGTH_SHORT).show()
            }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            cargarComentarios(eventId)

        }
        rvComentarios.layoutManager = LinearLayoutManager(this)
        rvComentarios.adapter = comentarioAdapter

        // Cargar comentarios
        cargarComentarios(eventId)

        // Configurar botón enviar comentario
        btnEnviarComentario.setOnClickListener {
            val comentario = etComentario.text.toString().trim()
            if (comentario.isEmpty()) {
                Toast.makeText(this, "Por favor escribe un comentario", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentUserUid?.let { userId ->
                guardarComentario(eventId, userId, comentario, 0f)
                etComentario.setText("")
            } ?: Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show()

        }
    }

    private fun guardarComentario(eventId: String, userId: String, comentario: String, calificacion: Float) {
        val comentarioData = hashMapOf(
            "eventoId" to eventId,
            "comentario" to comentario,
            "usuario" to userId,
            "calificacion" to calificacion,
            "timestamp" to Date()
        )
        db.collection("comentarios").add(comentarioData)
        cargarComentarios(eventId)
    }
    private fun cargarComentarios(eventId: String) {
        db.collection("comentarios").whereEqualTo("eventoId",eventId).orderBy("timestamp",Query.Direction.DESCENDING)
            .addSnapshotListener { result, e ->
            if (e != null) {
                Log.w("Firebase", "Error al escuchar cambios.", e)
                return@addSnapshotListener
            }
                if (result != null) {
                Toast.makeText(this, "Se ha actualizado un comentario", Toast.LENGTH_SHORT).show()
                    comentarioList.clear()
                for (document in result) {
                    val comentarioClass = document.toObject(Comentario::class.java)
                    if (comentarioClass != null) {
                        comentarioClass.id = document.id // Asigna el ID del documento
                        comentarioList.add(comentarioClass)
                    }
                }
                    comentarioAdapter.notifyDataSetChanged()
                }
            }
    }
}