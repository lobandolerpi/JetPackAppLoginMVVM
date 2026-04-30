package com.example.jetpackapploginmvvm.model.api

import retrofit2.http.GET

interface ApiService {
    // IMPORTANTE: Pega aquí la parte final de tu URL "Raw" de GitHub
    // (todo lo que va después de githubusercontent.com/)
    @GET("jguevaraag/ranking-mundial.json/refs/heads/main/ranking-mundial.json")
    suspend fun getRankingMundial(): List<RemoteUser>
}