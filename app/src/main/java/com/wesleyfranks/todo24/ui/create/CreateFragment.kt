package com.wesleyfranks.todo24.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.wesleyfranks.todo24.data.Todo
import com.wesleyfranks.todo24.data.TodoAdapter
import com.wesleyfranks.todo24.databinding.FragmentCreateBinding

class CreateFragment : Fragment() {

    companion object{
        private const val TAG:String = "CreateFragment"
    }

    private lateinit var createViewModel: CreateViewModel
    private var _binding: FragmentCreateBinding? = null
    private val binding get() = _binding!!
    private lateinit var createView: View

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
        val adapter = TodoAdapter()
        createViewModel.getAllTodos(binding.root.context).observe(viewLifecycleOwner, Observer {
            binding.createRv.adapter = adapter.apply {
                this.submitList(it)
            }
        })
        binding.createRv.layoutManager = LinearLayoutManager(requireContext())

        binding.createFab.setOnClickListener {
            createViewModel.fabClicked(binding.root)
        }


        //DeleteAllTodos()
    }

    private fun DeleteAllTodos() {
        createViewModel.deleteAllTodos(binding.root.context)
        Snackbar.make(binding.root,"All toods have been deleted...",Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}