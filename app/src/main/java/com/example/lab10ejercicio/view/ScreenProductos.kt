package com.example.lab10ejercicio.view

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.lab10ejercicio.data.ProductoApiService
import com.example.lab10ejercicio.data.ProductoModel
import kotlinx.coroutines.launch
import java.util.Locale

// Vuelve al listado quitando las pantallas intermedias de la pila de navegación
private fun irAListado(navController: NavHostController) {
    navController.navigate("productos") {
        popUpTo("productos") { inclusive = true }
    }
}

// ---------------------------------------------------------------------------
// LISTADO: GET /productos
// ---------------------------------------------------------------------------
@Composable
fun ContenidoProductosListado(navController: NavHostController, servicio: ProductoApiService) {
    val listaProductos = remember { mutableStateListOf<ProductoModel>() }
    var cargando by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    // Al entrar a la pantalla se piden los productos a la API
    LaunchedEffect(Unit) {
        try {
            val listado = servicio.selectProductos()
            listaProductos.clear()
            listaProductos.addAll(listado)
        } catch (e: Exception) {
            error = "No se pudo conectar con la API"
            Log.e("PRODUCTO-LISTADO", "Error: ${e.message}")
        }
        cargando = false
    }

    when {
        cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        error != null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(error!!, color = MaterialTheme.colorScheme.error, fontSize = 18.sp)
        }
        else -> LazyColumn(
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text(
                    text = "Total de productos: ${listaProductos.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
            // Una tarjeta por producto, con botones para editar y eliminar
            items(listaProductos, key = { it.id ?: "" }) { item ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "#${item.id}  ${item.nombre}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = "Categoría: ${item.categoria}", fontSize = 14.sp)
                            Text(
                                text = "Precio: S/ ${String.format(Locale.US, "%.2f", item.precio)}   |   Stock: ${item.stock}",
                                fontSize = 14.sp
                            )
                        }
                        IconButton(onClick = {
                            navController.navigate("productoVer/${item.id}")
                            Log.e("PRODUCTO-VER", "ID = ${item.id}")
                        }) {
                            Icon(Icons.Outlined.Edit, contentDescription = "Editar")
                        }
                        IconButton(onClick = {
                            navController.navigate("productoDel/${item.id}")
                            Log.e("PRODUCTO-DEL", "ID = ${item.id}")
                        }) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "Eliminar",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// NUEVO / EDITAR: si pid es null crea (POST); si no, carga (GET) y actualiza (PUT)
// ---------------------------------------------------------------------------
@Composable
fun ContenidoProductoEditar(navController: NavHostController, servicio: ProductoApiService, pid: String? = null) {
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("") }
    var grabando by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val esNuevo = pid == null

    // Si es edición, se cargan los datos actuales del producto
    if (!esNuevo) {
        LaunchedEffect(Unit) {
            try {
                servicio.selectProducto(pid!!).body()?.let { p ->
                    nombre = p.nombre
                    precio = p.precio.toString()
                    stock = p.stock.toString()
                    categoria = p.categoria
                }
            } catch (e: Exception) {
                Log.e("PRODUCTO-EDITAR", "Error: ${e.message}")
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (esNuevo) "Nuevo producto" else "Editar producto #$pid",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio (S/)") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = stock,
            onValueChange = { stock = it },
            label = { Text("Stock") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = categoria,
            onValueChange = { categoria = it },
            label = { Text("Categoría") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            enabled = nombre.isNotBlank() && !grabando,
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                grabando = true
                val producto = ProductoModel(
                    id = pid,
                    nombre = nombre.trim(),
                    precio = precio.replace(",", ".").toDoubleOrNull() ?: 0.0,
                    stock = stock.toIntOrNull() ?: 0,
                    categoria = categoria.trim()
                )
                scope.launch {
                    try {
                        val respuesta = if (esNuevo)
                            servicio.insertProducto(producto)           // POST /productos
                        else
                            servicio.updateProducto(pid!!, producto)    // PUT /productos/{id}
                        val mensaje = if (respuesta.isSuccessful)
                            (if (esNuevo) "Producto creado (id ${respuesta.body()?.id})" else "Producto actualizado")
                        else "Error HTTP ${respuesta.code()}"
                        Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                        Log.e("PRODUCTO-GRABAR", "Error: ${e.message}")
                    }
                    irAListado(navController)
                }
            }
        ) {
            Text(if (grabando) "Grabando..." else "Grabar", fontSize = 16.sp)
        }
    }
}

// ---------------------------------------------------------------------------
// ELIMINAR: confirmación + DELETE /productos/{id}
// ---------------------------------------------------------------------------
@Composable
fun ContenidoProductoEliminar(navController: NavHostController, servicio: ProductoApiService, id: String) {
    var showDialog by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                irAListado(navController)
            },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Está seguro de eliminar el producto #$id?") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    scope.launch {
                        try {
                            val respuesta = servicio.deleteProducto(id)
                            val mensaje = if (respuesta.isSuccessful) "Producto eliminado"
                                          else "Error HTTP ${respuesta.code()}"
                            Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
                            Log.e("PRODUCTO-ELIMINAR", "Error: ${e.message}")
                        }
                        irAListado(navController)
                    }
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    irAListado(navController)
                }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
