package com.dmm.rssreader.utils

import android.content.Context
import android.content.DialogInterface
import android.content.res.Configuration
import android.content.res.Resources
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class Utils {

	companion object {

		fun isNightMode(resources: Resources): Boolean {
			return when (resources.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
				Configuration.UI_MODE_NIGHT_YES -> {
					true
				}
				Configuration.UI_MODE_NIGHT_NO -> {
					false
				}
				else -> {
					false
				}
			}
		}

		fun alertDialog(
			context: Context?,
			message: String,
			title: String = "",
			textPositiveButton: String,
			textNegativeButton: String = "",
			callback: (DialogInterface) -> Unit
		) {
			context?.let {
				val alertDialog = AlertDialog.Builder(it)
				if(!title.isEmpty()) {
					alertDialog.setTitle(title)
				}
				alertDialog.setMessage(message)

				alertDialog.setPositiveButton(
					textPositiveButton,
					DialogInterface.OnClickListener { dialog, _ -> callback.invoke(dialog) }
				)

				if(textNegativeButton.isNotEmpty()) {
					alertDialog.setNegativeButton(
						textNegativeButton,
						null
					)
				}

				alertDialog.create().show()
			}
		}

		fun <T> merge(first: Collection<T>, second: Collection<T>): Collection<T> {
			return first.plus(second)
		}
	}
}