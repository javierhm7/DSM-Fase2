package com.dsm.eventos.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dsm.eventos.R
import com.dsm.eventos.model.Event
import com.google.firebase.auth.FirebaseAuth

class EventAdapter(private val eventList: List<Event>,
                   private val onItemClick: (Event, Int) -> Unit,
                   private val onParticiparClick: (Event) -> Unit,
                   private val onShare: (Event) -> Unit,
                   private val onShareMail: (Event) -> Unit,
                   private val onComentario: (Event) -> Unit) : RecyclerView.Adapter<EventAdapter.EventViewHolder>() {

    // ViewHolder para los ítems
    class EventViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEventName: TextView = view.findViewById(R.id.tvEventName)
        val tvEventDescription: TextView = view.findViewById(R.id.tvEventDescription)
        val tvEventDateTime: TextView = view.findViewById(R.id.tvEventDateTime)
        val tvEventLocation: TextView = view.findViewById(R.id.tvEventLocation)
        val tvEventOrganizador: TextView = view.findViewById(R.id.tvEventOrganizador)
        val tvEventParticipantes: TextView = view.findViewById(R.id.tvEventParticipantes)
        val btnParticipar: Button = view.findViewById(R.id.btnParticipar)
        val btnShare: Button = view.findViewById(R.id.btnShareEvent)
        val btnshareMail:Button=view.findViewById(R.id.btnShareMail)
        val btnsComentario:Button=view.findViewById(R.id.btnComentar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val event = eventList[position]
        holder.tvEventName.text = event.nombre
        holder.tvEventDescription.text = "Detalles: ${event.descripcion}"
        holder.tvEventLocation.text ="Ubicacion: ${event.ubicacion}"
        holder.tvEventDateTime.text = "Fecha: ${event.fecha} Hora: ${event.hora}"
        holder.tvEventOrganizador.text ="Organizador: "+ event.organizador
        holder.tvEventParticipantes.text ="Asistiran"+ event.participantes.toString()
        if (event.participantes.contains(currentUser?.email)) {
            holder.btnParticipar.isEnabled = false
            holder.btnsComentario.isEnabled = true
            holder.btnsComentario.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1BC6B6"))
            holder.btnParticipar.text = "participando"
        } else {
            holder.btnParticipar.isEnabled =true
            holder.btnsComentario.isEnabled = false
            holder.btnsComentario.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#9E9E9E"))
            holder.btnParticipar.text = "Participar"
        }
        holder.itemView.setOnClickListener {
           if(event.organizador.equals(currentUser?.email))
           {
               onItemClick(event, position) // Llamar al callback con el evento y su posición
           }
        }
        holder.btnParticipar.setOnClickListener {
            onParticiparClick(event) // Llama al callback con el evento seleccionado
        }
        holder.btnShare.setOnClickListener {
            onShare(event) // Llama al callback con el evento seleccionado
        }
        holder.btnshareMail.setOnClickListener {
            onShareMail(event) // Llama al callback con el evento seleccionado
        }
        holder.btnsComentario.setOnClickListener()
        {
            onComentario(event)
        }
    }


    override fun getItemCount() = eventList.size
}