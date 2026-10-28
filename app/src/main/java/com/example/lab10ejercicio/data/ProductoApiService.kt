package com.example.lab10ejercicio.data

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Endpoints CRUD del recurso "productos" en MockAPI.io.
// Retrofit genera la implementación a partir de las anotaciones.
interface ProductoApiService {

    // GET /productos -> lista todos los productos
    @GET("productos")
    suspend fun selectProductos(): ArrayList<ProductoModel>

    // GET /productos/{id} -> obtiene un producto
    @GET("productos/{id}")
    suspend fun selectProducto(@Path("id") id: String): Response<ProductoModel>

    // POST /productos -> crea un producto
    @Headers("Content-Type: application/json")
    @POST("productos")
    suspend fun insertProducto(@Body producto: ProductoModel): Response<ProductoModel>

    // PUT /productos/{id} -> actualiza un producto
    @PUT("productos/{id}")
    suspend fun updateProducto(@Path("id") id: String, @Body producto: ProductoModel): Response<ProductoModel>

    // DELETE /productos/{id} -> elimina un producto
    @DELETE("productos/{id}")
    suspend fun deleteProducto(@Path("id") id: String): Response<ProductoModel>
}
