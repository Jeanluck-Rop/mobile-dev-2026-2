package com.example.tarea3

import android.util.Log
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class NavDrawer(
    private val drawerLayout: DrawerLayout,
    private val navView: NavigationView) {

    fun setup() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_mi_cuenta     -> Log.d("NavDrawer:", "Click en Nav - Mi Cuenta")
                R.id.nav_mi_sitio      -> Log.d("NavDrawer:", "Click en Nav - Mi Sitio")
                R.id.nav_mis_productos -> Log.d("NavDrawer:", "Click en Nav - Mis Productos")
                R.id.nav_cerrar_sesion -> Log.d("NavDrawer:", "Click en Nav - Cerrar Sesion")
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
