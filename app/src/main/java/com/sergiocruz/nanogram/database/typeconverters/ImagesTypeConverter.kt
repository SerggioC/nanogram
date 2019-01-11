package com.sergiocruz.nanogram.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sergiocruz.nanogram.model.endpoint.usermedia.Images

class ImagesTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun jsonToImages(data: String?): Images? {
        if (data == null) return Images()
        val dataType = object : TypeToken<Images>() {}.type
        return gson.fromJson(data, dataType)
    }

    @TypeConverter
    fun imageToJson(images: Images?): String {
        return gson.toJson(images)
    }
}
