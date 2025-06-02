    package com.dsm.eventos

    import android.app.AlertDialog
    import android.content.Intent
    import android.os.Bundle
    import android.widget.Button
    import android.widget.EditText
    import android.widget.Toast
    import androidx.appcompat.app.AppCompatActivity
    import androidx.recyclerview.widget.LinearLayoutManager
    import androidx.recyclerview.widget.RecyclerView
    import com.dsm.eventos.adapters.EventAdapter
    import com.dsm.eventos.model.Event
    import com.google.firebase.auth.FirebaseAuth
    import com.google.firebase.firestore.FieldValue
    import com.google.firebase.firestore.FirebaseFirestore

    class EventosActivity : AppCompatActivity() {
                private lateinit var recyclerView: RecyclerView
                private lateinit var eventAdapter: EventAdapter
                private val eventList = mutableListOf<Event>()
                private val db = FirebaseFirestore.getInstance()
                val currentUser = FirebaseAuth.getInstance().currentUser

                override fun onCreate(savedInstanceState: Bundle?) {
                    super.onCreate(savedInstanceState)
                    setContentView(R.layout.activity_eventos)
                    val btnAddEvent:Button=findViewById(R.id.button)
                    val btnEventos:Button=findViewById(R.id.eventos)
                    val btnHistorial:Button=findViewById(R.id.historial)
                    recyclerView = findViewById(R.id.recyclerViewEvents)

                    btnAddEvent.setOnClickListener()
                    {
                      val i=Intent(baseContext,AddEventoActivity::class.java)
                        startActivity(i);
                    }
                    btnEventos.setOnClickListener()
                    {
                        fetchEvents(0)
                    }
                    btnHistorial.setOnClickListener()
                    {
                        fetchEvents(1)
                    }

                    recyclerView.layoutManager = LinearLayoutManager(this)
                    eventAdapter = EventAdapter(eventList, { event, position ->
                        // Llamada al método para editar el evento (por ejemplo, abrir un diálogo)
                        mostrarDialogoEditar(event, position)
                    }, { event ->
                        // Llamada al método para registrar la participación en Firestore
                        participarEnEvento(event)
                    }, { event ->
                        // Llamada al método para registrar la participación en Firestore
                        compartirEvento(event)
                    }, { event ->
                        compartirPorCorreo(event)
                    }, { event ->
                        comentarios(event)
                    })
                    recyclerView.adapter = eventAdapter
                    fetchEvents(0)
                }
        private fun comentarios(event: Event)
        {
            val intent = Intent(this, ComentarioActivity::class.java)
            intent.putExtra("eventId", event.id)
            intent.putExtra("event", event.nombre+", "+event.descripcion+", "+event.ubicacion)// Pasar el ID del evento
            startActivity(intent)
        }
        private fun compartirEvento(event: Event) {
            val eventName = event.nombre
            val eventDescription = event.descripcion
            val eventUbicacion = event.ubicacion

            // Crear el texto que se va a compartir
            val shareText = "$eventName\n$eventDescription\n$eventUbicacion"

            // Crear un intent de compartir
            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText) // El texto a compartir
                type = "text/plain"
            }

            // Iniciar el selector de aplicaciones para compartir
            startActivity(Intent.createChooser(shareIntent, "Compartir evento"))
        }
        private fun compartirPorCorreo(event: Event) {
            // Crear el cuerpo del mensaje del correo
            val emailSubject = "Invitación al evento: ${event.nombre}"
            val emailBody = "Te invitamos a participar en nuestro evento: ${event.nombre}\n\n${event.descripcion}\n\n ha llevarse a cabo en ${event.ubicacion}"

            // Crear un intent de correo
            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                type = "message/rfc822" // Tipo de mensaje
                putExtra(Intent.EXTRA_EMAIL, arrayOf("")) // Dejar vacío para que el usuario seleccione
                putExtra(Intent.EXTRA_SUBJECT, emailSubject) // Asunto del correo
                putExtra(Intent.EXTRA_TEXT, emailBody) // Cuerpo del correo
            }

            // Iniciar la actividad de correo
            startActivity(Intent.createChooser(emailIntent, "Enviar correo"))
        }
        private fun participarEnEvento(event: Event) {
            val userId = currentUser?.email // ID del usuario en sesion
            db.collection("events").document(event.id).update("participantes", FieldValue.arrayUnion(userId))
                .addOnSuccessListener {
                    Toast.makeText(this, "¡Participación registrada!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { error ->
                    Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            fetchEvents(0)
        }
                private fun fetchEvents(accion: Int) {
                    val db = FirebaseFirestore.getInstance()
                    if(accion==1)
                    {
                        db.collection("events").whereArrayContains("participantes",currentUser?.email.toString())
                            .addSnapshotListener { result,e ->
                                eventList.clear()
                                if (result != null) {
                                    for (document in result) {
                                        val event = document.toObject(Event::class.java)
                                        event.id = document.id // Asigna el ID del documento
                                        eventList.add(event)
                                    }
                                }
                                // Notificar al adaptador que los datos han cambiado
                                eventAdapter.notifyDataSetChanged()
                                Toast.makeText(this, "Mostrando historial", Toast.LENGTH_SHORT).show()
                            }
                    }
                    else
                    {
                        db.collection("events")
                            .addSnapshotListener { result,e ->
                                eventList.clear()
                                if (result != null) {
                                    for (document in result) {
                                        val event = document.toObject(Event::class.java)
                                        event.id = document.id // Asigna el ID del documento
                                        eventList.add(event)
                                    }
                                }
                                // Notificar al adaptador que los datos han cambiado
                                eventAdapter.notifyDataSetChanged()
                            }
                    }

                }
    private fun mostrarDialogoEditar(event: Event, position: Int) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_event, null)
        val etNombre = dialogView.findViewById<EditText>(R.id.etNombre)
        val etDescripcion = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val etFecha = dialogView.findViewById<EditText>(R.id.etFecha)
        val etHora = dialogView.findViewById<EditText>(R.id.etHora)
        val etUbicacion = dialogView.findViewById<EditText>(R.id.etUbicacion)

        // Prellenar los campos con el evento
        etNombre.setText(event.nombre)
        etDescripcion.setText(event.descripcion)
        etFecha.setText(event.fecha)
        etHora.setText(event.hora)
        etUbicacion.setText(event.ubicacion)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Evento")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevosDatos = mapOf(
                    "nombre" to etNombre.text.toString(),
                    "descripcion" to etDescripcion.text.toString(),
                    "fecha" to etFecha.text.toString(),
                    "hora" to etHora.text.toString(),
                    "ubicacion" to etUbicacion.text.toString()
                )

                // Actualizar en Firestore
                db.collection("events").document(event.id).update(nuevosDatos)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Evento actualizado correctamente", Toast.LENGTH_SHORT).show()
                        // Actualizar en la lista local y notificar al adaptador
                        eventList[position] = event.copy(
                            nombre = nuevosDatos["nombre"] as String,
                            descripcion = nuevosDatos["descripcion"] as String,
                            fecha = nuevosDatos["fecha"] as String,
                            hora = nuevosDatos["hora"] as String,
                            ubicacion = nuevosDatos["ubicacion"] as String
                        )
                        eventAdapter.notifyItemChanged(position)
                    }
                    .addOnFailureListener { error ->
                        Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .setNegativeButton("Cancelar", null)
            .create()
        dialog.show()
    }
        }