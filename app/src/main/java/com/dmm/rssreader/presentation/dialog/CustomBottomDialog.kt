package com.dmm.rssreader.presentation.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.dmm.rssreader.presentation.viewModel.MainViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class CustomBottomDialog<VB : ViewBinding>(
	private val bindingInflater: (inflater: LayoutInflater) -> VB
) : BottomSheetDialogFragment() {

	private lateinit var _binding: VB
	protected val binding: VB get() = _binding
	protected lateinit var viewModel: MainViewModel
	private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
	private lateinit var dialog: BottomSheetDialog

	protected open fun onViewCreated() = Unit

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
		//Disabled dragging
		dialog.setOnShowListener {
			val bottomSheet = dialog
				.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)

			if (bottomSheet != null) {
				val behavior: BottomSheetBehavior<*> = BottomSheetBehavior.from(bottomSheet)
				behavior.isDraggable = false
			}
		}
		return dialog
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		_binding = bindingInflater.invoke(inflater)

		return binding.root
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
		bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
		bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
		onViewCreated()
	}
}