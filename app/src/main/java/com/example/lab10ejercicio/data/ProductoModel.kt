package com.example.lab10ejercicio.data

import com.google.gson.annotations.SerializedName

// Modelo de un producto de la API pública de MockAPI.io.
// MockAPI genera el id como texto ("1", "2"...). Es nullable para que en un POST
// no se envíe (Gson omite los campos null) y el servidor asigne uno nuevo.
data class ProductoModel(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("nombre")
    var nombre: String,
    @SerializedName("precio")
    var precio: Double,
    @SerializedName("stock")
    var stock: Int,
    @SerializedName("categoria")
    var categoria: String
)
