package com.example.tarea4

import java.io.Serializable

data class ItemPedido(val producto: Producto,
		      val cantidad: Int) : Serializable

data class Pedido(val id: Int,
		  val nombreCliente: String,
		  val items: List<ItemPedido>,
		  val iconoIndex: Int) : Serializable {
    val total: Double get() = items.sumOf { it.producto.precio * it.cantidad }
    val resumenItems: String get() = items.joinToString(", ") { "${it.producto.nombre} x${it.cantidad}" }
}
