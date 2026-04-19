package com.example.jetpackapploginmvvm.model

// Object ens diu que aquesta classe és un singleton, directament!
// es crearà quan la intenti fer servir desde els botons onXClick
object UserRepository {
    // Totes les funcions són suspend per tal que puguin ser asíncrones.
    // Es a dir pausables (el fil pot dir "ja tornaré a per la resposta").
    // És al viewmodel on direm que vagi a un altre fil amb launch(Dispatchers.IO).

    //Com ara hi haurà persistència només afegeixo si no hi són.
    suspend fun prepararDadesDeProva(dao: UserDao) {
        // Si l'usuari "a" no existeix, el creem per defecte
        if (dao.getUser("a") == null) {
            dao.insert(User("a", "a", 0))
        }
        if (dao.getUser("b") == null) {
            dao.insert(User("b", "b", 0))
        }
        if (dao.getUser("c") == null) {
            dao.insert(User("c", "c", 0))
        }
    }

    // Faig servir la definicio del DAO per addUser
    suspend fun addUser(user: User, dao: UserDao): Boolean {
        val result = dao.insert(user)
        return result != -1L
    }

    // Faig servir la definicio del DAO per getUser
    suspend fun getUser(username: String, dao: UserDao): User? {
        return dao.getUser(username)
    }

    // Faig servir la definicio del DAO per getTop5
    suspend fun getTop5(dao: UserDao): List<User> {
        return dao.getTop5Users()
    }

    // Faig servir la definicio del DAO per actualitzar Highscore
    suspend fun updateHighScore(user: User, newScore: Int, dao: UserDao) {
        if (newScore > user.highScore) {
            val updatedUser = user.copy(highScore = newScore)
            dao.update(updatedUser)
        }
    }
}

