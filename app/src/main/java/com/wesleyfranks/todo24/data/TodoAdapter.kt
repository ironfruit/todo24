package com.wesleyfranks.todo24.data

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.wesleyfranks.todo24.databinding.TodoListItemBinding
import com.wesleyfranks.todo24.util.ConstantVar

class TodoAdapter(private val completedChecked: CompletedChecked, private val clickedTodo: ClickedTodo, private val deleteTodo: DeleteTodo) :
    ListAdapter<Todo, TodoAdapter.ViewHolder>(TodoDiffUtil()) {

    companion object{
        private const val TAG = "TodoAdapter"
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = TodoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding, completedChecked, clickedTodo, deleteTodo)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val todo = getItem(position)
        holder.bind(todo)
    }

    class ViewHolder(
            private val itemBinding: TodoListItemBinding,
            private val completedChecked: CompletedChecked,
            private val clickedTodo: ClickedTodo,
            private val deleteTodo: DeleteTodo)
        : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(todo: Todo) {

            itemBinding.root.setOnClickListener {
                Snackbar.make(it,"Clicked ${todo.pk}",Snackbar.LENGTH_SHORT).show()
            }

            itemBinding.todoItemCheck.setOnClickListener {
                if (itemBinding.todoItemCheck.isChecked){
                    completedChecked.OnRadioButtonChecked(todo, adapterPosition)
                }
            }

            itemBinding.todoItemDelete.setOnClickListener {
                val materialDialog = MaterialDialog(itemBinding.root.context)
                materialDialog.show {
                    cornerRadius(5f)
                    title(null,"Delete")
                    message {  }
                }
            }

            itemBinding.todoItemTitle.text = todo.title
            itemBinding.todoItemTimestamp.text = todo.timestamp
        }
    }

    class TodoDiffUtil : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.title == newItem.title
        }

    }


    interface CompletedChecked{
        fun OnRadioButtonChecked(todo: Todo, pos: Int)
    }

    interface ClickedTodo{
        fun OnItemClicked(todo: Todo, pos: Int)
    }

    interface DeleteTodo{
        fun OnItemDelete(todo: Todo, pos: Int)
    }
}