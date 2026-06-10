package com.example.proyectofinal

import android.app.Activity
import android.content.Intent
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class NavDrawer(
    private val activity: Activity,
    private val drawerLayout: DrawerLayout,
    private val navView: NavigationView
) {
    fun setup() {
        navView.setBackgroundColor(
            androidx.core.content.ContextCompat.getColor(activity, R.color.purple_700)
        )
        navView.itemTextColor = android.content.res.ColorStateList.valueOf(
            androidx.core.content.ContextCompat.getColor(activity, android.R.color.white)
        )
        navView.itemIconTintList = android.content.res.ColorStateList.valueOf(
            androidx.core.content.ContextCompat.getColor(activity, android.R.color.white)
        )

        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_mis_pedidos -> {
                    activity.startActivity(Intent(activity, MisPedidosActivity::class.java))
                }
                R.id.nav_mis_productos -> {
                    activity.startActivity(Intent(activity, MisProductosActivity::class.java))
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }
}
