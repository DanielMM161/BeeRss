package com.dmm.rssreader.utils

import android.app.ProgressDialog.show
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.core.view.accessibility.AccessibilityEventCompat.setAction
import com.dmm.rssreader.R
import com.dmm.rssreader.domain.model.FeedUI
import com.google.android.material.snackbar.Snackbar

class NotificationsUI {
	companion object {
		fun snackBar(view: View,message: String, onUndo: () -> Unit) {
			Snackbar.make(view, message, Snackbar.LENGTH_LONG).apply {
				setAction("Undo") {
					onUndo()
				}
				show()
			}
		}

		fun showToast(context: Context?, message: String) {
			context?.let {
				Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
			}
		}
	}
}