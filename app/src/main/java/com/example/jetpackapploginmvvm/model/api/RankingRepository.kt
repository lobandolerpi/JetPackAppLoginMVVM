package com.example.jetpackapploginmvvm.model.api

import com.example.jetpackapploginmvvm.model.UserDao
import com.example.jetpackapploginmvvm.model.api.RemoteUser
import com.example.jetpackapploginmvvm.model.api.RetrofitClient

object RankingRepository {

    // Retorna un "Pair": El primer valor és la llista,
    // el segon és el missatge d'error (null si tot va bé)
    suspend fun getRanking(dao: UserDao):
            Pair<List<RemoteUser>, String?> {
        return try {
            // 1. INTENT INTERNET: Truquem proveïdor extern (Retrofit)
            val dadesWeb = RetrofitClient.apiService.getRankingMundial()
            // Si arriba aquí, Internet ha funcionat!
            Pair(first = dadesWeb, second = null)
        } catch (e: Exception) {
            // 2. ERROR DE XARXA: Anem a Local (Room)
            val dadesLocals = dao.getTop5Users().map {
                usuariLocal ->
                // Hem de "disfressar" l'usuari de Room
                // perquè sembli un de Web per la UI
                RemoteUser(
                    username = usuariLocal.username,
                    highScore = usuariLocal.highScore
                )
            }
            // Retornem les dades locals i un text explicant l'error
            Pair(
                first = dadesLocals,
                second = "No hi ha connexió. Puntuacions locals."
            )
        }
    }
}
