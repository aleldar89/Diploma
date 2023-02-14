package ru.netology.diploma.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.netology.diploma.dto.*

@Entity
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
    @Embedded
    val coords: CoordinatesEmbedded? = null,
    @Embedded
    val type: Type,
    @Embedded
    val likeOwnerIds: List<Int>? = null,
    val likedByMe: Boolean,
    @Embedded
    val speakerIds: List<Int>? = null,
    @Embedded
    val participantsIds: List<Int>? = null,
    val participatedByMe: Boolean,
    @Embedded(prefix = "attachment_")
    val attachment: AttachmentEmbedded? = null,
    val link: String? = null,
    val ownedByMe: Boolean,
    @Embedded
    val users: List<UserPreview>,
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
        coords = coords?.toDto(),
        type = type,
        likeOwnerIds = likeOwnerIds,
        likedByMe = likedByMe,
        speakerIds = speakerIds,
        participantsIds = participantsIds,
        participatedByMe = participatedByMe,
        attachment = attachment?.toDto(),
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
                coords = dto.coords?.let {
                    CoordinatesEmbedded(it.lat, it.longitude)
                },
                type = dto.type,
                likeOwnerIds = dto.likeOwnerIds,
                likedByMe = dto.likedByMe,
                speakerIds = dto.speakerIds,
                participantsIds = dto.participantsIds,
                participatedByMe = dto.participatedByMe,
                attachment = dto.attachment?.let {
                    AttachmentEmbedded(it.url, it.type)
                },
                link = dto.link,
                ownedByMe = dto.ownedByMe,
                users = dto.users
            )
    }
}