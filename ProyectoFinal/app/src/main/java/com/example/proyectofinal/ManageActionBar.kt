package com.example.proyectofinal

import android.content.Intent
import android.view.Menu
import android.view.MenuItem
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout

class ManageActionBar(
    private val activity: AppCompatActivity,
    private val drawerLayout: DrawerLayout,
    private val isHome: Boolean = false,
    private val title: String = activity.getString(R.string.actionbar_title_main)) {

    fun setup() {
	activity.supportActionBar?.apply {
            this.title = this@ManageActionBar.title
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            if (isHome) {
                setHomeAsUpIndicator(R.drawable.burger_icon)
            } else {
                setHomeAsUpIndicator(R.drawable.home_icon)
            }
        }
    }
	
    fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater): Boolean {
        menuInflater.inflate(R.menu.action_bar_menu, menu)
        if (activity is HistoryActivity) {
            menu.findItem(R.id.action_history)?.isVisible = false
        }
        return true
    }

     fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (isHome) {
                    if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
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
                activity.startActivity(Intent(activity, HistoryActivity::class.java))
                true
            }
            R.id.action_more -> {
		val anchorView = activity.findViewById<android.view.View>(R.id.action_more)
		    ?: activity.window.decorView
		PopoverMenu(activity, anchorView).show()
		true
	    }
            else -> false
        }
    }
}
