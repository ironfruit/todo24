package com.wesleyfranks.todo24.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = [Todo::class], version = 4, exportSchema = false)
abstract class TodoDatabase : RoomDatabase(){

    abstract fun TodoDao(): TodoDao

    companion object{
        private var instance: TodoDatabase? = null
        @Synchronized
        open fun getInstance(context: Context): TodoDatabase? {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    TodoDatabase::class.java, "todo_database")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }
    }


}

