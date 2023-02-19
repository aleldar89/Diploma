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

//    @TypeConverter
//    fun toUsersMap(users: String): Map<Long, UserPreview> = users
//        .trim()
//        .split(",")  //неверный разделитель
//        .toList()
//        .associateBy(
//            {
//                it.substringBefore(": ").trim().toLong()
//            },
//            {
//                Gson().fromJson(it.substringAfter(": ").trim(), UserPreview::class.java)
//            }
//        )

//fun JSONObject.toMap(): Map<Long, UserPreview> = keys().asSequence().associateWith {
//    when (val value = this[it])
//    {
//        is JSONArray ->
//        {
//            val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
//            JSONObject(map).toMap().values.toList()
//        }
//        is JSONObject -> value.toMap()
//        JSONObject.NULL -> null
//        else            -> value
//    }
//}

//@TypeConverter
//fun toUsersList(users: String): List<Pair<String, UserPreview>> {
//    return users
//        .trim()
//        .split(",")
//        .map {
//            Pair(
//                it.substringBefore(": ").trim(),
//                Gson().fromJson(it.substringAfter(": ").trim(), UserPreview::class.java)
//            )
//        }
//}
//
//@TypeConverter
//fun fromUsersList(users: List<Pair<String, UserPreview>>): String = users
//    .joinToString(separator = ",") {
//        Gson().toJson(it)
//    }
//    .ifBlank { "" }

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