package com.example.tarea4

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class HistoryActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar
    private lateinit var llListaPedidos: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

	ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
	    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
	    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
	    insets
	}
		
        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(activity = this,
					  drawerLayout = drawerLayout,
					  isHome = false,
					  title = getString(R.string.actionbar_title_historial))
        manageActionBar.setup()
	
	llListaPedidos = findViewById(R.id.listaCompletaPedidos)
        cargarHistorial()
    }
    
    override fun onResume() {
        super.onResume()
        cargarHistorial()
    }

    private fun cargarHistorial() {
        llListaPedidos.removeAllViews()
	
        if (AppData.pedidos.isEmpty()) {
            val tv = TextView(this)
            tv.text = getString(R.string.label_pedidos_empty)
            tv.setPadding(16, 16, 16, 16)
            llListaPedidos.addView(tv)
            return
        }
	
        for (pedido in AppData.pedidos.reversed()) {
            val fila = layoutInflater.inflate(R.layout.item_pedido, llListaPedidos, false)
	    
            val ivIcono = fila.findViewById<ImageView>(R.id.ivPedidoIcono)
            val tvCliente = fila.findViewById<TextView>(R.id.tvPedidoCliente)
            val tvItems = fila.findViewById<TextView>(R.id.tvPedidoItems)
            val tvTotal = fila.findViewById<TextView>(R.id.tvPedidoTotal)
	    
            val iconoRes = when (pedido.iconoIndex) {
                1 -> R.drawable.pedido_icon1
                2 -> R.drawable.pedido_icon2
		3 -> R.drawable.pedido_icon3
                else -> R.drawable.pedido_icon4
            }
            ivIcono.setImageResource(iconoRes)

            tvCliente.text = pedido.nombreCliente
            tvItems.text = pedido.resumenItems
            tvTotal.text = "$${String.format("%.2f", pedido.total)}"
	    
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
