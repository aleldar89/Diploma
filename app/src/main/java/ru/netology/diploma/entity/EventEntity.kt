package ru.netology.diploma.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import ru.netology.diploma.dto.*

@Entity
@TypeConverters(Converters::class)
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val authorId: Int,
    val author: String,
    val authorAvatar: String? = null,
    val authorJob: String?,
    val content: String,
    val datetime: String,
    val published: String,
//    @Embedded(prefix = "coordinates_")
    @Embedded
    val coords: Coordinates? = null,
    val type: Type,
    val likeOwnerIds: List<Int>? = null,
    val likedByMe: Boolean,
    val speakerIds: List<Int>? = null,
    val participantsIds: List<Int>? = null,
    val participatedByMe: Boolean,
    @Embedded(prefix = "attachment_")
    val attachment: Attachment? = null,
    val link: String? = null,
    val ownedByMe: Boolean,
    val users: Map<Long, UserPreview>,
) {
    fun toDto() = Event(
        id = id,
        authorId = authorId,
        author = author,
        authorAvatar = authorAvatar,
        authorJob = authorJob,
        content = content,
        datetime = datetime,
        published = published,
        coords = coords,
        type = type,
        likeOwnerIds = likeOwnerIds,
        likedByMe = likedByMe,
        speakerIds = speakerIds,
        participantsIds = participantsIds,
        participatedByMe = participatedByMe,
        attachment = attachment,
        link = link,
        ownedByMe = ownedByMe,
        users = users,
    )

    companion object {
        fun fromDto(dto: Event) =
            EventEntity(
                id = dto.id,
                authorId = dto.authorId,
                author = dto.author,
                authorAvatar = dto.authorAvatar,
                authorJob = dto.authorJob,
                content = dto.content,
                datetime = dto.datetime,
                published = dto.published,
                coords = dto.coords,
                type = dto.type,
                likeOwnerIds = dto.likeOwnerIds,
                likedByMe = dto.likedByMe,
                speakerIds = dto.speakerIds,
                participantsIds = dto.participantsIds,
                participatedByMe = dto.participatedByMe,
                attachment = dto.attachment,
                link = dto.link,
                ownedByMe = dto.ownedByMe,
                users = dto.users
            )
    }
}