package com.dmm.rssreader.presentation.dialog

import QuoteSpanClass
import android.content.Intent
import android.net.Uri
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.QuoteSpan
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import com.dmm.rssreader.R
import com.dmm.rssreader.databinding.FeedDescriptionDialogBinding
import com.dmm.rssreader.domain.extension.gone
import com.dmm.rssreader.domain.extension.show
import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.utils.ImageGetter

class FeedDescriptionDialog(private val feedSelected: FeedUI) : CustomDialog<FeedDescriptionDialogBinding>(
	FeedDescriptionDialogBinding::inflate,
	R.style.FullScreenDialog
) {

	override fun onViewCreated() {
		super.onViewCreated()
		setUpUI()
		saveFeed()
		closeDialog()
		showButtonUrl()
	}

	private fun setUpUI(){
		setImageResourceImageButton(feedSelected.favourite)
		binding.title.text = feedSelected.title
		displayHtml(feedSelected.description)
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
			setImageResourceImageButton(!feedSelected.favourite)
			viewModel.saveFavouriteFeed(feedSelected) {
				viewModel.fetchFeedsDeveloper()
			}
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