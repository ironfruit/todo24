package com.wesleyfranks.todo24.data

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class TodoRepository {

    var todoDatabase: TodoDatabase? = null
    lateinit var todoList: Flow<List<Todo>>

    fun initializeDB(context: Context){
        todoDatabase = TodoDatabase.getInstance(context)
    }

    fun insertTodo(context: Context, todo: Todo){
        initializeDB(context)

        CoroutineScope(IO).launch {
            todoDatabase!!.TodoDao().insert(todo)
        }
    }

    fun updateTodo(context: Context, todo: Todo){
        initializeDB(context)

        CoroutineScope(IO).launch {
            todoDatabase!!.TodoDao().updateTodo(todo)
        }
    }

    fun deleteTodo(context: Context, todo: Todo){
        initializeDB(context)

        CoroutineScope(IO).launch {
            todoDatabase!!.TodoDao().deleteTodo(todo)
        }
    }

    fun deleteAllTodos(context: Context){
        initializeDB(context)

        CoroutineScope(IO).launch {
            todoDatabase!!.clearAllTables()
            todoDatabase!!.TodoDao().deleteAllTodos()
        }
    }

    fun getAllTodos(context: Context) : Flow<List<Todo>>{
        initializeDB(context)
        todoList = todoDatabase!!.TodoDao().getAllTodos()
        return todoList
    }

    fun getAllCompletedTodos(context: Context) : Flow<List<Todo>>{
        initializeDB(context)
        todoList = todoDatabase!!.TodoDao().getAllCompletedTodos()
        return todoList
    }

}

