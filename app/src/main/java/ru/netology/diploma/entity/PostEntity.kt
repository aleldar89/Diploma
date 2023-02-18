package ru.netology.diploma.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diploma.dto.Attachment
import ru.netology.diploma.dto.Coordinates
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.UserPreview

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
    val coords: Coordinates? = null,
//    val coords: CoordinatesEmbedded? = null,
    val link: String? = null,
    val likeOwnerIds: List<Int>? = null,
    val mentionIds: List<Int>? = null,
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    @Embedded(prefix = "attachment_")
    val attachment: Attachment? = null,
//    val attachment: AttachmentEmbedded? = null,
    val ownedByMe: Boolean,
    val users: List<Pair<String, UserPreview>>
//    val users: List<UserPreview>,
) {
    fun toDto() = Post(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        authorJob = authorJob,
        content = content,
        published = published,
        coords = coords,
//        coords = coords?.toDto(),
        link = link,
        likeOwnerIds = likeOwnerIds,
        mentionIds = mentionIds,
        mentionedMe = mentionedMe,
        likedByMe = likedByMe,
        attachment = attachment,
//        attachment = attachment?.toDto(),
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
                coords = dto.coords,
//                coords = dto.coords?.let {
//                    CoordinatesEmbedded(it.lat, it.longitude)
//                },
                link = dto.link,
                likeOwnerIds = dto.likeOwnerIds,
                mentionIds = dto.mentionIds,
                mentionedMe = dto.mentionedMe,
                likedByMe = dto.likedByMe,
                attachment = dto.attachment,
//                attachment = dto.attachment?.let {
//                    AttachmentEmbedded(it.url, it.type)
//                },
                ownedByMe = dto.ownedByMe,
                users = dto.users,
            )
    }
}