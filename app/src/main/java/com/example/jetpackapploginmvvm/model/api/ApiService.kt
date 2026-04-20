package com.example.jetpackapploginmvvm.model.api

import retrofit2.http.GET

interface ApiService {
    // La URL de GitHub Raw.
    // L'arrel (base) la posarem després al RetrofitClient
    @GET("lobandolerpi/api-simon/refs/heads/main/ranking-mundial.json")
    suspend fun getRankingMundial(): List<RemoteUser>
}