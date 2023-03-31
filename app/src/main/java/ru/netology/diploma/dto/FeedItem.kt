package ru.netology.diploma.dto

sealed interface FeedItem {
    val id: Int
}

data class Post(
    override val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val published: String,
    val coords: Coordinates? = null,
    val link: String? = null,
    val likeOwnerIds: List<Int>? = null,
    val mentionIds: List<Int>? = null,
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    val attachment: Attachment? = null,
    val ownedByMe: Boolean,
    val users: Map<Long, UserPreview>,
) : FeedItem

data class Event(
    override val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val datetime: String,
    val published: String,
    val coords: Coordinates? = null,
    val type: Type,
    val likeOwnerIds: List<Int>? = null,
    val likedByMe: Boolean,
    val speakerIds: List<Int>? = null,
    val participantsIds: List<Int>? = null,
    val participatedByMe: Boolean,
    val attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean,
    val users: Map<Long, UserPreview>,
) : FeedItem