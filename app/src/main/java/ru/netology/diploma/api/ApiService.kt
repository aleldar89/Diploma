package ru.netology.diploma.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import ru.netology.diploma.auth.Token
import ru.netology.diploma.dto.*

interface ApiService {

    /** Events methods */
    @GET("events")
    suspend fun getAllEvents(): Response<List<Event>>

    @POST("events")
    suspend fun saveEvent(@Body event: Event): Response<Event>

    @GET("events/latest")
    suspend fun getLatestEvents(@Query("count") count: Int): Response<List<Event>>

    @GET("events/{id}")
    suspend fun getByIdEvent(@Path("id") id: Int): Response<Event>

    @DELETE("events/{id}")
    suspend fun removeByIdEvent(@Path("id") id: Int): Response<Unit>

    @GET("events/{id}/after")
    suspend fun getAfterEvents(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Event>>

    @GET("events/{id}/before")
    suspend fun getBeforeEvents(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Event>>

    @POST("events/{id}/likes")
    suspend fun likeByIdEvent(@Path("id") id: Int): Response<Event>

    @DELETE("events/{id}/likes")
    suspend fun dislikeByIdEvent(@Path("id") id: Int): Response<Event>

    @GET("events/{id}/newer")
    suspend fun getNewerEvent(@Path("id") id: Long): Response<List<Event>>

    @POST("events/{id}/participants")
    suspend fun addByIdParticipants(@Path("id") id: Int): Response<Event>

    @DELETE("events/{id}/participants")
    suspend fun removeByIdParticipants(@Path("id") id: Int): Response<Event>


    /** Media method */
    @Multipart
    @POST("media")
    suspend fun upload(@Part media: MultipartBody.Part): Response<Media>


    /** Jobs methods */
    @GET("my/jobs")
    suspend fun getMyJobs(): Response<List<Job>>

    @POST("my/jobs")
    suspend fun saveMyJob(@Body job: Job): Response<Job>

    @DELETE("my/jobs/{id}")
    suspend fun removeByIdMyJob(@Path("id") id: Int): Response<Unit>

    @GET("{id}/jobs")
    suspend fun getByUserIdJobs(@Path("id") id: Int): Response<Job>


    /** MyWall (Posts) methods */
    @GET("my/wall")
    suspend fun getMyWall(): Response<List<Post>>

    @GET("my/wall/latest")
    suspend fun getLatestMyWall(@Query("count") count: Int): Response<List<Post>>

    @GET("my/wall/{id}/after")
    suspend fun getAfterMyWall(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("my/wall/{id}/before")
    suspend fun getBeforeMyWall(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("my/wall/{id}/newer")
    suspend fun getNewerMyWall(@Path("id") id: Long): Response<List<Post>>


    /** Posts methods */
    @GET("posts")
    suspend fun getAllPosts(): Response<List<Post>>

    @POST("posts")
    suspend fun savePost(@Body post: Post): Response<Post>

    @GET("posts/latest")
    suspend fun getLatestPosts(@Query("count") count: Int): Response<List<Post>>

    @GET("posts/{id}")
    suspend fun getByIdPost(@Path("id") id: Int): Response<Post>

    @DELETE("posts/{id}")
    suspend fun removeByIdPost(@Path("id") id: Int): Response<Unit>

    @GET("posts/{id}/after")
    suspend fun getAfterPosts(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("posts/{id}/before")
    suspend fun getBeforePosts(
        @Path("id") id: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @POST("posts/{id}/likes")
    suspend fun likeByIdPost(@Path("id") id: Int): Response<Post>

    @DELETE("posts/{id}/likes")
    suspend fun dislikeByIdPost(@Path("id") id: Int): Response<Post>

    @GET("posts/{id}/newer")
    suspend fun getNewerPosts(@Path("id") id: Long): Response<List<Post>>


    /** Users methods */
    @GET("users")
    suspend fun getUsers(): Response<List<UserResponse>>

    @FormUrlEncoded
    @POST("users/authentication")
    suspend fun authUser(
        @Field("login") login: String,
        @Field("password") password: String
    ): Response<Token>

    @FormUrlEncoded
    @POST("users/registration")
    suspend fun registerUser(
        @Field("login") login: String,
        @Field("password") password: String,
        @Field("name") name: String
    ): Response<Token>

    @Multipart
    @POST("users/registration")
    suspend fun registerWithPhoto(
        @Field("login") login: RequestBody,
        @Field("password") password: RequestBody,
        @Field("name") name: RequestBody,
        @Part media: MultipartBody.Part
    ): Response<Token>

    @GET("users/id")
    suspend fun getByIdUser(@Path("id") id: Int): Response<UserResponse>


    /** Wall (Posts) methods */
    @GET("{authorId}/wall")
    suspend fun getAuthorWall(): Response<List<Post>>

    @GET("{authorId}/wall/latest")
    suspend fun getLatestAuthorWall(@Query("count") count: Int): Response<List<Post>>

    @GET("{authorId}/wall/{postId}/after")
    suspend fun getAfterAuthorWall(
        @Path("authorId") authorId: Int,
        @Path("postId") postId: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("{authorId}/wall/{postId}/before")
    suspend fun getBeforeAuthorWall(
        @Path("authorId") authorId: Int,
        @Path("postId") postId: Int,
        @Query("count") count: Int
    ): Response<List<Post>>

    @GET("{authorId}/wall/{postId}/newer")
    suspend fun getNewerAuthorWall(
        @Path("authorId") authorId: Int,
        @Path("postId") postId: Int
    ): Response<List<Post>>

}