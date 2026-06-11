package com.example.proyectofinal

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import java.text.SimpleDateFormat
import java.util.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar
    private lateinit var llListaPedidos: LinearLayout
    private lateinit var tvTotalIngresado: TextView

    private val opcionesFiltro = arrayOf(
        "Todos", "Día", "Semana", "Quincena",
        "Mes", "Trimestre", "Semestre", "Año"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(
            activity = this,
            drawerLayout = drawerLayout,
            isHome = false,
            title = getString(R.string.actionbar_title_historial)
        )
        manageActionBar.setup()
        AppTheme.aplicar(this)

        llListaPedidos   = findViewById(R.id.listaCompletaPedidos)
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
            R.id.action_filter -> { mostrarDialogoFiltros(); true }
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

    //Logica de cada tipo de filtro
    private fun solicitarParametrosDeFiltro(tipo: String) {
        val cal = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        when (tipo) {

            //Sin filtro
            "Todos" -> ejecutarFiltroFinal(null, null)

            //Pide un dia
            "Día" -> {
                DatePickerDialog(this, { _, y, m, d ->
                    val inicio = cal.apply { set(y, m, d, 0, 0, 0) }.time
                    val fin    = cal.apply { set(y, m, d, 23, 59, 59) }.time
                    ejecutarFiltroFinal(sdf.format(inicio), sdf.format(fin))
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show()
            }

            //Pide dia de inicio, muestra ese dia + 6 dias siguientes
            "Semana" -> {
                DatePickerDialog(this, { _, y, m, d ->
                    val inicio = cal.apply { set(y, m, d, 0, 0, 0) }.time
                    cal.add(Calendar.DAY_OF_MONTH, 6)
                    cal.set(Calendar.HOUR_OF_DAY, 23)
                    cal.set(Calendar.MINUTE, 59)
                    cal.set(Calendar.SECOND, 59)
                    ejecutarFiltroFinal(sdf.format(inicio), sdf.format(cal.time))
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show()
            }

            //Pide dia de inicio, muestra ese dia + 14 dias siguientes
            "Quincena" -> {
                DatePickerDialog(this, { _, y, m, d ->
                    val inicio = cal.apply { set(y, m, d, 0, 0, 0) }.time
                    cal.add(Calendar.DAY_OF_MONTH, 14)
                    cal.set(Calendar.HOUR_OF_DAY, 23)
                    cal.set(Calendar.MINUTE, 59)
                    cal.set(Calendar.SECOND, 59)
                    ejecutarFiltroFinal(sdf.format(inicio), sdf.format(cal.time))
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH))
                .show()
            }

            //Pide mes+anho, muestra todos los pedidos de ese mes completo
            "Mes" -> mostrarSelectorMesAnio { y, m ->
                val inicio = cal.apply {
                    set(y, m, 1, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                val fin = cal.apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }.time
                ejecutarFiltroFinal(sdf.format(inicio), sdf.format(fin))
            }

            //Pide mes+anho de inicio, muestra ese mes + 2 meses siguientes
            "Trimestre" -> mostrarSelectorMesAnio { y, m ->
                val inicio = cal.apply {
                    set(y, m, 1, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                cal.add(Calendar.MONTH, 2)
                val fin = cal.apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }.time
                ejecutarFiltroFinal(sdf.format(inicio), sdf.format(fin))
            }

            //Pide mes+anho de inicio, muestra ese mes + 5 meses siguientes
            "Semestre" -> mostrarSelectorMesAnio { y, m ->
                val inicio = cal.apply {
                    set(y, m, 1, 0, 0, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
                cal.add(Calendar.MONTH, 5)
                val fin = cal.apply {
                    set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
                    set(Calendar.HOUR_OF_DAY, 23)
                    set(Calendar.MINUTE, 59)
                    set(Calendar.SECOND, 59)
                }.time
                ejecutarFiltroFinal(sdf.format(inicio), sdf.format(fin))
            }

            //Pide solo el anho, muestra todos los pedidos de ese anho
            "Año" -> {
                val input = NumberPicker(this).apply {
                    minValue = 2020
                    maxValue = 2035
                    value    = cal.get(Calendar.YEAR)
                }
                AlertDialog.Builder(this)
                    .setTitle("Selecciona el año")
                    .setView(input)
                    .setPositiveButton("Filtrar") { _, _ ->
                        val inicio = cal.apply {
                            set(input.value, Calendar.JANUARY, 1, 0, 0, 0)
                        }.time
                        val fin = cal.apply {
                            set(input.value, Calendar.DECEMBER, 31, 23, 59, 59)
                        }.time
                        ejecutarFiltroFinal(sdf.format(inicio), sdf.format(fin))
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        }
    }

    private fun mostrarSelectorMesAnio(onSeleccion: (anio: Int, mes: Int) -> Unit) {
        val cal = Calendar.getInstance()
        val anios = (2020..2035).map { it.toString() }.toTypedArray()
        val meses = arrayOf(
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        )

        val contenedor = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(32, 16, 32, 8)
        }

        val npMes = NumberPicker(this).apply {
            minValue = 0
            maxValue = 11
            displayedValues = meses
            value = cal.get(Calendar.MONTH)
        }
        val npAnio = NumberPicker(this).apply {
            minValue = 0
            maxValue = anios.size - 1
            displayedValues = anios
            value = anios.indexOf(cal.get(Calendar.YEAR).toString()).coerceAtLeast(0)
        }

        contenedor.addView(npMes)
        contenedor.addView(npAnio)

        AlertDialog.Builder(this)
            .setTitle("Selecciona mes y año")
            .setView(contenedor)
            .setPositiveButton("Filtrar") { _, _ ->
                val mesSeleccionado  = npMes.value
                val anioSeleccionado = anios[npAnio.value].toInt()
                onSeleccion(anioSeleccionado, mesSeleccionado)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun ejecutarFiltroFinal(fInicio: String?, fFin: String?) {
        llListaPedidos.removeAllViews()
        val listaFiltrada = AppData.obtenerPedidosFiltrados(this, fInicio, fFin)
        var sumatoriaTotal = 0.0

        if (listaFiltrada.isEmpty()) {
            llListaPedidos.addView(TextView(this).apply {
                text = "No se encontraron registros en el rango establecido."
                setPadding(16, 16, 16, 16)
            })
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
