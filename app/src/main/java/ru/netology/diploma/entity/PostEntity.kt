package ru.netology.diploma.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diploma.dto.*

@Entity
data class PostEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String? = null,
    val content: String,
    val published: String,
    @Embedded
    val coords: CoordinatesEmbedded? = null,
    val link: String? = null,
    @Embedded
    val likeOwnerIds: List<Int>? = null,
    @Embedded
    val mentionIds: List<Int>? = null,
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    @Embedded
    val attachment: AttachmentEmbedded? = null,
    val ownedByMe: Boolean,
    @Embedded
    val users: List<UserPreview>,
) {
    fun toDto() = Post(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        authorJob = authorJob,
        content = content,
        published = published,
        coords = coords?.toDto(),
        link = link,
        likeOwnerIds = likeOwnerIds,
        mentionIds = mentionIds,
        mentionedMe = mentionedMe,
        likedByMe = likedByMe,
        attachment = attachment?.toDto(),
        ownedByMe = ownedByMe,
        users = users,
    )

    companion object {
        fun fromDto(dto: Post) =
            PostEntity(
                id = dto.id,
                authorId = dto.authorId,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                authorJob = dto.authorJob,
                content = dto.content,
                published = dto.published,
                coords = dto.coords?.let {
                    CoordinatesEmbedded(it.lat, it.long)
                },
                link = dto.link,
                likeOwnerIds = dto.likeOwnerIds,
                mentionIds = dto.mentionIds,
                mentionedMe = dto.mentionedMe,
                likedByMe = dto.likedByMe,
                attachment = dto.attachment?.let {
                    AttachmentEmbedded(it.url, it.type)
                },
                ownedByMe = dto.ownedByMe,
                users = dto.users,
            )
    }
}