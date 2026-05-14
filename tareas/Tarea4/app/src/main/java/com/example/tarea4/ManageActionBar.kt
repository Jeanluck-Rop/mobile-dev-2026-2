package com.example.tarea4

import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.Gravity
import android.view.MenuItem
import android.graphics.Color
import android.widget.PopupMenu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.core.content.ContextCompat
import com.google.android.material.navigation.NavigationView
import androidx.core.view.GravityCompat
class ManageActionBar(
    private val activity: AppCompatActivity,
    private val drawerLayout: DrawerLayout,
    private val isHome: Boolean = false,
    private val title: String = activity.getString(R.string.actionbar_title_main)) {

    fun setup() {
        activity.supportActionBar?.apply {
            this.title = this@ManageActionBar.title
            if (isHome) {
                //Menu Hamburguesa
                val burgerIcon = ContextCompat.getDrawable(activity, R.drawable.burger_icon)
                burgerIcon?.setTint(android.graphics.Color.WHITE)
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.burger_icon)
            } else {
                //Regreso a Home
                val homeIcon = ContextCompat.getDrawable(activity, R.drawable.home_icon)
                homeIcon?.setTint(android.graphics.Color.WHITE)
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.home_icon)
            }
            setDisplayShowHomeEnabled(true)
        }
	
        if (isHome) {
            val navView = activity.findViewById<NavigationView>(R.id.nav_view)
            navView?.setNavigationItemSelectedListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.nav_mi_cuenta     -> Log.d("ManageActionBar:", "Click en Nav - Mi Cuenta")
                    R.id.nav_mi_sitio      -> Log.d("ManageActionBar:", "Click en Nav - Mi Sitio")
                    R.id.nav_mis_productos -> Log.d("ManageActionBar:", "Click en Nav - Mis Productos")
                    R.id.nav_cerrar_sesion -> Log.d("ManageActionBar:", "Click en Nav - Cerrar Sesión")
                }
                drawerLayout.closeDrawer(GravityCompat.START)
                true
            }
        }
    }
    
    fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        return true
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            android.R.id.home -> {
                if (isHome) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                        drawerLayout.closeDrawer(GravityCompat.START)
                    } else {
                        drawerLayout.openDrawer(GravityCompat.START)
                    }
                } else {
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    activity.startActivity(intent)
                    activity.finish()
                }
                true
            }

            R.id.action_history -> {
                val intent = Intent(activity, HistoryActivity::class.java)
                activity.startActivity(intent)
                true
            }

            R.id.action_more -> {
                val anchorView = activity.findViewById<android.view.View>(R.id.action_more)
                    ?: activity.window.decorView

                val popup = PopupMenu(activity, anchorView)
                popup.menuInflater.inflate(R.menu.popover_menu, popup.menu)

                try {
                    val fieldMPopup = PopupMenu::class.java.getDeclaredField("mPopup")
                    fieldMPopup.isAccessible = true
                    val mPopup = fieldMPopup.get(popup)
                    mPopup?.javaClass
                        ?.getDeclaredMethod("setForceShowIcon", Boolean::class.java)
                        ?.invoke(mPopup, true)
                } catch (_: Exception) { }

                popup.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.popover_configuracion -> {
			    Log.d("Popover:", "Click en popover - Configuración")
                            true
                        }
                        R.id.popover_ayuda -> {
			    Log.d("Popover:", "Click en popover - Ayuda")
                            true
                        }
                        else -> false
                    }
                }
                popup.show()
                true
            }
            else -> false
        }
    }
}
