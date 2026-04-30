package com.example.jetpackapploginmvvm.model

class UserRepository(private val userDao: UserDao) {
    
    suspend fun addUser(user: User): Boolean {
        // El método insert con OnConflictStrategy.IGNORE devuelve -1 si ya existe el registro.
        val id = userDao.insert(user)
        return id != -1L
    }

    suspend fun getUser(username: String): User? {
        return userDao.getUser(username)
    }
}
