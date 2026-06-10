package com.example.proyectofinal

import android.app.Activity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import android.content.res.ColorStateList

class AgregarPedidoActivity : AppCompatActivity() {

private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar

    private lateinit var etNombreCliente: EditText
    private lateinit var llProductos: LinearLayout
    private lateinit var tvTotal: TextView
    private lateinit var btnConfirmar: Button

    private val cantidades = mutableMapOf<Int, Int>()
    private val extrasSeleccionados = mutableMapOf<Int, MutableSet<Producto>>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_pedido)

	val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
           
        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(activity = this,
                                  drawerLayout = drawerLayout,
                                  isHome = false,
                                  title = getString(R.string.actionbar_title_pedido))
        manageActionBar.setup()

	AppTheme.aplicar(this)
	
        etNombreCliente = findViewById(R.id.etNombreCliente)
        llProductos = findViewById(R.id.llProductosPedido)
        tvTotal = findViewById(R.id.tvTotal)
        btnConfirmar = findViewById(R.id.btnConfirmarPedido)
    
        btnConfirmar.setOnClickListener { confirmarPedido() }
    
        cargarProductos()
    }
    
    override fun onResume() {
        super.onResume()
	AppTheme.aplicar(this)
        cargarProductos()
    }
    
    private fun cargarProductos() {
        llProductos.removeAllViews()
        cantidades.clear()
        extrasSeleccionados.clear()

        val strAlimento = getString(R.string.agregar_producto_alimento)
        val strExtra = getString(R.string.agregar_producto_extra)

        if (AppData.productos.isEmpty()) {
            val tv = TextView(this)
            tv.text = getString(R.string.pedido_sin_productos_registrados)
            tv.setPadding(16, 16, 16, 16)
            llProductos.addView(tv)
            actualizarTotal()
            return
        }

        // Filtramos la lista de extras disponibles en el sistema
        val extrasDisponibles = AppData.productos.filter { it.tipo == strExtra }
    
        for (producto in AppData.productos) {
            if (producto.tipo == strExtra)
		continue

            cantidades[producto.id] = 0
        
            // Contenedor de la fila para poder añadirle elementos debajo en cascada
            val contenedorItem = LinearLayout(this)
            contenedorItem.orientation = LinearLayout.VERTICAL

            val fila = layoutInflater.inflate(R.layout.item_selector_producto, contenedorItem, false)
        
            val tvNombre = fila.findViewById<TextView>(R.id.tvSelectorNombre)
            val tvCantidad = fila.findViewById<TextView>(R.id.tvSelectorCantidad)
            val btnMenos = fila.findViewById<ImageButton>(R.id.btnSelectorMenos)
            val btnMas = fila.findViewById<ImageButton>(R.id.btnSelectorMas)
        
            tvNombre.text   = "${producto.nombre}: $${String.format("%.2f", producto.precio)}"
            tvCantidad.text = "0"
            
            contenedorItem.addView(fila)

            // Si es un alimento, creamos y preparamos su panel de extras acoplados
            if (producto.tipo == strAlimento) {
                val llExtrasPanel = LinearLayout(this)
                llExtrasPanel.orientation = LinearLayout.VERTICAL
                llExtrasPanel.setPadding(64, 8, 16, 16) // Indentación visual hacia la derecha
                llExtrasPanel.visibility = View.GONE    // Oculto por defecto
                
                val conjuntoSeleccion = mutableSetOf<Producto>()
                extrasSeleccionados[producto.id] = conjuntoSeleccion

                // Agregamos dinámicamente un Checkbox por cada extra disponible
                for (extra in extrasDisponibles) {
                    val cb = CheckBox(this)
                    cb.text = "${extra.nombre} (+$${String.format("%.2f", extra.precio)})"

		    val checkTint = ColorStateList(
			arrayOf(
			    intArrayOf(-android.R.attr.state_checked),
			    intArrayOf( android.R.attr.state_checked)
			),
			intArrayOf(
			    android.graphics.Color.GRAY,
			    AppTheme.obtenerColor(this)
			)
		    )
		    androidx.core.widget.CompoundButtonCompat.setButtonTintList(cb, checkTint)
		    
                    cb.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            conjuntoSeleccion.add(extra)
                        } else {
                            conjuntoSeleccion.remove(extra)
                        }
                        actualizarTotal() // Recalcula el total al activar/desactivar un extra
                    }
                    llExtrasPanel.addView(cb)
                }
                contenedorItem.addView(llExtrasPanel)

                // Listeners modificados para mostrar/ocultar el panel de extras
                btnMenos.setOnClickListener {
                    val actual = cantidades[producto.id] ?: 0
                    if (actual > 0) {
                        val nuevo = actual - 1
                        cantidades[producto.id] = nuevo
                        tvCantidad.text = nuevo.toString()
                        
                        if (nuevo == 0) {
                            llExtrasPanel.visibility = View.GONE
                            // Desmarcar Checkboxes de este alimento si la cantidad regresa a cero
                            for (i in 0 until llExtrasPanel.childCount) {
                                (llExtrasPanel.getChildAt(i) as? CheckBox)?.isChecked = false
                            }
                            conjuntoSeleccion.clear()
                        }
                        actualizarTotal()
                    }
                }
            
                btnMas.setOnClickListener {
                    val actual = cantidades[producto.id] ?: 0
                    val nuevo = actual + 1
                    cantidades[producto.id] = nuevo
                    tvCantidad.text = nuevo.toString()
                    
                    if (nuevo > 0) llExtrasPanel.visibility = View.VISIBLE
                    actualizarTotal()
                }
            } else {
                //Listeners normales para Bebidas o Postres sin lógica de extras
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
            }
        
            llProductos.addView(contenedorItem)
        }

        actualizarTotal()
    }
    
    private fun actualizarTotal() {
        var total = 0.0
        for (producto in AppData.productos) {
            val qty = cantidades[producto.id] ?: 0
            if (qty > 0) {
                val precioBase = producto.precio
                //Sumamos el valor de los extras seleccionados para este producto específico
                val precioExtras = extrasSeleccionados[producto.id]?.sumOf { it.precio } ?: 0.0
                total += (precioBase + precioExtras) * qty
            }
        }
        tvTotal.text = "$${String.format("%.2f", total)}"
    }

    private fun confirmarPedido() {
        val cliente = etNombreCliente.text.toString().trim()
        if (cliente.isEmpty()) {
            etNombreCliente.error = getString(R.string.pedido_error_nombre_cliente)
            return
        }

        //Construimos la lista final mapeando el Alimento con sus respectivos Extras seleccionados
        val items = AppData.productos
            .filter { (cantidades[it.id] ?: 0) > 0 }
            .map { 
                ItemPedido(
                    producto = it, 
                    cantidad = cantidades[it.id]!!,
                    extras = extrasSeleccionados[it.id]?.toList() ?: emptyList() //Guardamos sus extras mapeados
                ) 
            }
    
        if (items.isEmpty()) {
            Toast.makeText(this, getString(R.string.toast_selecciona_producto), Toast.LENGTH_SHORT).show()
            return
        }

        val exito = AppData.agregarPedido(cliente, items)
        if (exito) {
            Toast.makeText(this, getString(R.string.toast_pedido_confirmado, cliente), Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        } else {
            Toast.makeText(this, "Error al registrar el pedido en la BD", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return manageActionBar.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return manageActionBar.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }
}
