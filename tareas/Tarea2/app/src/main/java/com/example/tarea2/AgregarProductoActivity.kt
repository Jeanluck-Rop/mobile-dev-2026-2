package com.example.tarea2

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var btnRegresar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        btnRegresar = findViewById(R.id.btnRegresar)

        btnRegresar.setOnClickListener {
            finish()
        }
    }
}