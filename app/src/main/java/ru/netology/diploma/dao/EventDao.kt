package ru.netology.diploma.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.diploma.entity.EventEntity

@Dao
interface EventDao {
    @Query("SELECT * FROM EventEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, EventEntity>

    @Query("SELECT * FROM EventEntity WHERE id = :id")
    suspend fun getById(id: Int): EventEntity

    @Query("DELETE FROM EventEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: EventEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(events: List<EventEntity>)

    @Query("""
        UPDATE EventEntity SET
        content = :content
        WHERE id = :id
        """)
    suspend fun updateContentById(id: Int, content: String)

    suspend fun save(event: EventEntity) =
        if (event.id == 0) insert(event)
        else updateContentById(event.id, event.content)

    suspend fun saveOld(event: EventEntity) = insert(event)

    //TODO вставка/удаление id лайкнувшего пользователя в likeOwnerIds

    @Query("""
            UPDATE EventEntity SET
            likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
            WHERE id = :id
        """)
    suspend fun likeById(id: Int)

    @Query("UPDATE EventEntity SET id = :id WHERE id = 0")
    suspend fun updateId(id: Int)

    @Query("SELECT * FROM EventEntity ORDER BY ID DESC LIMIT 1")
    suspend fun selectLast(): EventEntity

    //TODO вставка/удаление id пользователя-участник в participantsIds

    @Query("""
            UPDATE EventEntity SET
            participatedByMe = CASE WHEN participatedByMe THEN 0 ELSE 1 END
            WHERE id = :id
        """)
    suspend fun participateById(id: Int)
}