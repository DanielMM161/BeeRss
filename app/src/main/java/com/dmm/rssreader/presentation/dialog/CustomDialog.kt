package com.dmm.rssreader.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.dmm.rssreader.R
import com.dmm.rssreader.presentation.viewModel.MainViewModel

abstract class CustomDialog<VB : ViewBinding>(
	private val bindingInflater: (inflater: LayoutInflater) -> VB,
	private val layoutResource: Int,
	private val themeResource: Int? = null
) : DialogFragment() {

	private lateinit var _binding: VB
	protected val binding: VB get() = _binding
	protected lateinit var viewModel: MainViewModel
	protected open fun onViewCreated() = Unit

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
	}

	override fun getTheme(): Int {
		if (themeResource == null) {
			return super.getTheme()
		}
		return themeResource
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = bindingInflater.invoke(inflater)
		return  binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
		onViewCreated()
	}

}
