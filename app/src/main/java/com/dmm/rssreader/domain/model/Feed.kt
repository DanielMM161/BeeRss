package com.dmm.rssreader.domain.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Path
import org.simpleframework.xml.Root

@Root(name = "rss", strict = false)
data class Feed(
	@field:Element(name = "title")
	@param:Element(name = "title")
	@field:Path("channel")
	@param:Path("channel")
	val title: String,
	@field:ElementList(name = "item", inline = true, required = false)
	@param:ElementList(name = "item", inline = true, required = false)
	@field:Path("channel")
	@param:Path("channel")
	val itemList: List<Item>? = null
)