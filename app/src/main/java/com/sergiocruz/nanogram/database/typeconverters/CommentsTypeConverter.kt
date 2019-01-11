package com.sergiocruz.nanogram.database.typeconverters

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sergiocruz.nanogram.model.endpoint.usermedia.Comments

class CommentsTypeConverter {
    private val gson = Gson()

    @TypeConverter
    fun jsonToComments(data: String?): Comments? {
        if (data == null) return Comments()
        val dataType = object : TypeToken<Comments>() {}.type
        return gson.fromJson(data, dataType)
    }

    @TypeConverter
    fun commentsToJson(data: Comments?): String {
        return gson.toJson(data)
    }

}
