package com.example.tarea4

import android.net.Uri
import java.io.Serializable

data class Producto(val id: Int,
		    val nombre: String,
		    val precio: Double,
		    val tipo: String,
                    val imagenUri: Uri? = null) : Serializable
