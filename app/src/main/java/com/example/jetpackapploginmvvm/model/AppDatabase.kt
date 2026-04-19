package com.example.jetpackapploginmvvm.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


// Definim quines entitats (taules) té i quina és la versió.
// (Si afegim taules al futur, la versió passarà a 2).
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {


    // Accessor pel nostre DAO
    abstract fun userDao(): UserDao


    // Singleton clàssic thread-safe
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null


        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "simon_database"
                    // El nom de l'arxiu físic al mòbil
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

