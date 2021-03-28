package com.wesleyfranks.todo24.ui.completed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.wesleyfranks.todo24.R
import com.wesleyfranks.todo24.databinding.FragmentCompletedBinding

class CompletedFragment : Fragment() {

    private lateinit var completedViewModel: CompletedViewModel
    private lateinit var binding: FragmentCompletedBinding
    private lateinit var completeView: View

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

}