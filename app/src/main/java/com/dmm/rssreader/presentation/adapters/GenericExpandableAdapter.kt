package com.dmm.rssreader.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.viewbinding.ViewBinding

abstract class GenericExpandableAdapter<T, VB: ViewBinding>(
	private val collectionGroup: Collection<String>,
	private val collectionItems: Map<String, Collection<T>>,
	private val group_layout: Int,
	private val bindingInflater: (inflater: LayoutInflater) -> VB
	) : BaseExpandableListAdapter() {

	private lateinit var _binding: VB
	protected val binding: VB get() = _binding
	protected open fun getChildView(item: T) = Unit

	override fun getGroupCount(): Int {
		return collectionGroup.size
	}

	override fun getChildrenCount(groupPosition: Int): Int {
		return collectionItems[collectionGroup.elementAt(groupPosition)]?.size ?: -1
	}

	override fun getGroup(groupPosition: Int): Any {
		return collectionGroup.elementAt(groupPosition)
	}

	override fun getChild(groupPosition: Int, childPosition: Int): T {
		val collection = collectionItems[collectionGroup.elementAt(groupPosition)] ?: listOf()
		return collection.elementAt(childPosition)
	}

	override fun getGroupId(groupPosition: Int): Long {
		return groupPosition.toLong()
	}

	override fun getChildId(groupPosition: Int, childPosition: Int): Long {
		return childPosition.toLong()
	}

	override fun hasStableIds(): Boolean {
		return true
	}

	override fun getGroupView(groupPosition: Int, isExpaned: Boolean, convertView: View?, parent: ViewGroup?): View {
		var myConvertView = convertView
		val title = getGroup(groupPosition) as String
		if (myConvertView == null) {
			val inflater = parent?.context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater;
			myConvertView = inflater.inflate(group_layout, null)
		}
		val textView = findFirstTextView(myConvertView!!)
		textView?.text = title

		return myConvertView
	}

	override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup): View {
		val convertView = convertView
		_binding = if (convertView == null) {
			bindingInflater.invoke(LayoutInflater.from(parent.context))
		} else {
			DataBindingUtil.getBinding(convertView)!!
		}
		getChildView(getChild(groupPosition, childPosition))
		return binding.root
	}

	override fun isChildSelectable(p0: Int, p1: Int): Boolean {
		return true
	}

	private fun findFirstTextView(view: View): TextView? {
		if (view is TextView) {
			return view
		}

		if (view is ViewGroup) {
			val childCount = view.childCount
			for (i in 0 until childCount) {
				val child = view.getChildAt(i)
				val textView = findFirstTextView(child)
				if (textView != null) {
					return textView
				}
			}
		}
		return null
	}
}