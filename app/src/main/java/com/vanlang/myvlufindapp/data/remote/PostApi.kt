package com.vanlang.myvlufindapp.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface PostApi {

    @GET("posts")
    suspend fun getPosts(): List<PostDto>

    @POST("posts")
    suspend fun createPost(@Body post: PostDto): PostDto

    @PUT("posts/{id}")
    suspend fun updatePost(@Path("id") id: Long, @Body post: PostDto): PostDto
}