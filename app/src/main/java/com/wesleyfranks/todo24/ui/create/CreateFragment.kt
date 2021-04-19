package com.wesleyfranks.todo24.ui.create

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
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
import com.afollestad.materialdialogs.lifecycle.lifecycleOwner
import com.google.android.material.snackbar.Snackbar
import com.wesleyfranks.todo24.R
import com.wesleyfranks.todo24.data.Todo
import com.wesleyfranks.todo24.data.TodoAdapter
import com.wesleyfranks.todo24.databinding.FragmentCreateBinding
import com.wesleyfranks.todo24.ui.completed.CompletedFragment
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
    private lateinit var createAdapter:TodoAdapter

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
        createAdapter = TodoAdapter(this,this, this)
        binding.createRv.apply {
            adapter = createAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        createViewModel.getAllTodos(requireContext()).observe(viewLifecycleOwner, Observer {
            createAdapter.submitList(it)
        })
        binding.createFab.setOnClickListener {
            fabClicked(binding.root)
        }
        createViewModel.todosList.observe(viewLifecycleOwner, Observer {
            Log.d(TAG, "onViewCreated: TODOLIST OBSERVE")
            if (it.isEmpty()) {
                createViewModel.status.postValue(getString(R.string.create_text_listempty))
            } else {
                createViewModel.status.postValue("")
            }
        })
        createViewModel.status.observe(viewLifecycleOwner, Observer {
            binding.textCompleted.text = it
        })
        setHasOptionsMenu(true)
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
        todo.completed = !todo.completed
        createViewModel.updateTodo(binding.root.context, todo)
    }

    override fun OnItemClicked(todo: Todo) {
        Log.e(TAG, "OnItemClicked: todo -> $todo")
        val md = MaterialDialog(binding.root.context, BottomSheet(LayoutMode.WRAP_CONTENT))
        md.show {
            cornerRadius(16f)
            title(null, "Create Todo")
            message(null, "Type todo and then tap save.")
            customView(viewRes = R.layout.create_todo_bsd)
            val et = getCustomView().findViewById<EditText>(R.id.create_todo_bsd_et)
            et.requestFocus()
            et.setText(todo.title)
            et.setSelection(et.text.length)
            positiveButton(R.string.todo_bsd_save) {
                val todoTitle = et.editableText.toString().trim()
                todo.title = todoTitle
                Log.d(TAG, "fabClicked: SAVE BUTTON CLICKED -> $todo")
                createViewModel.updateTodo(binding.root.context, todo)
                Snackbar.make(binding.root, "Todo viewed/edited...", Snackbar.LENGTH_LONG).setAction(
                        "Undo",
                        View.OnClickListener {
                            createViewModel.deleteTodo(binding.root.context, todo)
                            OnItemClicked(todo)
                        }
                ).show()
            }
            negativeButton(android.R.string.cancel){
                this.dismiss()
            }
            lifecycleOwner(this@CreateFragment)
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
                Snackbar.make(binding.root, "Deleted Todo.", Snackbar.LENGTH_SHORT).setAction(
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

    fun fabClicked(view: View, todo: Todo? = null){
        Log.d(TAG, "onViewCreated: I clicked FAB")
        val md = MaterialDialog(view.context, BottomSheet(LayoutMode.WRAP_CONTENT))
        md.show {
            cornerRadius(16f)
            title(null, "Create Todo")
            message(null, "Type todo and then tap save.")
            customView(viewRes = R.layout.create_todo_bsd)
            val et = getCustomView().findViewById<EditText>(R.id.create_todo_bsd_et)
            et.requestFocus()
            if (todo != null) {
                et.setText(todo.title)
                et.setSelection(et.text.length)
            }
            positiveButton(R.string.todo_bsd_save) {
                val todoTitle = et.editableText.toString().trim()
                val todoTimestamp = GetTimestamp().getCurrentTime()
                if(todo != null){
                    if (todo.title == todoTitle){
                        Snackbar.make(binding.root, "Todo Not Updated.", Snackbar.LENGTH_SHORT).show()
                    }else{
                        todo.title = todoTitle
                        createViewModel.updateTodo(binding.root.context, todo)
                        this.dismiss()
                        Snackbar.make(binding.root, "Todo Updated...", Snackbar.LENGTH_SHORT).setAction(
                                "Undo",
                                View.OnClickListener {
                                    createViewModel.deleteTodo(binding.root.context, todo)
                                    fabClicked(view, todo)
                                }
                        ).show()
                    }
                }else{
                    val newTodo = Todo(title = todoTitle,timestamp = todoTimestamp)
                    createViewModel.insertTodo(view.context, newTodo)
                    this.dismiss()
                    Snackbar.make(binding.root, "Todo Added...", Snackbar.LENGTH_SHORT).setAction(
                            "Undo",
                            View.OnClickListener {
                                createViewModel.deleteTodo(binding.root.context, newTodo)
                                fabClicked(view, newTodo)
                            }
                    ).show()
                }
            }
            negativeButton(android.R.string.cancel)
            lifecycleOwner(this@CreateFragment)
        }
    }



}