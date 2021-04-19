package com.wesleyfranks.todo24.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    companion object{
        private const val TAG:String = "TodoDao"
    }

    @Query("SELECT * FROM todo_table WHERE completed = 0 ORDER by timestamp DESC")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todo_table WHERE completed = 1 ORDER by timestamp DESC")
    fun getAllCompletedTodos(): Flow<List<Todo>>

    @Insert
    fun insert(todo: Todo)

    @Delete
    fun deleteTodo(todo: Todo)

    @Query("DELETE FROM todo_table")
    fun deleteAllTodos()

    @Update
    fun updateTodo(todo: Todo)

}