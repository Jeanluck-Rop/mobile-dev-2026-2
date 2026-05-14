package com.example.tarea4

import android.net.Uri

/**
 * AppData, para almacenar en memoria los pedidos/productos
 * sin usar bd, los datos viven mientras la app este abierta
 */
object AppData {
    private var nextProductoId = 1
    private var nextPedidoId = 1

    val productos: MutableList<Producto> = mutableListOf()
    val pedidos: MutableList<Pedido> = mutableListOf()

    fun agregarProducto(nombre: String,
			precio: Double,
			tipo: String,
                        imagenUri: Uri? = null): Producto {
        val p = Producto(id = nextProductoId++, nombre = nombre, precio = precio, tipo = tipo, imagenUri = imagenUri)
        productos.add(p)
        return p
    }

    fun agregarPedido(nombreCliente: String,
		      items: List<ItemPedido>): Pedido {
        val icono = (1..4).random()
        val pedido = Pedido(id = nextPedidoId++,
			    nombreCliente = nombreCliente,
                            items = items,
			    iconoIndex = icono)
        pedidos.add(pedido)
        return pedido
    }
}
