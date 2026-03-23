package com.example.tarea2

import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnAgregarPedido: Button
    private lateinit var btnAgregarProducto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initMainButtons()
    }

    private fun initMainButtons() {
        btnAgregarPedido = findViewById(R.id.btnAgregarPedido)
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto)

        btnAgregarPedido.setOnClickListener {
            setContentView(R.layout.activity_agregar_pedido)

            findViewById<Button>(R.id.btnRegresar).setOnClickListener {
                setContentView(R.layout.activity_main)
                initMainButtons()
            }
        }

        btnAgregarProducto.setOnClickListener {
            setContentView(R.layout.activity_agregar_producto)

            findViewById<Button>(R.id.btnRegresar).setOnClickListener {
                setContentView(R.layout.activity_main)
                initMainButtons()
            }
        }
    }
}