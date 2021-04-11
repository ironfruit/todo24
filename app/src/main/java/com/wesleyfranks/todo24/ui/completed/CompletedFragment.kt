package com.wesleyfranks.todo24.ui.completed

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.google.android.material.snackbar.Snackbar
import com.wesleyfranks.todo24.R
import com.wesleyfranks.todo24.data.Todo
import com.wesleyfranks.todo24.data.TodoAdapter
import com.wesleyfranks.todo24.data.TodoRepository
import com.wesleyfranks.todo24.databinding.FragmentCompletedBinding
import com.wesleyfranks.todo24.databinding.FragmentCreateBinding
import com.wesleyfranks.todo24.util.ConstantVar
import java.util.stream.Collectors

class CompletedFragment : Fragment(),
        TodoAdapter.CompletedChecked,
        TodoAdapter.ClickedTodo,
        TodoAdapter.DeleteTodo {

    companion object{
        private const val TAG:String = "CompletedFragment"
    }

    private lateinit var completedViewModel: CompletedViewModel
    private var _binding: FragmentCompletedBinding? = null
    private val binding get() = _binding!!
    private lateinit var completeView: View
    private lateinit var adapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCompletedBinding.inflate(layoutInflater)
        completeView = binding.root
        return completeView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        completedViewModel = ViewModelProvider(this).get(CompletedViewModel::class.java)
        adapter = TodoAdapter(this,this, this)
        completedViewModel.getAllCompletedTodos(requireContext()).observe(viewLifecycleOwner,
            Observer {
                binding.completedRv.adapter = adapter.apply {
                    this.submitList(it)
                }
            })
        completedViewModel.todosList.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "onViewCreated: TODOLIST OBSERVE")
            if (it.isEmpty()) {
                completedViewModel.status.postValue(getString(R.string.completed_text_listempty))
            } else {
                completedViewModel.status.postValue("")
            }
        })
        completedViewModel.status.observe(viewLifecycleOwner, Observer {
            binding.textCompleted.text = it
        })
        binding.completedRv.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.create_options_deleteAllTodos -> {
                DeleteAllTodos()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun DeleteAllTodos() {
        completedViewModel.deleteAllTodos(binding.root.context)
        Snackbar.make(binding.root,"All toods have been deleted...", Snackbar.LENGTH_LONG).show()
    }

    override fun OnItemClicked(editViewTodo: Todo) {
        // edit/view
    }

    override fun OnItemDelete(todo: Todo) {
        val materialDialog = MaterialDialog(binding.root.context)
        materialDialog.show {
            cornerRadius(16f)
            title(null,"Delete")
            message(R.string.todo_dialog_delete_message)
            positiveButton {
                completedViewModel.deleteTodo(binding.root.context,todo)
                Snackbar.make(binding.root, "Deleted Todo...", Snackbar.LENGTH_SHORT).setAction(
                    "Undo",
                    View.OnClickListener {
                        // completedViewModel.status.postValue("")
                        completedViewModel.insertTodo(binding.root.context, todo)
                    }
                ).show()
            }
            negativeButton {
                it.dismiss()
            }
        }
    }

    override fun OnRadioButtonChecked(todo: Todo) {
        val updatedTodo = todo.copy(completed = !todo.completed)
        if (!todo.completed){
            // need to delete item from room database
            val repo = TodoRepository()
            repo.updateTodo(binding.root.context, updatedTodo)
            Snackbar.make(binding.root,"Todo has been uncompleted, \"" +
                    todo.title.take(ConstantVar().charlim) + "...\""
                ,Snackbar.LENGTH_LONG).setAction("Undo")
            {
                repo.updateTodo(binding.root.context, todo)
                Snackbar.make(binding.root,"Todo has been updated...",Snackbar.LENGTH_SHORT).show()
            }.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}