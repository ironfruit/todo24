package com.wesleyfranks.todo24.ui.create

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.lifecycle.*
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.snackbar.Snackbar
import com.wesleyfranks.todo24.R
import com.wesleyfranks.todo24.data.Todo
import com.wesleyfranks.todo24.data.TodoRepository
import com.wesleyfranks.todo24.util.GetTimestamp
import kotlinx.coroutines.launch

class CreateViewModel : ViewModel() {

    companion object{
        private const val TAG:String = "CreateViewModel"
    }

    lateinit var todosList: LiveData<List<Todo>>
    lateinit var createdTodo: Todo
    val status: MutableLiveData<String> = MutableLiveData()

    init {
        status.value = ""
    }

    // insert a todo

    fun insertTodo(context: Context, todo: Todo){
        TodoRepository().insertTodo(context, todo)
    }

    // delete a todo

    fun deleteTodo(context: Context, todo: Todo){
        Log.d(TAG, "deleteTodo: ${todo.title}")
        TodoRepository().deleteTodo(context, todo)
    }

    // delete all todos

    fun deleteAllTodos(context: Context){
        TodoRepository().deleteAllTodos(context)
    }

    // update a todo

    fun updateTodo(context: Context,todo: Todo){
        Log.d(TAG, "updateTodo: todo -> $todo")
        TodoRepository().updateTodo(context,todo)
    }

    // get all Todos

    fun getAllTodos(context: Context): LiveData<List<Todo>>{
        viewModelScope.launch {
            todosList = TodoRepository().getAllTodos(context).asLiveData()
        }
        return todosList
    }

}
