package com.example.jetpackapploginmvvm.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUser(username: String): User?

    @Query("SELECT * FROM users ORDER BY highScore DESC LIMIT 5")
    suspend fun getTop5Users(): List<User>
}
