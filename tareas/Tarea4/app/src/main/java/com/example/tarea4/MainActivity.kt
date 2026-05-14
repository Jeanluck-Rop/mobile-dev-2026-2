package com.example.tarea4

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class MainActivity : AppCompatActivity() {
    
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar
    
    private lateinit var llListaPedidos: LinearLayout
    private lateinit var llListaProductos: LinearLayout
    
    private lateinit var btnAgregarPedido: Button
    private lateinit var btnAgregarProducto: Button
    
    //Launchers para recibir resultado de las actividades
    private val
	launcherProducto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
	    result -> if (result.resultCode == RESULT_OK) refrescarProductos()
	}
    
    private val
	launcherPedido = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
	    result -> if (result.resultCode == RESULT_OK) refrescarPedidos()
	}
    
    /* */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
	
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
	
        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(activity = this,
					  drawerLayout = drawerLayout,
					  isHome = true,
					  title = getString(R.string.actionbar_title_main))
        manageActionBar.setup()
	
	llListaPedidos = findViewById(R.id.listaPedidos)
        llListaProductos = findViewById(R.id.listaProductos)
        btnAgregarPedido = findViewById(R.id.btnAgregarPedido)
        btnAgregarProducto = findViewById(R.id.btnAgregarProducto)
	
        btnAgregarPedido.setOnClickListener {
	    if (AppData.productos.isEmpty()) {
                Toast.makeText(this,
			       getString(R.string.toast_agregar_productos_primero),
			       Toast.LENGTH_SHORT).show()
            } else {
		launcherProducto.launch(Intent(this, AgregarPedidoActivity::class.java))
	    }
        }

        btnAgregarProducto.setOnClickListener {
	    launcherPedido.launch(Intent(this, AgregarProductoActivity::class.java))
        }
    }

    /* */
    override fun onResume() {
        super.onResume()
        refrescarProductos()
        refrescarPedidos()
    }
    
    /* Logica por el momento sin usar base de datos */
    private fun refrescarProductos() {
        llListaProductos.removeAllViews()
	
        if (AppData.productos.isEmpty()) {
            val tv = TextView(this)
            tv.text = getString(R.string.label_productos_empty)
            tv.setPadding(16, 16, 16, 16)
            llListaProductos.addView(tv)
            return
        }
	
        for (producto in AppData.productos) {
            val card = layoutInflater.inflate(R.layout.item_producto, llListaProductos, false)
            card.findViewById<TextView>(R.id.tvProductoNombre).text = producto.nombre
            card.findViewById<TextView>(R.id.tvProductoPrecio).text = "$${String.format("%.2f", producto.precio)}"
	    val iv = card.findViewById<ImageView>(R.id.ivProductoImagen)
	    if (producto.imagenUri != null) {
		iv.setImageURI(producto.imagenUri)
	    } else {
		iv.setImageResource(android.R.drawable.ic_menu_gallery)
	    }
            llListaProductos.addView(card)
        }
    }
    
    /* Logica por el momento sin usar base de datos */
    private fun refrescarPedidos() {
        llListaPedidos.removeAllViews()
	
        if (AppData.pedidos.isEmpty()) {
            val tv = TextView(this)
            tv.text = getString(R.string.label_pedidos_empty)
            tv.setPadding(16, 16, 16, 16)
            llListaPedidos.addView(tv)
            return
        }
	
        for (pedido in AppData.pedidos.takeLast(5).reversed()) {
            val fila = layoutInflater.inflate(R.layout.item_pedido, llListaPedidos, false)

            val iconoRes = when (pedido.iconoIndex) {
                1 -> R.drawable.pedido_icon1
                2 -> R.drawable.pedido_icon2
		3 -> R.drawable.pedido_icon3
                else -> R.drawable.pedido_icon4
            }
	    
            fila.findViewById<ImageView>(R.id.ivPedidoIcono).setImageResource(iconoRes)
            fila.findViewById<TextView>(R.id.tvPedidoCliente).text = pedido.nombreCliente
            fila.findViewById<TextView>(R.id.tvPedidoItems).text = pedido.resumenItems
            fila.findViewById<TextView>(R.id.tvPedidoTotal).text = "$${String.format("%.2f", pedido.total)}"
	    llListaPedidos.addView(fila)
        }
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return manageActionBar.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return manageActionBar.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }
}
