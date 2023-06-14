package com.dmm.rssreader.utils

import com.dmm.rssreader.domain.model.FeedUI
import com.dmm.rssreader.utils.Constants.DATE_PATTERN_1
import com.dmm.rssreader.utils.Constants.DATE_PATTERN_2
import com.dmm.rssreader.utils.Constants.DATE_PATTERN_3
import com.dmm.rssreader.utils.Constants.DATE_PATTERN_4
import com.dmm.rssreader.utils.Constants.DATE_PATTERN_OUTPUT
import com.dmm.rssreader.utils.Constants.FORMAT_BMP
import com.dmm.rssreader.utils.Constants.FORMAT_GIF
import com.dmm.rssreader.utils.Constants.FORMAT_JPEG
import com.dmm.rssreader.utils.Constants.FORMAT_JPG
import com.dmm.rssreader.utils.Constants.FORMAT_PNG
import com.dmm.rssreader.utils.Constants.FORMAT_TIF
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import java.text.SimpleDateFormat

class FeedParser() {

	private val pullParserFactory = XmlPullParserFactory.newInstance()
	private val parser = pullParserFactory.newPullParser()

	fun parse(xml: String, source: String): List<FeedUI> {
		val feedsUI: MutableList<FeedUI> = mutableListOf()
		parser.setInput(xml.byteInputStream(), null)

		while (parser.eventType != XmlPullParser.END_DOCUMENT) {
			if(parser.eventType == XmlPullParser.START_TAG && parser.name == "item") {
				feedsUI.add(readFeedItem(parser, source))
			} else if(parser.eventType == XmlPullParser.START_TAG && parser.name == "entry") {
				feedsUI.add(readFeedItem(parser, source))
			}
			parser.next()
		}

		return feedsUI
	}

	private fun readFeedItem(parse: XmlPullParser, source: String): FeedUI {
		var title = ""
		var feedSource = source
		var description = ""
		var link = ""
		var image = ""
		var published = ""

		while (parse.next() != XmlPullParser.END_TAG) {
			if(parse.eventType == XmlPullParser.START_TAG && parser.name == "title") {
				title = readText(parser)
			} else if(parse.eventType == XmlPullParser.START_TAG && parser.name == "link") {
				link = readText(parser)
			} else if(parse.eventType == XmlPullParser.START_TAG && parser.name == "description") {
				description = readText(parser)
			} else if(parse.eventType == XmlPullParser.START_TAG && parser.name == "content:encoded") {
				description = readText(parser)
			} else if(parse.eventType == XmlPullParser.START_TAG && parser.name == "content") {
				description = readText(parser)
			} else if(parse.eventType == XmlPullParser.START_TAG && parser.name == "cover_image") {
				image = readText(parser)
			} else if(parse.eventType == XmlPullParser.START_TAG && parser.name == "published") {
				published = parseDate(readText(parser))
			} else if(parse.eventType == XmlPullParser.START_TAG && parser.name == "pubDate") {
				published = parseDate(readText(parser))
			} else if(parser.eventType == XmlPullParser.START_TAG) {
				skipTag(parser)
			}
		}

		if(image.isEmpty() ) {
			image = getImageFromContent(description)
		}

		val feedUI = FeedUI(feedSource, description, link, image, published)
		feedUI.title = title

		return feedUI
	}

	private fun parseDate(dateString: String): String {
		var result = ""
		val dateFormats: List<String> = listOf(DATE_PATTERN_1, DATE_PATTERN_2, DATE_PATTERN_3,DATE_PATTERN_4)
		run lit@{
			dateFormats.forEach { format ->
				val dateformatted = formattedDate(dateString,format)
				if(!dateformatted.isEmpty()) {
					result = dateformatted
					return@lit
				}
			}
		}


		return result
	}

	fun formattedDate(dateString: String?, dateFormat: String): String {
		return try {
			val sdf = SimpleDateFormat(dateFormat).parse(dateString)
			SimpleDateFormat(DATE_PATTERN_OUTPUT).format(sdf)
		} catch (e: Exception) {
			""
		}
	}

	private fun readText(parser: XmlPullParser) : String {
		var text = ""
		while (parser.next() != XmlPullParser.END_TAG) {
			if(parser.eventType == XmlPullParser.TEXT) {
				text = parser.text
			}
		}
		var count = 0
		while(count < parser.attributeCount - 1) {
			if(parser.getAttributeName(count) == "href") {
				text = parser.getAttributeValue(count)
				count = parser.attributeCount
			}
			count++
		}
		return text
	}

	private fun getImageFromContent(content: String?): String {
		var image = ""
		val formats = listOf(
			FORMAT_JPEG,
			FORMAT_JPG,
			FORMAT_PNG,
			FORMAT_GIF,
			FORMAT_TIF,
			FORMAT_BMP
		)
		val formatsPosition = mutableMapOf<String, Int>()
		if (content != null) {
			formats.forEach { it ->
				val position = content.indexOf(it)
				if (position > -1) {
					formatsPosition[it] = position
				}
			}
			if (!formatsPosition.isEmpty()) {
				val endPosition = formatsPosition.minOf { it -> it.value }
				val keyEndPosition = formatsPosition.filterValues { it -> it == endPosition }
				val startPosition = content.indexOf("https")
				keyEndPosition.forEach { (key, value) ->
					image = content.substring(startPosition, value + key.length)
				}
			}
		}
		return image
	}

	@Suppress("ControlFlowWithEmptyBody")
	private fun skipTag(parser: XmlPullParser)  {
		while (parser.next() != XmlPullParser.END_TAG) {
		}
	}
}