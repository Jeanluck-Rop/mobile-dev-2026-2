package com.example.proyectofinal

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class MisPedidosActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar
    private lateinit var llLista: LinearLayout
    private lateinit var etBuscar: EditText

    // Lista que se muestra actualmente (filtrada o completa)
    private var listaActual: List<Pedido> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_pedidos)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(
            activity = this,
            drawerLayout = drawerLayout,
            isHome = false,
            title = getString(R.string.nav_mis_pedidos)
        )
        manageActionBar.setup()

	AppTheme.aplicar(this)
	
        val navView = findViewById<NavigationView>(R.id.nav_view)
        NavDrawer(activity = this, drawerLayout = drawerLayout, navView = navView).setup()

        llLista  = findViewById(R.id.llListaMisPedidos)
        etBuscar = findViewById(R.id.etBuscarPedido)

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filtrarYRenderizar(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
	AppTheme.aplicar(this)
        // Recargamos desde BD cada vez que volvemos a la pantalla
        AppData.refrescarListasConContexto(this)
        listaActual = AppData.pedidos.toList()
        filtrarYRenderizar(etBuscar.text.toString())
    }

    // Filtra por nombre de cliente y redibuja la lista
    private fun filtrarYRenderizar(query: String) {
        listaActual = if (query.isBlank()) {
            AppData.pedidos.toList()
        } else {
            AppData.pedidos.filter {
                it.nombreCliente.contains(query, ignoreCase = true)
            }
        }
        renderizarLista()
    }

    private fun renderizarLista() {
        llLista.removeAllViews()

        if (listaActual.isEmpty()) {
            val tv = TextView(this).apply {
                text = "No se encontraron pedidos."
                setPadding(16, 16, 16, 16)
            }
            llLista.addView(tv)
            return
        }

        for (pedido in listaActual) {
            val fila = layoutInflater.inflate(R.layout.item_pedido, llLista, false)

            val iconoRes = when (pedido.iconoIndex) {
                1 -> R.drawable.pedido_icon1
                2 -> R.drawable.pedido_icon2
                3 -> R.drawable.pedido_icon3
                else -> R.drawable.pedido_icon4
            }
            fila.findViewById<ImageView>(R.id.ivPedidoIcono).setImageResource(iconoRes)
            fila.findViewById<TextView>(R.id.tvPedidoCliente).text = pedido.nombreCliente
            fila.findViewById<TextView>(R.id.tvPedidoItems).text = pedido.resumenItems
            fila.findViewById<TextView>(R.id.tvPedidoTotal).text =
                "$${String.format("%.2f", pedido.total)}"

            // Click reactivo → diálogo de edición
            fila.setOnClickListener { mostrarDialogoEdicion(pedido) }

            llLista.addView(fila)
        }
    }

    private fun mostrarDialogoEdicion(pedido: Pedido) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_pedido, null)

        val etCliente  = dialogView.findViewById<EditText>(R.id.etDialogCliente)
        val tvFecha    = dialogView.findViewById<TextView>(R.id.tvDialogFecha)
        val llItems    = dialogView.findViewById<LinearLayout>(R.id.llDialogItems)
        val tvTotal    = dialogView.findViewById<TextView>(R.id.tvDialogTotal)
        val btnGuardar = dialogView.findViewById<Button>(R.id.btnDialogGuardar)
        val btnEliminar = dialogView.findViewById<Button>(R.id.btnDialogEliminar)

        // Rellenar datos
        etCliente.setText(pedido.nombreCliente)
        tvFecha.text = pedido.fechaCreacion
        tvTotal.text = "$${String.format("%.2f", pedido.total)}"

        // Listar productos del pedido
        llItems.removeAllViews()
        for (item in pedido.items) {
            val tv = TextView(this)
            val extrasStr = if (item.extras.isEmpty()) ""
                            else " (+${item.extras.joinToString(", ") { it.nombre }})"
            tv.text = "• ${item.producto.nombre}$extrasStr  ×${item.cantidad}" +
                      "  —  $${String.format("%.2f",
                          (item.producto.precio + item.extras.sumOf { it.precio }) * item.cantidad)}"
            tv.textSize = 13f
            tv.setPadding(0, 2, 0, 2)
            llItems.addView(tv)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Detalles del pedido")
            .setView(dialogView)
            .create()

        btnGuardar.setOnClickListener {
            val nuevoNombre = etCliente.text.toString().trim()
            if (nuevoNombre.isEmpty()) {
                etCliente.error = "El nombre no puede estar vacío"
                return@setOnClickListener
            }
            AlertDialog.Builder(this)
                .setTitle("Guardar cambios")
                .setMessage("¿Confirmas guardar los cambios en este pedido?")
                .setPositiveButton("Guardar") { _, _ ->
                    val exito = AppData.actualizarPedido(pedido.id, nuevoNombre)
                    Toast.makeText(
                        this,
                        if (exito) "Pedido actualizado." else "Error al actualizar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (exito) {
                        dialog.dismiss()
                        filtrarYRenderizar(etBuscar.text.toString())
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar pedido")
                .setMessage("¿Estás seguro de que deseas eliminar el pedido de \"${pedido.nombreCliente}\"? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    val exito = AppData.eliminarPedido(pedido.id)
                    Toast.makeText(
                        this,
                        if (exito) "Pedido eliminado." else "Error al eliminar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (exito) {
                        dialog.dismiss()
                        filtrarYRenderizar(etBuscar.text.toString())
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        dialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return manageActionBar.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return manageActionBar.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }
}
