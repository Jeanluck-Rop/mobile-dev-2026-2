package com.example.tarea4

import android.util.Log
import android.view.View
import android.widget.PopupMenu
import androidx.appcompat.app.AppCompatActivity

class PopoverMenu(
    private val activity: AppCompatActivity,
    private val anchorView: View) {
    fun show() {
        val popup = PopupMenu(activity, anchorView)
        popup.menuInflater.inflate(R.menu.popover_menu, popup.menu)
	
        try {
            val field = PopupMenu::class.java.getDeclaredField("mPopup")
            field.isAccessible = true
            val mPopup = field.get(popup)
            mPopup?.javaClass
                ?.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                ?.invoke(mPopup, true)
        } catch (_: Exception) { }

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.popover_configuracion -> {
                    Log.d("PopoverMenu:", "Click en Popover - Configuracion")
                    true
                }
                R.id.popover_ayuda -> {
                    Log.d("PopoverMenu:", "Click en Popover -Ayuda")
                    true
                }
                else -> false
            }
        }

        popup.show()
    }
}
