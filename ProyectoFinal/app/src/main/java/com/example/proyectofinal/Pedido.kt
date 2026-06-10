package com.example.proyectofinal

import java.io.Serializable

data class ItemPedido(val producto: Producto,
		      val cantidad: Int,
		      val extras: List<Producto> = emptyList()) : Serializable

data class Pedido(val id: Int,
		  val nombreCliente: String,
		  val items: List<ItemPedido>,
		  val iconoIndex: Int,
		  val fechaCreacion: String) : Serializable {
    val total: Double get() = items.sumOf { item ->
			  val precioBase = item.producto.precio
			  val precioExtras = item.extras.sumOf { it.precio }
			  (precioBase + precioExtras) * item.cantidad
		      }
    
    val resumenItems: String get() = items.joinToString(", ") { item ->
        if (item.extras.isEmpty()) {
            "${item.producto.nombre} x${item.cantidad}"
        } else {
            val stringExtras = item.extras.joinToString("+") { it.nombre }
            "${item.producto.nombre} ($stringExtras) x${item.cantidad}"
        }
    }
}
