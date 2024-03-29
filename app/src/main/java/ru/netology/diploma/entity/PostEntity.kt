package ru.netology.diploma.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.netology.diploma.dto.Attachment
import ru.netology.diploma.dto.Coordinates
import ru.netology.diploma.dto.Post
import ru.netology.diploma.dto.UserPreview

@Entity
@TypeConverters(Converters::class)
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
    val coords: Coordinates?,
    val link: String? = null,
    val likeOwnerIds: List<Int>? = null,
    val mentionIds: List<Int>? = null,
    val mentionedMe: Boolean,
    val likedByMe: Boolean,
    @Embedded(prefix = "attachment_")
    val attachment: Attachment? = null,
    val ownedByMe: Boolean,
    val users: Map<Long, UserPreview>,
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
        link = link,
        likeOwnerIds = likeOwnerIds,
        mentionIds = mentionIds,
        mentionedMe = mentionedMe,
        likedByMe = likedByMe,
        attachment = attachment,
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
                link = dto.link,
                likeOwnerIds = dto.likeOwnerIds,
                mentionIds = dto.mentionIds,
                mentionedMe = dto.mentionedMe,
                likedByMe = dto.likedByMe,
                attachment = dto.attachment,
                ownedByMe = dto.ownedByMe,
                users = dto.users,
            )
    }
}