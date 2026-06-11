package com.example.proyectofinal

import android.net.Uri
import android.content.Context
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.util.Locale
import java.util.Date
import java.text.SimpleDateFormat

/* */
object AppData {
    private lateinit var dbHelper: DBHelper

    val productos: MutableList<Producto> = mutableListOf()
    val pedidos: MutableList<Pedido> = mutableListOf()

    fun inicializar(context: Context) {
        dbHelper = DBHelper(context)
        // Cargamos lo que haya en la BD a la memoria al arrancar la app
        refrescarListasDesdeBD()
    }

    // Sincroniza las listas de la memoria RAM con el disco duro (SQLite)
    fun refrescarListasDesdeBD() {
        productos.clear()
        pedidos.clear()

        val db = dbHelper.readableDatabase

        //1. Cargar Productos
        val cursorProds = db.rawQuery("SELECT * FROM productos", null)
        if (cursorProds.moveToFirst()) {
            do {
                val id = cursorProds.getInt(cursorProds.getColumnIndexOrThrow("id"))
                val nombre = cursorProds.getString(cursorProds.getColumnIndexOrThrow("nombre"))
                val precio = cursorProds.getDouble(cursorProds.getColumnIndexOrThrow("precio"))
                val tipo = cursorProds.getString(cursorProds.getColumnIndexOrThrow("tipo"))
                val uriStr = cursorProds.getString(cursorProds.getColumnIndexOrThrow("imagenUri"))
                val uri = uriStr?.let { Uri.parse(it) }
                
                productos.add(Producto(id, nombre, precio, tipo, uri))
            } while (cursorProds.moveToNext())
        }
        cursorProds.close()

	// 2. Cargar Pedidos
	val cursorPeds = db.rawQuery(
            "SELECT id, nombreCliente, iconoIndex, fecha_creacion FROM pedidos ORDER BY fecha_creacion DESC",
            null
	)
	if (cursorPeds.moveToFirst()) {
            do {
		val pId     = cursorPeds.getInt(cursorPeds.getColumnIndexOrThrow("id"))
		val cliente = cursorPeds.getString(cursorPeds.getColumnIndexOrThrow("nombreCliente"))
		val icono   = cursorPeds.getInt(cursorPeds.getColumnIndexOrThrow("iconoIndex"))
		val fecha   = cursorPeds.getString(cursorPeds.getColumnIndexOrThrow("fecha_creacion"))
		val items   = extraerItemsRelacionados(db, pId)
		pedidos.add(Pedido(pId, cliente, items, icono, fecha))
            } while (cursorPeds.moveToNext())
	}
	cursorPeds.close()
    }
    
    fun obtenerPedidosFiltrados(context: Context, fInicio: String?, fFin: String?): List<Pedido> {
	val listaResultados = mutableListOf<Pedido>()
	val db = dbHelper.readableDatabase

	var query = "SELECT id, nombreCliente, iconoIndex, fecha_creacion FROM pedidos"
	val args  = mutableListOf<String>()

	if (fInicio != null && fFin != null) {
            query += " WHERE fecha_creacion BETWEEN ? AND ?"
            args.add(fInicio)
            args.add(fFin)
	}
	query += " ORDER BY fecha_creacion DESC"

	val cursor = db.rawQuery(query, args.toTypedArray())
	try {
            if (cursor.moveToFirst()) {
		do {
                    val pId     = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val cliente = cursor.getString(cursor.getColumnIndexOrThrow("nombreCliente"))
                    val icono   = cursor.getInt(cursor.getColumnIndexOrThrow("iconoIndex"))
                    val fecha   = cursor.getString(cursor.getColumnIndexOrThrow("fecha_creacion"))
                    val items   = extraerItemsRelacionados(db, pId)
                    listaResultados.add(Pedido(pId, cliente, items, icono, fecha))
		} while (cursor.moveToNext())
            }
	} finally {
            cursor.close()
	}
	return listaResultados
    }

