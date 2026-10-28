package com.example.lab10ejercicio.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.lab10ejercicio.data.ProductoApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// URL base de la API pública creada en MockAPI.io (usa HTTPS)
private const val URL_BASE = "https://6ae16c94be3bc8fead9879f3.mockapi.io/"

@Composable
fun ProductosApp() {
    // Retrofit se crea una sola vez: URL base + conversor Gson
    val servicio = remember {
        Retrofit.Builder()
            .baseUrl(URL_BASE)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductoApiService::class.java)
    }
    val navController = rememberNavController()

    Scaffold(
        topBar = { BarraSuperior() },
        bottomBar = { BarraInferior(navController) },
        floatingActionButton = { BotonFAB(navController) },
        content = { paddingValues -> Contenido(paddingValues, navController, servicio) }
    )
}

// Botón "+" visible solo en el listado; abre el formulario de nuevo producto
@Composable
fun BotonFAB(navController: NavHostController) {
    val cbeState by navController.currentBackStackEntryAsState()
    if (cbeState?.destination?.route == "productos") {
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary,
            onClick = { navController.navigate("productoNuevo") }
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Agregar")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BarraSuperior() {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "PRODUCTOS APP",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    )
}

// Barra inferior con colores del tema (buen contraste en modo claro y oscuro)
@Composable
fun BarraInferior(navController: NavHostController) {
    val cbeState by navController.currentBackStackEntryAsState()
    val rutaActual = cbeState?.destination?.route
    val colores = NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
        selectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
        indicatorColor = MaterialTheme.colorScheme.primary,
        unselectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
        unselectedTextColor = MaterialTheme.colorScheme.onPrimaryContainer
    )
    NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.Home, contentDescription = "Inicio") },
            label = { Text("Inicio") },
            selected = rutaActual == "inicio",
            colors = colores,
            onClick = { navController.navigate("inicio") { launchSingleTop = true } }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Outlined.ShoppingCart, contentDescription = "Productos") },
            label = { Text("Productos") },
            selected = rutaActual == "productos",
            colors = colores,
            onClick = { navController.navigate("productos") { launchSingleTop = true } }
        )
    }
}

// Rutas de navegación de la app
@Composable
fun Contenido(pv: PaddingValues, navController: NavHostController, servicio: ProductoApiService) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(pv)
    ) {
        NavHost(navController = navController, startDestination = "inicio") {
            composable("inicio") { ScreenInicio() }
            composable("productos") { ContenidoProductosListado(navController, servicio) }
            composable("productoNuevo") { ContenidoProductoEditar(navController, servicio, null) }
            composable(
                "productoVer/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                ContenidoProductoEditar(navController, servicio, it.arguments?.getString("id"))
            }
            composable(
                "productoDel/{id}",
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) {
                ContenidoProductoEliminar(navController, servicio, it.arguments?.getString("id") ?: "")
            }
        }
    }
}
