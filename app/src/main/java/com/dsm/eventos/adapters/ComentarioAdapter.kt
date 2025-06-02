package com.dsm.eventos.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dsm.eventos.R
import com.dsm.eventos.model.Comentario
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComentarioAdapter(private val comentarioList: List<Comentario>,
                        private val onRatingChanged: (Comentario) -> Unit) :
    RecyclerView.Adapter<ComentarioAdapter.ComentarioViewHolder>() {

    class ComentarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvComentario: TextView = view.findViewById(R.id.tvComentario)
        val rbCalificacion: RatingBar = view.findViewById(R.id.rbCalificacion)
        val tvUsuario: TextView = view.findViewById(R.id.tvUsuario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ComentarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_comentario, parent, false)
        return ComentarioViewHolder(view)
    }
    fun formatoFecha(date: Date): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        return formatter.format(date)
    }
    override fun onBindViewHolder(holder: ComentarioViewHolder, position: Int) {
        val comentario = comentarioList[position]
        holder.tvUsuario.text=comentario.usuario +" "+formatoFecha(comentario.timestamp)
        holder.tvComentario.text = comentario.comentario
        holder.rbCalificacion.rating = comentario.calificacion
        holder.rbCalificacion.setOnRatingBarChangeListener { _, rating, fromUser ->
            if (fromUser) {
                comentario.calificacion = rating // Actualizar el modelo
                onRatingChanged(comentario) // Callback para manejar el cambio
            }
        }
    }

    override fun getItemCount() = comentarioList.size
}