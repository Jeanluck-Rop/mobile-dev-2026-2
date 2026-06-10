package com.example.proyectofinal

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "proyectofinal.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase) {
        //Tabla de Productos
        db.execSQL("""
            CREATE TABLE productos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT,
                precio REAL,
                tipo TEXT,
                imagenUri TEXT
            )
        """.trimIndent())

        //Tabla de Pedidos (Cabecera)
        db.execSQL("""
            CREATE TABLE pedidos (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nombreCliente TEXT,
                iconoIndex INTEGER,
		fecha_creacion TEXT
            )
        """.trimIndent())

        //Tabla Intermedia (Detalle del pedido)
        db.execSQL("""
            CREATE TABLE pedido_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                pedidoId INTEGER,
                productoId INTEGER,
                cantidad INTEGER,
		parentItemId INTEGER DEFAULT 0
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS pedido_items")
        db.execSQL("DROP TABLE IF EXISTS pedidos")
        db.execSQL("DROP TABLE IF EXISTS productos")
        onCreate(db)
    }
}
