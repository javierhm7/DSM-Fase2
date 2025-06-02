package com.dsm.eventos.model

import com.google.firebase.firestore.Exclude

data class Event(
    @Exclude var id: String = "",
    val nombre: String="",
    val descripcion: String="",
    val ubicacion: String="",
    val fecha: String="",
    val hora: String="",
    val organizador: String="",
    val participantes: List<String> = emptyList(),
)