    private fun extraerItemsRelacionados(db: SQLiteDatabase, pedidoId: Int): List<ItemPedido> {
        val listaItems = mutableListOf<ItemPedido>()
        val query = """
            SELECT pi.cantidad, p.id, p.nombre, p.precio, p.tipo, p.imagenUri, pi.id 
            FROM pedido_items pi
            JOIN productos p ON pi.productoId = p.id
            WHERE pi.pedidoId = ? AND (pi.parentItemId = 0 OR pi.parentItemId IS NULL)
        """
        val cursor = db.rawQuery(query, arrayOf(pedidoId.toString()))
        try {
            if (cursor.moveToFirst()) {
                do {
                    val cant = cursor.getInt(0)
                    val prodId = cursor.getInt(1)
                    val name = cursor.getString(2)
                    val price = cursor.getDouble(3)
                    val type = cursor.getString(4)
                    val uri = cursor.getString(5)
                    val rowId = cursor.getInt(6)

                    val principal = Producto(prodId, name, price, type, uri?.let { Uri.parse(it) })
                    val complementos = extraerExtrasHijos(db, rowId)

                    listaItems.add(ItemPedido(principal, cant, complementos))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return listaItems
    }

    private fun extraerExtrasHijos(db: SQLiteDatabase, parentRowId: Int): List<Producto> {
        val listaExtras = mutableListOf<Producto>()
        val query = """
            SELECT p.id, p.nombre, p.precio, p.tipo, p.imagenUri
            FROM pedido_items pi
            JOIN productos p ON pi.productoId = p.id
            WHERE pi.parentItemId = ?
        """
        val cursor = db.rawQuery(query, arrayOf(parentRowId.toString()))
        try {
            if (cursor.moveToFirst()) {
                do {
                    listaExtras.add(Producto(
                        cursor.getInt(0), cursor.getString(1),
                        cursor.getDouble(2), cursor.getString(3),
                        cursor.getString(4)?.let { Uri.parse(it) }
                    ))
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
        }
        return listaExtras
    }

    fun agregarProducto(nombre: String, precio: Double, tipo: String, imagenUri: Uri?): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put("nombre", nombre)
            put("precio", precio)
            put("tipo", tipo)
            put("imagenUri", imagenUri?.toString())
        }
        return if (db.insert("productos", null, values) != -1L) {
            refrescarListasDesdeBD()
            true
        } else false
    }

    fun agregarPedido(nombreCliente: String, items: List<ItemPedido>): Boolean {
        val db = dbHelper.writableDatabase
        db.beginTransaction()
        try {
            val icono = (1..4).random()
            val valuesPedido = ContentValues().apply {
                put("nombreCliente", nombreCliente)
                put("iconoIndex", icono)
                // Registramos la estampa de tiempo actual de forma automatizada
                put("fecha_creacion", datetime("now", "localtime"))
            }
            val pedidoId = db.insert("pedidos", null, valuesPedido)
            if (pedidoId == -1L) return false

            for (item in items) {
                val valuesItem = ContentValues().apply {
                    put("pedidoId", pedidoId)
                    put("productoId", item.producto.id)
                    put("cantidad", item.cantidad)
                    put("parentItemId", 0)
                }
                val mainRowId = db.insert("pedido_items", null, valuesItem)

                for (extra in item.extras) {
                    val valuesExtra = ContentValues().apply {
                        put("pedidoId", pedidoId)
                        put("productoId", extra.id)
                        put("cantidad", item.cantidad)
                        put("parentItemId", mainRowId)
                    }
                    db.insert("pedido_items", null, valuesExtra)
                }
            }
            db.setTransactionSuccessful()
            refrescarListasDesdeBD()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        } finally {
            db.endTransaction()
        }
    }

    fun actualizarProducto(producto: Producto): Boolean {
	val db = dbHelper.writableDatabase
	val values = ContentValues().apply {
            put("nombre", producto.nombre)
            put("precio", producto.precio)
            put("tipo", producto.tipo)
            put("imagenUri", producto.imagenUri?.toString())
	}
	val filas = db.update("productos", values, "id = ?", arrayOf(producto.id.toString()))
	return if (filas > 0) { refrescarListasDesdeBD(); true } else false
    }

    fun eliminarProducto(productoId: Int): Boolean {
	val db = dbHelper.writableDatabase
	val filas = db.delete("productos", "id = ?", arrayOf(productoId.toString()))
	return if (filas > 0) { refrescarListasDesdeBD(); true } else false
    }

    fun actualizarPedido(pedidoId: Int, nuevoCliente: String): Boolean {
	val db = dbHelper.writableDatabase
	val values = ContentValues().apply {
            put("nombreCliente", nuevoCliente)
	}
	val filas = db.update("pedidos", values, "id = ?", arrayOf(pedidoId.toString()))
	return if (filas > 0) { refrescarListasDesdeBD(); true } else false
    }

    fun eliminarPedido(pedidoId: Int): Boolean {
	val db = dbHelper.writableDatabase
	db.beginTransaction()
	return try {
            db.delete("pedido_items", "pedidoId = ?", arrayOf(pedidoId.toString()))
            val filas = db.delete("pedidos", "id = ?", arrayOf(pedidoId.toString()))
            db.setTransactionSuccessful()
            if (filas > 0) { refrescarListasDesdeBD(); true } else false
	} catch (e: Exception) {
            e.printStackTrace(); false
	} finally {
            db.endTransaction()
	}
    }

    // Funcion auxiliar para emular comandos nativos de tiempo dentro de los ContentValues
    private fun datetime(format: String, modifier: String): String {
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }
}
