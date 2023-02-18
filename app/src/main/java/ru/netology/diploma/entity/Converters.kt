package ru.netology.diploma.entity

import androidx.room.TypeConverter
import com.google.gson.Gson
import ru.netology.diploma.dto.Attachment
import ru.netology.diploma.dto.Coordinates
import ru.netology.diploma.dto.UserPreview

class Converters {

    @TypeConverter
    fun toIdsList(ids: String): List<Int> = ids
        .split(",")
        .map {
            it.toInt()
        }
        .ifEmpty { emptyList() }

    @TypeConverter
    fun fromIdsList(ids: List<Int>): String = ids
        .joinToString(separator = ",")
        .ifBlank { "" }

    @TypeConverter
    fun toUsersList(users: String): List<Pair<String, UserPreview>> {
        return users
            .trim()
            .split(",")
            .map {
                Pair(
                    it.substringBefore(": ").trim(),
                    Gson().fromJson(it.substringAfter(": ").trim(), UserPreview::class.java)
                )
            }
    }

    @TypeConverter
    fun fromUsersList(users: List<Pair<String, UserPreview>>): String = users
        .joinToString(separator = ",") {
            Gson().toJson(it)
        }
        .ifBlank { "" }


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

//@TypeConverter
//fun toUsersList(users: String): List<Pair<String, UserPreview>> {
//    return users
//        .trim()
//        .split(",")
//        .map {
//            it.split(' ')
//            Pair(
//                it[0].toString(),
//                Gson().fromJson(it[1].toString(), UserPreview::class.java)
//            )
//        }
//}

//    @TypeConverter
//    fun toUsersList(users: String): List<UserPreview> = users
//        .split(",")
//        .map {
//            Gson().fromJson(it, UserPreview::class.java)
//        }
//        .ifEmpty { emptyList() }

//    @TypeConverter
//    fun toUsersList(users: String): List<UserPreview> = users
//        .trim()
//        .splitToSequence(' ')
//        .toList()
//        .map {
//            Gson().fromJson(it, UserPreview::class.java)
//        }

//    @TypeConverter
//    fun fromUsersList(users: List<UserPreview>): String = users
//        .map {
//            Gson().toJson(it)
//        }
//        .joinToString(separator = ",")
//        .ifBlank { "" }

//    @TypeConverter
//    fun toType(line: String): Type = Type.valueOf(line)
//
//    @TypeConverter
//    fun fromType(type: Type): String = type.name