package com.example.jetpackapploginmvvm.model.api

import com.example.jetpackapploginmvvm.model.UserDao

object RankingRepository {
    // Retorna un "Pair": El primero es la lista de usuarios, el segundo es el error (null si va bien)
    suspend fun getRanking(dao: UserDao): Pair<List<RemoteUser>, String?> {
        return try {
            // 1. INTENTO INTERNET: Llamamos a Retrofit
            val dadesWeb = RetrofitClient.apiService.getRankingMundial()
            Pair(first = dadesWeb, second = null)
        } catch (e: Exception) {
            // 2. ERROR DE RED: Vamos a Local (Room)
            val dadesLocals = dao.getTop5Users().map { usuariLocal ->
                // Disfrazamos el usuario de Room para que parezca de Web
                RemoteUser(
                    username = usuariLocal.username,
                    highScore = usuariLocal.highScore
                )
            }
            Pair(
                first = dadesLocals,
                second = "No hay conexión. Mostrando puntuaciones locales."
            )
        }
    }
}
