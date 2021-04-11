package com.wesleyfranks.todo24.ui.create

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.snackbar.Snackbar
import com.wesleyfranks.todo24.R
import com.wesleyfranks.todo24.data.Todo
import com.wesleyfranks.todo24.data.TodoAdapter
import com.wesleyfranks.todo24.data.TodoRepository
import com.wesleyfranks.todo24.databinding.FragmentCreateBinding
import com.wesleyfranks.todo24.util.ConstantVar
import com.wesleyfranks.todo24.util.GetTimestamp

class CreateFragment : Fragment(),
        TodoAdapter.ClickedTodo,
        TodoAdapter.CompletedChecked,
        TodoAdapter.DeleteTodo {

    companion object{
        private const val TAG:String = "CreateFragment"
    }

    private lateinit var createViewModel: CreateViewModel
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var createView: View
    private lateinit var adapter:TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateBinding.inflate(layoutInflater)
        createView = binding.root
        return createView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createViewModel = ViewModelProvider(this).get(CreateViewModel::class.java)
        adapter = TodoAdapter(this,this, this)
        createViewModel.getAllTodos(binding.root.context).observe(viewLifecycleOwner, Observer {
            binding.createRv.adapter = adapter.apply {
                this.submitList(it)
            }
        })
        binding.createRv.layoutManager = LinearLayoutManager(requireContext())

        binding.createFab.setOnClickListener {
            createViewModel.fabClicked(binding.root)
        }

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
        createViewModel.deleteAllTodos(binding.root.context)
        Snackbar.make(binding.root,"All toods have been deleted...",Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun OnRadioButtonChecked(todo: Todo) {
        val updatedTodo = todo.copy(completed = !todo.completed)
        if (updatedTodo.completed){
            // need to delete item from room database
            val repo = TodoRepository()
            repo.updateTodo(binding.root.context, updatedTodo)
            Snackbar.make(binding.root,"Completed, \"" +
                    todo.title.take(ConstantVar().charlim) + "...\""
                ,Snackbar.LENGTH_LONG).setAction("Undo")
            {
                repo.updateTodo(binding.root.context, todo)
                Snackbar.make(binding.root,"Todo has been updated...",Snackbar.LENGTH_SHORT).show()
            }.show()
        }
    }

    override fun OnItemClicked(todo: Todo) {
        view?.let { MaterialDialog(it.context, BottomSheet(LayoutMode.WRAP_CONTENT)) }?.show {
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
            et.setText(todo.title)
            button.setOnClickListener {
                val todoTitle = et.editableText.toString().trim()
                val todoTimestamp = GetTimestamp().getTimeOnDevice()
                val updatedTodo = todo.copy(title = todoTitle, timestamp = todoTimestamp)
                Log.d(TAG, "fabClicked: SAVE BUTTON CLICKED -> $todo")
                createViewModel.updateTodo(view.context, updatedTodo)
                this.dismiss()
                Snackbar.make(view, "Todo viewed/edited...", Snackbar.LENGTH_SHORT).setAction(
                        "Undo",
                        View.OnClickListener {
                            createViewModel.deleteTodo(view.context, todo)
                            createViewModel.fabClicked(view, todo)
                        }
                ).show()
            }
        }
    }

    override fun OnItemDelete(todo: Todo) {
        val materialDialog = MaterialDialog(binding.root.context)
        materialDialog.show {
            cornerRadius(16f)
            title(null,"Delete")
            message(R.string.todo_dialog_delete_message)
            positiveButton {
                createViewModel.deleteTodo(binding.root.context,todo)
                Snackbar.make(binding.root, "Deleted Todo...", Snackbar.LENGTH_SHORT).setAction(
                        "Undo",
                        View.OnClickListener {
                            createViewModel.insertTodo(binding.root.context, todo)
                        }
                ).show()
            }
            negativeButton {
                it.dismiss()
            }
        }

    }

}