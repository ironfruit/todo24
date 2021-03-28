package com.wesleyfranks.todo24.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.*

@Dao
interface TodoDao {

    @Query("SELECT * FROM todo_database")
    fun getAllTodos(): LiveData<List<Todo>>

    @Insert
    fun insert(todo: Todo)

    @Delete
    fun deleteTodo(todo: Todo)

    @Query("DELETE FROM todo_database")
    fun deleteAllTodos()

    @Update
    fun updateTodo(todo: Todo)

}