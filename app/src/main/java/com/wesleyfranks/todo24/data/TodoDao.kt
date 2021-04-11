package com.wesleyfranks.todo24.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_table WHERE completed = 0")
    fun getAllTodos(): Flow<List<Todo>>

    @Query("SELECT * FROM todo_table WHERE completed = 1")
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