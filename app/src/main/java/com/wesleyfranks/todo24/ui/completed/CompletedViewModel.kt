package com.wesleyfranks.todo24.ui.completed

import android.content.Context
import androidx.lifecycle.*
import com.wesleyfranks.todo24.data.Todo
import com.wesleyfranks.todo24.data.TodoRepository
import kotlinx.coroutines.launch

class CompletedViewModel : ViewModel() {

    companion object{
        private const val TAG:String = "CompletedViewModel"
    }

    lateinit var todosList: LiveData<List<Todo>>
    lateinit var completedTodo: Todo
    val status: MutableLiveData<String> = MutableLiveData()

    init {
        status.value = ""
    }

    // update a todo

    fun insertTodo(context: Context, todo: Todo){
        TodoRepository().insertTodo(context,todo)
    }

    // delete a todo

    fun deleteTodo(context: Context, todo: Todo){
        TodoRepository().deleteTodo(context, todo)
    }

    // delete all todos

    fun deleteAllTodos(context: Context){
        TodoRepository().deleteAllTodos(context)
    }

    // update a todo

    fun updateTodo(context: Context, todo: Todo){
        TodoRepository().updateTodo(context,todo)
    }

    // get all Todos

    fun getAllCompletedTodos(context: Context): LiveData<List<Todo>>{
        viewModelScope.launch {
            todosList = TodoRepository().getAllCompletedTodos(context).asLiveData()
        }
        return todosList
    }

}