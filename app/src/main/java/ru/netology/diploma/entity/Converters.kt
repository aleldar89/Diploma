package ru.netology.diploma.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import ru.netology.diploma.dto.Attachment
import ru.netology.diploma.dto.Coordinates
import ru.netology.diploma.dto.UserPreview

class Converters {

    @TypeConverter
    fun toIdsList(ids: String): List<Int> {
        return if (ids.isEmpty())
            emptyList()
        else {
            ids
                .split(",")
                .map {
                    it.toInt()
                }
        }
    }

    @TypeConverter
    fun fromIdsList(ids: List<Int>): String = ids
        .joinToString(separator = ",")
        .ifBlank { "" }

    @TypeConverter
    fun toUsersMap(users: String): Map<Long, UserPreview> {
        val gson = Gson()
        val mapType = object : TypeToken<Map<Long, UserPreview>>() {}.type
        return gson.fromJson(users, mapType)
    }

    @TypeConverter
    fun fromUsersMap(users: Map<Long, UserPreview>): String {
        return Gson().toJson(users)
    }

    @TypeConverter
    fun toAttachment(attachment: String): Attachment? {
        return Gson().fromJson(attachment, Attachment::class.java) ?: null
    }

    @TypeConverter
    fun fromAttachment(attachment: Attachment): String? {
        return Gson().toJson(attachment) ?: null
    }

    @TypeConverter
    fun toCoordinates(coordinates: String): Coordinates? {
        return Gson().fromJson(coordinates, Coordinates::class.java) ?: null
    }

    @TypeConverter
    fun fromCoordinates(coordinates: Coordinates): String? {
        return Gson().toJson(coordinates) ?: null
    }

}