package com.example.jetpackapploginmvvm.model

// Object ens diu que aquesta classe és un singleton, directament!
// es crearà quan la intenti fer servir desde els botons onXClick
object UserRepository {
    private val users = mutableMapOf<String, User>()

    fun addUser(user: User): Boolean {
        if (users.containsKey(user.username)) {
            return false
        } else {
            users[user.username] = user
            return true
        }
    }

    fun getUser(username: String): User? {
        return users[username]
    }
}