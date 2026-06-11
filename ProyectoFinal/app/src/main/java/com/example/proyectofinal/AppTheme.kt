package com.example.proyectofinal

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.navigation.NavigationView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.core.widget.CompoundButtonCompat

object AppTheme {

    private const val PREFS_NAME = "orderly_prefs"
    private const val KEY_COLOR_ID = "theme_color_res_id"

    private fun defaultColorRes() = R.color.purple_700

    val opciones: List<Pair<String, Int>> = listOf(
        "Morado"  to R.color.purple_700,
        "Verde"   to R.color.theme_verde,
        "Azul"    to R.color.theme_azul,
        "Naranja" to R.color.theme_naranja,
        "Rojo"    to R.color.theme_rojo,
        "Amarillo" to R.color.theme_amarillo
    )

    fun guardarColor(context: Context, colorResId: Int) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_COLOR_ID, colorResId)
            .apply()
    }

    fun obtenerColorRes(context: Context): Int {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_COLOR_ID, defaultColorRes())
    }

    fun obtenerColor(context: Context): Int {
        return ContextCompat.getColor(context, obtenerColorRes(context))
    }

    fun aplicar(activity: AppCompatActivity) {
        val color = obtenerColor(activity)
        val colorRes = obtenerColorRes(activity)
        val tint = ColorStateList.valueOf(color)
        val blanco = Color.WHITE

	// 1. Toolbar
	activity.findViewById<Toolbar>(R.id.toolbar)?.setBackgroundColor(color)
		
	// 2. Botones de MainActivity
	activity.findViewById<Button>(R.id.btnAgregarPedido)?.backgroundTintList = tint
	activity.findViewById<Button>(R.id.btnAgregarProducto)?.backgroundTintList = tint
	
	// 3. Botones de AgregarProductoActivity
	activity.findViewById<Button>(R.id.btnSeleccionarImagen)?.backgroundTintList = tint
	activity.findViewById<Button>(R.id.btnGuardarProducto)?.backgroundTintList = tint
	
	// 4. RadioButtons de AgregarProductoActivity
	val radioGroup = activity.findViewById<RadioGroup>(R.id.rgTipoProducto)
	if (radioGroup != null) {
            val radioTint = ColorStateList(
		arrayOf(
                    intArrayOf(-android.R.attr.state_checked), //sin seleccionar
                    intArrayOf( android.R.attr.state_checked)  //seleccionado
		),
		intArrayOf(Color.GRAY, color)
            )
            for (i in 0 until radioGroup.childCount) {
		val rb = radioGroup.getChildAt(i) as? RadioButton ?: continue
		androidx.core.widget.CompoundButtonCompat.setButtonTintList(rb, radioTint)
            }
	}
	
	// 6. Botón de AgregarPedidoActivity
	activity.findViewById<Button>(R.id.btnConfirmarPedido)?.backgroundTintList = tint
	
	// 7. NavigationView + header (MainActivity, MisPedidos, MisProductos)
	activity.findViewById<NavigationView>(R.id.nav_view)?.let { navView ->
            navView.setBackgroundColor(color)
            navView.itemTextColor = ColorStateList.valueOf(blanco)
            navView.itemIconTintList = ColorStateList.valueOf(blanco)
            navView.getHeaderView(0)?.setBackgroundColor(color)
	}
    }
}
