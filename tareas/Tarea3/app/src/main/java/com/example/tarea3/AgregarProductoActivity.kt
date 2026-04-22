package com.example.tarea3

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(
            activity     = this,
            drawerLayout = drawerLayout,
            isHome       = false,
            title        = getString(R.string.actionbar_title_producto)
        )
        manageActionBar.setup()

        //Aqui ira la logica de agregar pedido
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return manageActionBar.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return manageActionBar.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }
}
