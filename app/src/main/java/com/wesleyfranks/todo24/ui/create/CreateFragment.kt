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
import com.wesleyfranks.todo24.databinding.FragmentCreateBinding
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
        createAdapter = TodoAdapter(this,this, this)
        binding.createRv.apply {
            adapter = createAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
        createViewModel.getAllTodos(binding.root.context).observe(viewLifecycleOwner, Observer {
            createAdapter.submitList(it)
        })
        binding.createFab.setOnClickListener {
            fabClicked(binding.root)
        }

    }

    private fun fabClicked(view: View, todo: Todo? = null){
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
            if (todo != null) {
                et.setText(todo.title)
            }
            button.setOnClickListener {
                val createdTodo = Todo(title = et.editableText.toString().trim(),timestamp = GetTimestamp().getCurrentTime())
                createViewModel.insertTodo(view.context, createdTodo)
                this.dismiss()
                Snackbar.make(view, "Todo Added...", Snackbar.LENGTH_SHORT).setAction(
                        "Undo",
                        View.OnClickListener {
                            createViewModel.deleteTodo(binding.root.context, createdTodo)
                            fabClicked(view, createdTodo)
                        }
                ).show()
            }
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
        createViewModel.updateTodo(requireContext(),updatedTodo)
    }

    override fun OnItemClicked(todo: Todo) {
        Log.e(TAG, "OnItemClicked: todo -> $todo")
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
                val todoTimestamp = GetTimestamp().getCurrentTime()
                val tempTodo = todo.copy(title = todoTitle,timestamp = todoTimestamp)
                Log.d(TAG, "fabClicked: SAVE BUTTON CLICKED -> $tempTodo")
                createViewModel.updateTodo(view.context, tempTodo)
                this.dismiss()
                Snackbar.make(view, "Todo viewed/edited...", Snackbar.LENGTH_SHORT).setAction(
                        "Undo",
                        View.OnClickListener {
                            createViewModel.deleteTodo(view.context, tempTodo)
                            fabClicked(view, tempTodo)
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



}