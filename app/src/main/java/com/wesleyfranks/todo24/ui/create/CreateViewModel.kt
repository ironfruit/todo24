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
import com.wesleyfranks.todo24.data.TodoDao
import com.wesleyfranks.todo24.data.TodoRepository
import com.wesleyfranks.todo24.util.GetTimestamp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CreateViewModel : ViewModel() {

    companion object{
        private const val TAG:String = "CreateViewModel"
    }

    lateinit var todosList: LiveData<List<Todo>>
    lateinit var createdTodo: Todo

    // insert a todo

    fun insertTodo(context: Context, todo: Todo){
        TodoRepository().insertTodo(context, todo)
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

    fun updateTodo(context: Context,todo: Todo){
        TodoRepository().updateTodo(context,todo)
    }

    // get all Todos

    fun getAllTodos(context: Context): LiveData<List<Todo>>{
        viewModelScope.launch {
            todosList = TodoRepository().getAllTodos(context).asLiveData()
        }
        return todosList
}

    fun fabClicked(view: View, editTodo: Todo? = null){
        Log.d(TAG, "onViewCreated: I clicked FAB")
        val md = MaterialDialog(view.context, BottomSheet(LayoutMode.WRAP_CONTENT))
        md.show {
            cornerRadius(16f)
            title(null, "Create Todo")
            customView(
                viewRes = R.layout.create_todo_bsd,
                null,
                false,
                false,
                false,
                true
            )


            val et = getCustomView().findViewById<EditText>(R.id.create_todo_bsd_et)
            val button = getCustomView().findViewById<Button>(R.id.create_todo_bsd_savebutton)
            et.requestFocus()
            if (editTodo != null){
                et.setText(editTodo.title)
            }
            button.setOnClickListener {
                val todoTitle = et.editableText.toString().trim()
                val todoTimestamp = GetTimestamp().getTimeOnDevice()
                createdTodo = Todo(todoTitle,todoTimestamp)
                Log.d(TAG, "fabClicked: SAVE BUTTON CLICKED -> $createdTodo")
                insertTodo(view.context, createdTodo)
                this.dismiss()
                Snackbar.make(view, "Todo Added...", Snackbar.LENGTH_SHORT).setAction(
                    "Undo",
                    View.OnClickListener {
                        deleteTodo(view.context, createdTodo)
                        fabClicked(view, createdTodo)
                    }
                ).show()
            }
        }
    }

}
