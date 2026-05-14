package com.example.tarea4

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout

class AgregarProductoActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var manageActionBar: ManageActionBar

    private lateinit var rgTipo: RadioGroup
    private lateinit var etNombre: EditText
    private lateinit var etPrecio: EditText
    private lateinit var btnGuardar: Button

    private lateinit var btnImagen:  Button
    private lateinit var ivProducto: ImageView

    private var imagenUri: Uri? = null

    //Launcher: abrimos selector de imagen del sistema
    private val imagePicker = registerForActivityResult(
	ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
	if (uri != null) {
	    //Permiso persistente para leer el Uri aunque la app se reinicie en la sesion
            try {
		contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
		)
            } catch (e: Exception) {
		e.printStackTrace()
            }
            imagenUri = uri
            ivProducto.setImageURI(uri)
        }
    }

    //Launcher: solicitamos permiso de galeria
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            Toast.makeText(
                this,
                getString(R.string.producto_permiso_imagen_denegado),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_producto)

	ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
	    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
	    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
	    insets
	}
		
        drawerLayout = findViewById(R.id.drawer_layout)
        manageActionBar = ManageActionBar(activity = this,
					  drawerLayout = drawerLayout,
					  isHome = false,
					  title = getString(R.string.actionbar_title_producto))
        manageActionBar.setup()

        rgTipo = findViewById(R.id.rgTipoProducto)
        etNombre = findViewById(R.id.etNombreProducto)
        etPrecio = findViewById(R.id.etPrecioProducto)
        btnGuardar = findViewById(R.id.btnGuardarProducto)
	btnImagen  = findViewById(R.id.btnSeleccionarImagen)
        ivProducto = findViewById(R.id.ivProducto)

	btnImagen.setOnClickListener  { solicitarImagen() }
        btnGuardar.setOnClickListener { guardarProducto() }
    }

    private fun solicitarImagen() {
        val permiso = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            Manifest.permission.READ_MEDIA_IMAGES
        else
            Manifest.permission.READ_EXTERNAL_STORAGE
	
        when {
            //Si ya tenemos permiso abrimos selector directamente
            ContextCompat.checkSelfPermission(this, permiso) == PackageManager.PERMISSION_GRANTED -> {
               imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
	    //Si no, debemos explicar por quq se necesita el permiso antes de pedirlo
            shouldShowRequestPermissionRationale(permiso) -> {
                Toast.makeText(
                    this,
                    getString(R.string.producto_permiso_imagen_razon),
                    Toast.LENGTH_LONG
                ).show()
                permissionLauncher.launch(permiso)
            }
            //Pedimos permiso directamente
            else -> permissionLauncher.launch(permiso)
        }
    }
	
    private fun guardarProducto() {
        val nombre = etNombre.text.toString().trim()
        val precioStr = etPrecio.text.toString().trim()

        if (nombre.isEmpty()) {
            etNombre.error = getString(R.string.producto_error_nombre)
            return
        }
        if (precioStr.isEmpty()) {
            etPrecio.error = getString(R.string.producto_error_precio_vacio)
            return
        }
        val precio = precioStr.toDoubleOrNull()
        if (precio == null || precio <= 0) {
            etPrecio.error = getString(R.string.producto_error_precio_invalido)
            return
        }

        val tipo = when (rgTipo.checkedRadioButtonId) {
            R.id.rbBebida -> getString(R.string.agregar_producto_bebida)
            R.id.rbPostre -> getString(R.string.agregar_producto_postre)
            R.id.rbExtra -> getString(R.string.agregar_producto_extra)
            else -> getString(R.string.agregar_producto_alimento)
        }

        AppData.agregarProducto(nombre, precio, tipo, imagenUri)

        Toast.makeText(this,  getString(R.string.toast_producto_agregado, nombre), Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return manageActionBar.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return manageActionBar.onOptionsItemSelected(item) || super.onOptionsItemSelected(item)
    }
}
