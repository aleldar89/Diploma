package ru.netology.diploma.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.netology.diploma.entity.PostEntity

@Dao
interface PostDao {
    @Query("SELECT * FROM PostEntity ORDER BY id DESC")
    fun getPagingSource(): PagingSource<Int, PostEntity>

    @Query("SELECT * FROM PostEntity WHERE id = :id")
    suspend fun getById(id: Int): PostEntity

    @Query("DELETE FROM PostEntity WHERE id = :id")
    suspend fun removeById(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(post: PostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(posts: List<PostEntity>)

    @Query("""
        UPDATE PostEntity SET
        content = :content
        WHERE id = :id
        """)
    suspend fun updateContentById(id: Int, content: String)

    suspend fun save(post: PostEntity) =
        if (post.id == 0) insert(post)
        else updateContentById(post.id, post.content)

    suspend fun saveOld(post: PostEntity) = insert(post)

    //TODO вставка id лайкнувшего пользователя в likeOwnerIds

    @Query("""
            UPDATE PostEntity SET
            likedByMe = CASE WHEN likedByMe THEN 0 ELSE 1 END
            WHERE id = :id
        """)
    suspend fun likeById(id: Int)

    @Query("UPDATE PostEntity SET id = :id WHERE id = 0")
    suspend fun updateId(id: Int)

    @Query("SELECT * FROM PostEntity ORDER BY ID DESC LIMIT 1")
    suspend fun selectLast(): PostEntity
}