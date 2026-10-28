package com.example.lab10ejercicio

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.lab10ejercicio.ui.theme.Lab10EjercicioTheme
import com.example.lab10ejercicio.view.ProductosApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab10EjercicioTheme {
                // Punto de entrada de la interfaz: CRUD de productos
                ProductosApp()
            }
        }
    }
}
