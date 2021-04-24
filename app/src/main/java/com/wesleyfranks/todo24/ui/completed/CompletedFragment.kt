package com.wesleyfranks.todo24.ui.completed

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.wesleyfranks.todo24.R
import com.wesleyfranks.todo24.data.Todo
import com.wesleyfranks.todo24.data.TodoAdapter
import com.wesleyfranks.todo24.databinding.FragmentCompletedBinding
import com.wesleyfranks.todo24.ui.create.CreateFragmentDirections

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
    private lateinit var completeAdapter: TodoAdapter

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
        completeAdapter = TodoAdapter(this,this, this)
        binding.completedRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = completeAdapter
            setHasFixedSize(true)
        }
        completedViewModel.getAllCompletedTodos(requireContext()).observe(viewLifecycleOwner,
            Observer {
                binding.completedRv.adapter = completeAdapter.apply {
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
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.create_options, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.todo_settings -> {
                val action = CompletedFragmentDirections.actionNavigationCompletedToSettingsFragment()
                findNavController().navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun OnItemClicked(todo: Todo) {
        // edit/view
        Log.d(TAG, "OnItemClicked: todo -> $todo")
    }

    override fun OnItemDelete(todo: Todo) {
        val materialDialog = MaterialDialog(binding.root.context)
        materialDialog.show {
            cornerRadius(16f)
            title(R.string.delete)
            message(R.string.todo_dialog_delete_message)
            positiveButton(android.R.string.yes) {
                completedViewModel.deleteTodo(binding.root.context,todo)
                Snackbar.make(binding.root, "Deleted Todo.", Snackbar.LENGTH_SHORT).setAction(
                    "Undo",
                    View.OnClickListener {
                        completedViewModel.insertTodo(binding.root.context, todo)
                    }
                ).show()
            }
            negativeButton(android.R.string.no) {
                it.dismiss()
            }
            lifecycleOwner(this@CompletedFragment)

        }
    }

    override fun OnRadioButtonChecked(todo: Todo) {
        todo.completed = !todo.completed
        completedViewModel.updateTodo(binding.root.context, todo)
        Snackbar.make(binding.root, "Todo Updated.", Snackbar.LENGTH_LONG).setAction(
                "Undo",
                View.OnClickListener {
                    todo.completed = !todo.completed
                    completedViewModel.updateTodo(binding.root.context, todo)
                }
        ).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}