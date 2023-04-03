package com.dmm.rssreader.presentation.fragments

import QuoteSpanClass
import android.app.Dialog
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.QuoteSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.lifecycle.ViewModelProvider
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.FeedDescriptionDialogBinding
import com.dmm.rssreader.domain.extension.gone
import com.dmm.rssreader.domain.extension.show
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.presentation.viewModel.MainViewModel
import com.dmm.rssreader.utils.ImageGetter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class FeedDescriptionDialog(private val feedSelected: FeedUI) : BottomSheetDialogFragment() {

	private lateinit var binding: FeedDescriptionDialogBinding
	private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
	private lateinit var dialog: BottomSheetDialog
	private lateinit var viewModel: MainViewModel

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
		return inflater.inflate(R.layout.feed_description_dialog, container, false)
	}

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		binding = FeedDescriptionDialogBinding.bind(view)
		viewModel = ViewModelProvider(requireActivity())[MainViewModel::class.java]
		bottomSheetBehavior = BottomSheetBehavior.from(view.parent as View)
		bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
		val layout = binding.bottomSheetLayout
		layout.minimumHeight = Resources.getSystem().displayMetrics.heightPixels

		setUpUI()
		saveFeed()
		closeDialog()
		showButtonUrl()
	}

	private fun setUpUI(){
		setImageResourceImageButton(feedSelected.favourite)
		binding.title.text = feedSelected.title
		feedSelected.description.let {
			if(it != null) {
				displayHtml(it)
			}
		}
	}

	private fun displayHtml(html: String) {
		val imageGetter = ImageGetter(resources, binding.htmlViewer, requireContext())

		val styledText = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_SEPARATOR_LINE_BREAK_LIST, imageGetter, null)

		replaceQuoteSpans(styledText as Spannable)

		binding.htmlViewer.text = styledText

		binding.htmlViewer.movementMethod = LinkMovementMethod.getInstance()
	}


	private fun replaceQuoteSpans(spannable: Spannable)
	{
		val quoteSpans: Array<QuoteSpan> =
			spannable.getSpans(0, spannable.length - 1, QuoteSpan::class.java)

		quoteSpans.forEach {
			val start: Int = spannable.getSpanStart(it)
			val end: Int = spannable.getSpanEnd(it)
			val flags: Int = spannable.getSpanFlags(it)
			spannable.removeSpan(it)
			spannable.setSpan(
				QuoteSpanClass(
					// background color
					ContextCompat.getColor(requireContext(), R.color.quote_background),
					// strip color
					ContextCompat.getColor(requireContext(), R.color.quote_strip),
					// strip width
					10F, 50F
				),
				start, end, flags
			)
		}
	}

	private fun saveFeed() {
		binding.save.setOnClickListener {
			viewModel.saveFavouriteFeed(feedSelected)
			setImageResourceImageButton(feedSelected.favourite)
			viewModel.fetchFeedsDeveloper()
		}
	}

	private fun setImageResourceImageButton(favourite: Boolean) {
		if(favourite) {
			binding.save.setImageResource(R.drawable.bookmark_add_fill)
		} else {
			binding.save.setImageResource(R.drawable.bookmark_add)
		}
	}

	private fun closeDialog() {
		binding.close.setOnClickListener {
			dismiss()
		}
	}

	private fun showButtonUrl() {
		feedSelected.link?.let {
			binding.buttonUrl.gone()
			if(it.isNotEmpty()) {
				binding.buttonUrl.show()
				setMarginLayout()
				val url = it
				binding.buttonUrl.setOnClickListener {
					val intent = Intent(Intent.ACTION_VIEW)
					intent.data = Uri.parse(url)
					startActivity(intent)
				}
			}
		}

	}

	private fun setMarginLayout() {
		val bottom = resources.getDimension(R.dimen.if_link_exist)

		val params = binding.layoutDescription.layoutParams as ViewGroup.MarginLayoutParams
		params.bottomMargin = bottom.toInt()
	}
}