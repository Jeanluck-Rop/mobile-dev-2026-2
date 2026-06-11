package com.example.proyectofinal

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.navigation.NavigationView

class PopoverMenu(
    private val activity: AppCompatActivity,
    private val anchorView: View
) {
    fun show() {
        val popup = android.widget.PopupMenu(activity, anchorView)
        popup.menuInflater.inflate(R.menu.popover_menu, popup.menu)

        try {
            val field = android.widget.PopupMenu::class.java.getDeclaredField("mPopup")
            field.isAccessible = true
            val mPopup = field.get(popup)
            mPopup?.javaClass
                ?.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                ?.invoke(mPopup, true)
        } catch (_: Exception) { }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.popover_configuracion -> {
                    mostrarDialogoCambiarColor()
                    true
                }
                R.id.popover_ayuda -> {
                    mostrarDialogoInfo()
                    true
                }
                else -> false
            }
        }
        popup.show()
    }

    //Dialogo Cambiar color
    private fun mostrarDialogoCambiarColor() {
        val opciones  = AppTheme.opciones
        val nombres   = opciones.map { it.first }.toTypedArray()
        val colorActualRes = AppTheme.obtenerColorRes(activity)
        val seleccionInicial = opciones.indexOfFirst { it.second == colorActualRes }.coerceAtLeast(0)
        var seleccion = seleccionInicial

        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.dialog_color_titulo))
            .setSingleChoiceItems(nombres, seleccionInicial) { _, which ->
                seleccion = which
            }
            .setPositiveButton("Aplicar") { _, _ ->
                val nuevoColorRes = opciones[seleccion].second
                AppTheme.guardarColor(activity, nuevoColorRes)
                aplicarColorATodasLasVistas(nuevoColorRes)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun aplicarColorATodasLasVistas(colorResId: Int) {
        val color = ContextCompat.getColor(activity, colorResId)
        val tint  = ColorStateList.valueOf(color)
        val blanco = Color.WHITE

        activity.findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
            ?.setBackgroundColor(color)

        activity.findViewById<Button>(R.id.btnAgregarPedido)
            ?.backgroundTintList = tint
        activity.findViewById<Button>(R.id.btnAgregarProducto)
            ?.backgroundTintList = tint

        activity.findViewById<NavigationView>(R.id.nav_view)?.let { navView ->
            navView.setBackgroundColor(color)
            navView.itemTextColor    = ColorStateList.valueOf(blanco)
            navView.itemIconTintList = ColorStateList.valueOf(blanco)
            navView.getHeaderView(0)?.setBackgroundColor(color)
        }
    }

    //Dialogo Informacion
    private fun mostrarDialogoInfo() {
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.dialog_info_titulo))
            .setMessage(activity.getString(R.string.dialog_info_texto))
            .setPositiveButton("Entendido", null)
            .show()
    }
}
