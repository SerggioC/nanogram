package com.sergiocruz.nanogram.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sergiocruz.nanogram.model.endpoint.usermedia.Likes

class LikesTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun jsonToLikes(data: String?): Likes? {
        if (data == null) return Likes()
        val dataType = object : TypeToken<Likes>() {}.type
        return gson.fromJson(data, dataType)
    }

    @TypeConverter
    fun likesToJson(images: Likes?): String {
        return gson.toJson(images)
    }

}
