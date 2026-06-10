package com.example.proyectofinal

import android.content.Intent
import android.net.Uri
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

class MisProductosActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar
    private lateinit var etBuscar: EditText

    // Contenedores de cada sección
    private lateinit var tvTituloAlimento: TextView
    private lateinit var hsvAlimentos: HorizontalScrollView
    private lateinit var llAlimentos: LinearLayout

    private lateinit var tvTituloBebida: TextView
    private lateinit var hsvBebidas: HorizontalScrollView
    private lateinit var llBebidas: LinearLayout

    private lateinit var tvTituloPostre: TextView
    private lateinit var hsvPostres: HorizontalScrollView
    private lateinit var llPostres: LinearLayout

    private lateinit var tvTituloExtra: TextView
    private lateinit var hsvExtras: HorizontalScrollView
    private lateinit var llExtras: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_productos)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(
            activity = this,
            drawerLayout = drawerLayout,
            isHome = false,
            title = getString(R.string.nav_mis_productos)
        )
        manageActionBar.setup()

	AppTheme.aplicar(this)
	
        val navView = findViewById<NavigationView>(R.id.nav_view)
        NavDrawer(activity = this, drawerLayout = drawerLayout, navView = navView).setup()

        etBuscar = findViewById(R.id.etBuscarProducto)

        tvTituloAlimento = findViewById(R.id.tvTituloAlimento)
        hsvAlimentos     = findViewById(R.id.hsvAlimentos)
        llAlimentos      = findViewById(R.id.llAlimentos)

        tvTituloBebida   = findViewById(R.id.tvTituloBebida)
        hsvBebidas       = findViewById(R.id.hsvBebidas)
        llBebidas        = findViewById(R.id.llBebidas)

        tvTituloPostre   = findViewById(R.id.tvTituloPostre)
        hsvPostres       = findViewById(R.id.hsvPostres)
        llPostres        = findViewById(R.id.llPostres)

        tvTituloExtra    = findViewById(R.id.tvTituloExtra)
        hsvExtras        = findViewById(R.id.hsvExtras)
        llExtras         = findViewById(R.id.llExtras)

        etBuscar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                renderizarSecciones(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
	AppTheme.aplicar(this)
        AppData.refrescarListasDesdeBD()
        renderizarSecciones(etBuscar.text.toString())
    }

    private fun renderizarSecciones(query: String) {
        val strAlimento = getString(R.string.agregar_producto_alimento)
        val strBebida   = getString(R.string.agregar_producto_bebida)
        val strPostre   = getString(R.string.agregar_producto_postre)
        val strExtra    = getString(R.string.agregar_producto_extra)

        val filtrados = if (query.isBlank()) AppData.productos
                        else AppData.productos.filter {
                            it.nombre.contains(query, ignoreCase = true)
                        }

        renderizarGrupo(filtrados.filter { it.tipo == strAlimento }, llAlimentos,
                        tvTituloAlimento, hsvAlimentos)
        renderizarGrupo(filtrados.filter { it.tipo == strBebida   }, llBebidas,
                        tvTituloBebida, hsvBebidas)
        renderizarGrupo(filtrados.filter { it.tipo == strPostre   }, llPostres,
                        tvTituloPostre, hsvPostres)
        renderizarGrupo(filtrados.filter { it.tipo == strExtra    }, llExtras,
                        tvTituloExtra, hsvExtras)
    }

    // Rellena un HorizontalScrollView con tarjetas de item_producto y oculta la sección si está vacía
    private fun renderizarGrupo(
        lista: List<Producto>,
        contenedor: LinearLayout,
        titulo: TextView,
        hsv: HorizontalScrollView
    ) {
        contenedor.removeAllViews()

        if (lista.isEmpty()) {
            titulo.visibility = View.GONE
            hsv.visibility    = View.GONE
            return
        }

        titulo.visibility = View.VISIBLE
        hsv.visibility    = View.VISIBLE

        for (producto in lista) {
            val card = layoutInflater.inflate(R.layout.item_producto, contenedor, false)

            val iv     = card.findViewById<ImageView>(R.id.ivProductoImagen)
            val tvNom  = card.findViewById<TextView>(R.id.tvProductoNombre)
            val tvPre  = card.findViewById<TextView>(R.id.tvProductoPrecio)
            val tvTipo = card.findViewById<TextView>(R.id.tvProductoTipo)

            if (producto.imagenUri != null) {
                iv.setImageURI(producto.imagenUri)
            } else {
                iv.setImageResource(android.R.drawable.ic_menu_gallery)
            }
            tvNom.text  = producto.nombre
            tvPre.text  = "$${String.format("%.2f", producto.precio)}"
            tvTipo.text = producto.tipo

            card.setOnClickListener { mostrarDialogoEdicion(producto) }

            contenedor.addView(card)
        }
    }

    private fun mostrarDialogoEdicion(producto: Producto) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_producto, null)

        val iv          = dialogView.findViewById<ImageView>(R.id.ivDialogProducto)
        val etNombre    = dialogView.findViewById<EditText>(R.id.etDialogNombre)
        val etPrecio    = dialogView.findViewById<EditText>(R.id.etDialogPrecio)
        val rgTipo      = dialogView.findViewById<RadioGroup>(R.id.rgDialogTipo)
        val btnGuardar  = dialogView.findViewById<Button>(R.id.btnDialogGuardarProducto)
        val btnEliminar = dialogView.findViewById<Button>(R.id.btnDialogEliminarProducto)

        // Rellenar datos actuales
        if (producto.imagenUri != null) iv.setImageURI(producto.imagenUri)
        etNombre.setText(producto.nombre)
        etPrecio.setText(String.format("%.2f", producto.precio))

        val strAlimento = getString(R.string.agregar_producto_alimento)
        val strBebida   = getString(R.string.agregar_producto_bebida)
        val strPostre   = getString(R.string.agregar_producto_postre)

        rgTipo.check(when (producto.tipo) {
            strBebida -> R.id.rbDialogBebida
            strPostre -> R.id.rbDialogPostre
            getString(R.string.agregar_producto_extra) -> R.id.rbDialogExtra
            else -> R.id.rbDialogAlimento
        })

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar producto")
            .setView(dialogView)
            .create()

        btnGuardar.setOnClickListener {
            val nuevoNombre = etNombre.text.toString().trim()
            val nuevoPrecioStr = etPrecio.text.toString().trim()

            if (nuevoNombre.isEmpty()) {
                etNombre.error = "El nombre no puede estar vacío"
                return@setOnClickListener
            }
            val nuevoPrecio = nuevoPrecioStr.toDoubleOrNull()
            if (nuevoPrecio == null || nuevoPrecio <= 0) {
                etPrecio.error = "Precio inválido"
                return@setOnClickListener
            }
            val nuevoTipo = when (rgTipo.checkedRadioButtonId) {
                R.id.rbDialogBebida -> strBebida
                R.id.rbDialogPostre -> strPostre
                R.id.rbDialogExtra  -> getString(R.string.agregar_producto_extra)
                else -> strAlimento
            }

            AlertDialog.Builder(this)
                .setTitle("Guardar cambios")
                .setMessage("¿Confirmas guardar los cambios en \"${producto.nombre}\"?")
                .setPositiveButton("Guardar") { _, _ ->
                    val productoActualizado = producto.copy(
                        nombre = nuevoNombre,
                        precio = nuevoPrecio,
                        tipo   = nuevoTipo
                    )
                    val exito = AppData.actualizarProducto(productoActualizado)
                    Toast.makeText(
                        this,
                        if (exito) "Producto actualizado." else "Error al actualizar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (exito) {
                        dialog.dismiss()
                        renderizarSecciones(etBuscar.text.toString())
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }

        btnEliminar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Eliminar producto")
                .setMessage("¿Estás seguro de que deseas eliminar \"${producto.nombre}\"? Esta acción no se puede deshacer.")
                .setPositiveButton("Eliminar") { _, _ ->
                    val exito = AppData.eliminarProducto(producto.id)
                    Toast.makeText(
                        this,
                        if (exito) "Producto eliminado." else "Error al eliminar.",
                        Toast.LENGTH_SHORT
                    ).show()
                    if (exito) {
                        dialog.dismiss()
                        renderizarSecciones(etBuscar.text.toString())
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
