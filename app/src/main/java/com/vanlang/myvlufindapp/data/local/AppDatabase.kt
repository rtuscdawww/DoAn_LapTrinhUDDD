package com.vanlang.myvlufindapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.vanlang.myvlufindapp.data.model.Post

@Database(entities = [Post::class], version = 2, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun postDao(): PostDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vlufind.db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}