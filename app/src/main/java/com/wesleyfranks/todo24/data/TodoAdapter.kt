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
import com.wesleyfranks.todo24.R
import com.wesleyfranks.todo24.databinding.TodoListItemBinding
import com.wesleyfranks.todo24.util.ConstantVar
import com.wesleyfranks.todo24.util.GetTimestamp

class TodoAdapter(
    private val completedChecked: CompletedChecked? = null,
    private val clickedTodo: ClickedTodo? = null,
    private val deleteTodo: DeleteTodo? = null) :
    ListAdapter<Todo, TodoAdapter.ViewHolder>(TodoDiffUtil()) {

    companion object{
        private const val TAG = "TodoAdapter"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemBinding = TodoListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemBinding, completedChecked!!, clickedTodo!!, deleteTodo!!)
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

            itemBinding.todoItemTitle.text = todo.title
            itemBinding.todoItemTimestamp.text = GetTimestamp().formateNonMilDateTime(todo.timestamp)

            setCompletedCheck(todo)

            itemBinding.root.setOnClickListener {
                clickedTodo.OnItemClicked(todo)
            }

            itemBinding.todoItemCheck.setOnClickListener {
                completedChecked.OnRadioButtonChecked(todo)
                itemBinding.todoItemCheck.isChecked = todo.completed
                Log.d(TAG, "bind: todoItemCheck isChecked -> ${itemBinding.todoItemCheck.isChecked}")
            }

            itemBinding.todoItemDelete.setOnClickListener {
                deleteTodo.OnItemDelete(todo)
            }

        }

        private fun setCompletedCheck(todo: Todo) {
            if (todo.completed) {
                itemBinding.todoItemCheck.isChecked = true
                return
            } else {
                itemBinding.todoItemCheck.isChecked = false
                return
            }
        }
    }

    override fun submitList(list: List<Todo>?) {
        super.submitList(list?.let { ArrayList(it) })
    }

    class TodoDiffUtil : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            Log.d(TAG, "areItemsTheSame: oldItem $oldItem -- newItem $newItem")
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            Log.d(TAG, "areContentsTheSame: oldItem.Title ${oldItem.title} newItem.Title ${newItem.title}")
            return oldItem.title == newItem.title
        }

    }


    interface CompletedChecked{
        fun OnRadioButtonChecked(todo: Todo)
    }

    interface ClickedTodo{
        fun OnItemClicked(todo: Todo)
    }

    interface DeleteTodo{
        fun OnItemDelete(todo: Todo)
    }
}