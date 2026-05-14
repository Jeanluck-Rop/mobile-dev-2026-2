package com.example.tarea4

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class AgregarPedidoActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar

    private lateinit var etNombreCliente: EditText
    private lateinit var llProductos: LinearLayout
    private lateinit var tvTotal: TextView
    private lateinit var btnConfirmar: Button

    //mapa producto.id -> cantidad seleccionada
    private val cantidades = mutableMapOf<Int, Int>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_pedido)

	ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
	    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
	    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
	    insets
	}
		
        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(activity = this,
					  drawerLayout = drawerLayout,
					  isHome = false,
					  title = getString(R.string.actionbar_title_pedido))
        manageActionBar.setup()
	
        etNombreCliente = findViewById(R.id.etNombreCliente)
        llProductos = findViewById(R.id.llProductosPedido)
        tvTotal = findViewById(R.id.tvTotal)
        btnConfirmar = findViewById(R.id.btnConfirmarPedido)
	
        btnConfirmar.setOnClickListener { confirmarPedido() }
	
        cargarProductos()
    }
    
    override fun onResume() {
        super.onResume()
        cargarProductos()
    }
    
    private fun cargarProductos() {
        llProductos.removeAllViews()
        cantidades.clear()
	
        if (AppData.productos.isEmpty()) {
            val tv = TextView(this)
            tv.text = getString(R.string.pedido_sin_productos_registrados)
            tv.setPadding(16, 16, 16, 16)
            llProductos.addView(tv)
            actualizarTotal()
            return
        }
	
        for (producto in AppData.productos) {
            cantidades[producto.id] = 0
	    
            val fila = layoutInflater.inflate(R.layout.item_selector_producto, llProductos, false)
	    
            val tvNombre = fila.findViewById<TextView>(R.id.tvSelectorNombre)
            val tvCantidad = fila.findViewById<TextView>(R.id.tvSelectorCantidad)
            val btnMenos = fila.findViewById<ImageButton>(R.id.btnSelectorMenos)
            val btnMas = fila.findViewById<ImageButton>(R.id.btnSelectorMas)
	    
            tvNombre.text   = "${producto.nombre} — $${String.format("%.2f", producto.precio)}"
            tvCantidad.text = "0"
	    
            btnMenos.setOnClickListener {
                val actual = cantidades[producto.id] ?: 0
                if (actual > 0) {
                    cantidades[producto.id] = actual - 1
                    tvCantidad.text = (actual - 1).toString()
                    actualizarTotal()
                }
            }
	    
            btnMas.setOnClickListener {
                val actual = cantidades[producto.id] ?: 0
                cantidades[producto.id] = actual + 1
                tvCantidad.text = (actual + 1).toString()
                actualizarTotal()
            }
	    
            llProductos.addView(fila)
        }

        actualizarTotal()
    }
    
    private fun actualizarTotal() {
        var total = 0.0
        for (producto in AppData.productos) {
            total += producto.precio * (cantidades[producto.id] ?: 0)
        }
        tvTotal.text = "$${String.format("%.2f", total)}"
    }

    private fun confirmarPedido() {
        val cliente = etNombreCliente.text.toString().trim()
        if (cliente.isEmpty()) {
            etNombreCliente.error = getString(R.string.pedido_error_nombre_cliente)
            return
        }

        val items = AppData.productos
            .filter { (cantidades[it.id] ?: 0) > 0 }
            .map { ItemPedido(it, cantidades[it.id]!!) }
	
        if (items.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_selecciona_producto), Toast.LENGTH_SHORT).show()
            return
        }
	
        AppData.agregarPedido(cliente, items)
        Toast.makeText(this, getString(R.string.toast_pedido_confirmado, cliente), Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return manageActionBar.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return manageActionBar.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }
}
