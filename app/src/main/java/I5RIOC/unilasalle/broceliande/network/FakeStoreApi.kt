package I5RIOC.unilasalle.broceliande.network

import I5RIOC.unilasalle.broceliande.model.Product
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

interface FakeStoreApi {
    // Récupère tous les produits
    @GET("products")
    suspend fun getAllProducts(): List<Product>

    // AJOUT : Récupère la liste des catégories
    // L'endpoint est "products/categories" et renvoie une liste de String
    @GET("products/categories")
    suspend fun getCategories(): List<String>
}

object RetrofitInstance {
    private const val BASE_URL = "https://fakestoreapi.com/"

    val api: FakeStoreApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(FakeStoreApi::class.java)
    }
}