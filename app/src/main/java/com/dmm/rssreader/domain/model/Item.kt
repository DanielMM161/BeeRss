package com.dmm.rssreader.domain.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Namespace
import org.simpleframework.xml.Root

@Root(name = "item", strict = false)
data class Item(
	@field:Element(name = "title")
	var title: String? = null,
	@field:Element(name = "link")
	var link: String? = null,
	@field:Element(name = "description", required = false)
	var description: String? = "",
	@field:Element(name = "encoded", required = false)
	@Namespace(prefix = "content")
	var content: String,
	@field:Element(name = "pubDate")
	var pubDate: String? = null,
)
