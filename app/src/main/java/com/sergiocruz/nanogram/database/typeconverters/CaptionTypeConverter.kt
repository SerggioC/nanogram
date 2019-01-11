package com.sergiocruz.nanogram.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sergiocruz.nanogram.model.endpoint.usermedia.Caption

class CaptionTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun jsonToCaption(data: String?): Caption? {
        if (data == null) return Caption()
        val dataType = object : TypeToken<Caption>() {}.type
        return gson.fromJson(data, dataType)
    }

    @TypeConverter
    fun captionToJson(data: Caption?): String {
        return gson.toJson(data)
    }

}
