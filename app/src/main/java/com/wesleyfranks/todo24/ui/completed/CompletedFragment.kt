package com.wesleyfranks.todo24.ui.completed

import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.wesleyfranks.todo24.R
import com.wesleyfranks.todo24.databinding.FragmentCompletedBinding

class CompletedFragment : Fragment() {

    private lateinit var completedViewModel: CompletedViewModel
    private lateinit var binding: FragmentCompletedBinding
    private lateinit var completeView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        completedViewModel =
                ViewModelProvider(this).get(CompletedViewModel::class.java)
        binding = FragmentCompletedBinding.inflate(layoutInflater)
        completeView = binding.root
        return completeView
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

}