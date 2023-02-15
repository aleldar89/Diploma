package ru.netology.diploma.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.netology.diploma.dto.Attachment
import ru.netology.diploma.dto.Coordinates
import ru.netology.diploma.dto.Type
import ru.netology.diploma.dto.UserPreview

class Converters {

    /** доработать на случай null */

    @TypeConverter
    fun toIdsList(ids: String): List<Int> = ids
        .split(",")
        .map {
            it.toInt()
        }

    @TypeConverter
    fun fromIdsList(ids: List<Int>): String = ids.joinToString(separator = ",")

    @TypeConverter
    fun toUsersList(users: String): List<UserPreview> {
        return users
            .split(";")
            .map {
                Gson().fromJson(it, UserPreview::class.java)
            }
    }

    @TypeConverter
    fun fromUsersList(users: List<UserPreview>): String {
        return users
            .map {
                Gson().toJson(it)
            }
            .joinToString(separator = ";")
    }

    @TypeConverter
    fun toAttachment(attachment: String): Attachment {
        return Gson().fromJson(attachment, Attachment::class.java)
    }

    @TypeConverter
    fun fromAttachment(attachment: Attachment): String {
        return Gson().toJson(attachment)
    }

    @TypeConverter
    fun toCoordinates(coordinates: String): Coordinates {
        return Gson().fromJson(coordinates, Coordinates::class.java)
    }

    @TypeConverter
    fun fromCoordinates(coordinates: Coordinates): String {
        return Gson().toJson(coordinates)
    }

//    @TypeConverter
//    fun toType(line: String): Type = Type.valueOf(line)
//
//    @TypeConverter
//    fun fromType(type: Type): String = type.name

}