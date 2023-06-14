package com.dmm.rssreader.presentation.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.dmm.rssreader.R
import com.dmm.rssreader.presentation.viewModel.MainViewModel

abstract class BaseFragment<VB : ViewBinding>(
	private val bindingInflater: (inflater: LayoutInflater) -> VB
) : Fragment() {

	private lateinit var _binding: VB
	protected val binding: VB get() = _binding
	protected lateinit var viewModel: MainViewModel

	protected open fun onViewCreated() = Unit
	protected open fun onCreateView() = Unit

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = bindingInflater.invoke(inflater)
		onCreateView()

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
		onViewCreated()
	}
}