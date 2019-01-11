package com.sergiocruz.nanogram.database.typeconverters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class GeneralTypeConverter {
    val gson = Gson()

    inline fun <reified T> fromJson(data: String?): T? {
        if (data == null) return null
        val dataType = object : TypeToken<T>() {}.type
        return gson.fromJson(data, dataType)
    }

    inline fun <reified T> toJson(data: T): String {
        return gson.toJson(data)
    }
}