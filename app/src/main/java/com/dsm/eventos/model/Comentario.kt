package com.dsm.eventos.model

import com.google.firebase.firestore.Exclude
import java.util.Date

data class Comentario(
    @Exclude var id: String="",
    val eventoId : String="",
    val usuario:    String="",
    val comentario: String = "",
    var calificacion: Float=0f,
    var timestamp: Date = Date()
)