package com.dmm.rssreader.data.persistence.converters

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

@ProvidedTypeConverter
class ConverterList  @Inject constructor(
  private val moshi: Moshi
){

  @TypeConverter
  fun fromString(value: String): MutableList<String>? {
    val listType = Types.newParameterizedType(MutableList::class.java, String::class.java)
    val adapter: JsonAdapter<MutableList<String>> = moshi.adapter(listType)
    return adapter.fromJson(value)
  }

  @TypeConverter
  fun fromListString(list: MutableList<String>?): String {
    val listType = Types.newParameterizedType(MutableList::class.java, String::class.java)
    val adapter: JsonAdapter<MutableList<String>> = moshi.adapter(listType)
    return adapter.toJson(list)
  }
}