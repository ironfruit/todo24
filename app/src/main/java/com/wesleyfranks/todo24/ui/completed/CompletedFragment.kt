package com.wesleyfranks.todo24.ui.completed

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.wesleyfranks.todo24.R
import com.wesleyfranks.todo24.data.Todo
import com.wesleyfranks.todo24.data.TodoAdapter
import com.wesleyfranks.todo24.data.TodoRepository
import com.wesleyfranks.todo24.databinding.FragmentCompletedBinding
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
    private lateinit var binding: FragmentCompletedBinding
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
        binding = FragmentCompletedBinding.inflate(layoutInflater)
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
                    if (this.currentList.isEmpty()){
                        binding.textCompleted.text = getString(R.string.completed_text_listempty)
                    }
                }
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

    override fun OnItemClicked(editViewTodo: Todo, pos: Int) {
        TODO("Not yet implemented")
    }

    override fun OnItemDelete(todo: Todo, pos: Int) {
        TODO("Not yet implemented")
    }

    override fun OnRadioButtonChecked(todo: Todo, pos: Int) {
        Log.d(TAG, "bind: adapter position -> $pos")
        val updatedList = adapter.currentList.stream().collect(Collectors.toList())
        val updatedTodo = updatedList[pos]
        updatedTodo.completed = false
        if (!todo.completed){
            // need to remove the item at position checked
            updatedList.remove(updatedTodo)
            adapter.submitList(updatedList)
            // need to delete item from room database
            val repo = TodoRepository()
            repo.updateTodo(binding.root.context, updatedTodo)
            Snackbar.make(binding.root,"Todo has been uncompleted, \"" +
                    todo.title.take(ConstantVar().charlim) + "...\""
                ,Snackbar.LENGTH_LONG).setAction("Undo")
            {
                updatedTodo.completed = true
                repo.updateTodo(binding.root.context, updatedTodo)
                Snackbar.make(binding.root,"Todo has been updated...",Snackbar.LENGTH_SHORT).show()
            }.show()
        }
    }

}