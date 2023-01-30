package ru.netology.diploma.dto

data class Event(
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String,
    val authorJob: String,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates,
    val type: Type,
    val likeOwnerIds: List<Int>,
    val likedByMe: Boolean,
    val speakerIds: List<Int>,
    val participantsIds: List<Int>,
    val participatedByMe: Boolean,
    val attachment: Attachment? = null,
    val link: String,
    val ownedByMe: Boolean,
    val users: List<User>,
)