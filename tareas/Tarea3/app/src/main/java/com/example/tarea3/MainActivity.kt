package com.example.tarea3

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar

    private lateinit var btnAgregarPedido: Button
    private lateinit var btnAgregarProducto: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(
            activity     = this,
            drawerLayout = drawerLayout,
            isHome       = true,
            title        = getString(R.string.actionbar_title_main)
        )
        manageActionBar.setup()

        btnAgregarPedido   = findViewById(R.id.btnAgregarPedido)
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto)

        btnAgregarPedido.setOnClickListener {
            startActivity(Intent(this, AgregarPedidoActivity::class.java))
        }

        btnAgregarProducto.setOnClickListener {
            startActivity(Intent(this, AgregarProductoActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return manageActionBar.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return manageActionBar.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }
}
