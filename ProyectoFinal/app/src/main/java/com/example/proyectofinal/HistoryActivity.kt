package com.example.proyectofinal

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar
    private lateinit var llListaPedidos: LinearLayout
    private lateinit var tvTotalIngresado: TextView

    private val opcionesFiltro = arrayOf("Todos", "Día", "Semana", "Quincena", "Mes", "Trimestre", "Semestre", "Año")
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

	val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
	setSupportActionBar(toolbar)
		
        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(activity = this,
					  drawerLayout = drawerLayout,
					  isHome = false,
					  title = getString(R.string.actionbar_title_historial))
        manageActionBar.setup()

	AppTheme.aplicar(this)
	
	llListaPedidos = findViewById(R.id.listaCompletaPedidos)
	tvTotalIngresado = findViewById(R.id.tvTotalIngresado)
	
	ejecutarFiltroFinal(null, null)
    }
    
    override fun onResume() {
        super.onResume()
	AppTheme.aplicar(this)
	ejecutarFiltroFinal(null, null)
    }
    
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.history_menu, menu)
        return true
    }
    
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_filter -> {
                mostrarDialogoFiltros()
                true
            }
            else -> manageActionBar.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
        }
    }

    private fun mostrarDialogoFiltros() {
        var seleccion = 0
        AlertDialog.Builder(this)
            .setTitle("Opciones de Filtro")
            .setSingleChoiceItems(opcionesFiltro, 0) { _, which -> seleccion = which }
            .setPositiveButton("Siguiente") { dialog, _ ->
                dialog.dismiss()
                solicitarParametrosDeFiltro(opcionesFiltro[seleccion])
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun solicitarParametrosDeFiltro(tipo: String) {
        val calendar = Calendar.getInstance()
        when (tipo) {
            "Todos" -> ejecutarFiltroFinal(null, null)
            "Año" -> {
                val input = NumberPicker(this).apply {
                    minValue = 2020
                    maxValue = 2030
                    value = calendar.get(Calendar.YEAR)
                }
                AlertDialog.Builder(this)
                    .setTitle("Selecciona el Año")
                    .setView(input)
                    .setPositiveButton("Filtrar") { _, _ ->
                        ejecutarFiltroFinal("${input.value}-01-01 00:00:00", "${input.value}-12-31 23:59:59")
                    }
                    .show()
            }
            "Mes" -> {
                DatePickerDialog(this, { _, year, month, _ ->
                    val mesStr = String.format("%02d", month + 1)
                    val fInicio = "$year-$mesStr-01 00:00:00"
                    val fFin = "date('$year-$mesStr-01', '+1 month', '-1 day') || ' 23:59:59'"
                    ejecutarFiltroFinal(fInicio, fFin)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 1).show()
            }
            else -> {
                // Intervalos continuos (Día, Semana, Quincena, Trimestre, Semestre)
                DatePickerDialog(this, { _, year, month, dayOfMonth ->
                    val dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                    val (fInicio, fFin) = when (tipo) {
                        "Día" -> Pair("$dateStr 00:00:00", "$dateStr 23:59:59")
                        "Semana" -> Pair("$dateStr 00:00:00", "date('$dateStr', '+7 days') || ' 23:59:59'")
                        "Quincena" -> Pair("$dateStr 00:00:00", "date('$dateStr', '+15 days') || ' 23:59:59'")
                        "Trimestre" -> Pair("$dateStr 00:00:00", "date('$dateStr', '+90 days') || ' 23:59:59'")
                        "Semestre" -> Pair("$dateStr 00:00:00", "date('$dateStr', '+180 days') || ' 23:59:59'")
                        else -> Pair("$dateStr 00:00:00", "$dateStr 23:59:59")
                    }
                    ejecutarFiltroFinal(fInicio, fFin)
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
    }

    private fun ejecutarFiltroFinal(fInicio: String?, fFin: String?) {
        llListaPedidos.removeAllViews()
        val listaFiltrada = AppData.obtenerPedidosFiltrados(this, fInicio, fFin)
        var sumatoriaTotal = 0.0

        if (listaFiltrada.isEmpty()) {
            val tv = TextView(this).apply {
                text = "No se encontraron registros en el rango establecido."
                setPadding(16, 16, 16, 16)
            }
            llListaPedidos.addView(tv)
            tvTotalIngresado.text = "Total Ingresado: $0.00"
            return
        }

        for (pedido in listaFiltrada) {
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
            
            sumatoriaTotal += pedido.total
            llListaPedidos.addView(fila)
        }
        tvTotalIngresado.text = "Total Ingresado: $${String.format("%.2f", sumatoriaTotal)}"
    }
}
