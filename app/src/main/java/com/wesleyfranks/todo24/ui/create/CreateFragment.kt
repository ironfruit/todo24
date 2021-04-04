package com.wesleyfranks.todo24.ui.create

import android.graphics.Canvas
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.*
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.callbacks.onDismiss
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.wesleyfranks.todo24.R
import com.wesleyfranks.todo24.data.Todo
import com.wesleyfranks.todo24.data.TodoAdapter
import com.wesleyfranks.todo24.data.TodoRepository
import com.wesleyfranks.todo24.databinding.FragmentCreateBinding
import com.wesleyfranks.todo24.util.ConstantVar
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import java.util.*

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
    lateinit var materialDialog: MaterialDialog

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

    override fun OnRadioButtonChecked(todo: Todo, pos: Int) {
        // need to remove the item at position checked
        Log.d(TAG, "bind: adapter position -> $pos")
        val completedTodo = adapter.currentList[pos]
        completedTodo.completed = true
        adapter.currentList.remove(completedTodo)
        adapter.submitList(adapter.currentList)
        // need to delete item from room database
        val repo = TodoRepository()
        repo.updateTodo(binding.root.context, completedTodo)
        Snackbar.make(binding.root,"Completed, " +
                todo.title.take(ConstantVar().charlim)
                ,Snackbar.LENGTH_LONG).setAction("Undo")
        {
            completedTodo.completed = false
            adapter.currentList.add(completedTodo)
            adapter.submitList(adapter.currentList)
            repo.updateTodo(binding.root.context, completedTodo)

        }.show()
    }

    override fun OnItemClicked(todo: Todo, pos: Int) {
        TODO("Not yet implemented")
    }

    override fun OnItemDelete(todo: Todo, pos: Int) {
        TODO("Not yet implemented")
    }

}