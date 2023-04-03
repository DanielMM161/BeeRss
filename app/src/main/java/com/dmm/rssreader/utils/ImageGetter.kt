package com.dmm.rssreader.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageGetter(
	val res: Resources,
	val htmlTextView: TextView,
	val context: Context
) : Html.ImageGetter {


	override fun getDrawable(url: String?): Drawable {
		val holder = BitmapDrawablePlaceHolder(res, null)

		GlobalScope.launch(Dispatchers.IO) {
			runCatching {
				val bitmap = Glide.with(context)
					.asBitmap()
					.load(url)
					.submit()
					.get()
				val drawable = BitmapDrawable(res, bitmap)

				// To make sure Images don't go out of screen , Setting width less
				// than screen width, You can change image size if you want
				val width = getScreenWidth() - 150

				// Images may stretch out if you will only resize width,
				// hence resize height to according to aspect ratio
				val aspectRatio: Float =
					(drawable.intrinsicWidth.toFloat()) / (drawable.intrinsicHeight.toFloat())
				val height = width / aspectRatio
				drawable.setBounds(5, 10, width, height.toInt())
				holder.setDrawable(drawable)
				holder.setBounds(5, 10, width, height.toInt())
				withContext(Dispatchers.Main) {
					htmlTextView.text = htmlTextView.text
				}
			}
		}
		return holder
	}

	// Function to get screenWidth used above
	fun getScreenWidth() =
		Resources.getSystem().displayMetrics.widthPixels

	// Actually Putting images
	internal class BitmapDrawablePlaceHolder(res: Resources, bitmap: Bitmap?) :
		BitmapDrawable(res, bitmap) {
		private var drawable: Drawable? = null

		override fun draw(canvas: Canvas) {
			drawable?.run { draw(canvas) }
		}

		fun setDrawable(drawable: Drawable) {
			this.drawable = drawable
		}
	}
}