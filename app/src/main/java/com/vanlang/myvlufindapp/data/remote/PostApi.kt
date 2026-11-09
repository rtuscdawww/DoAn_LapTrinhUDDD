package com.vanlang.myvlufindapp.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface PostApi {

    @GET("posts")
    suspend fun getPosts(): List<PostDto>

    @POST("posts")
    suspend fun createPost(@Body post: PostDto): PostDto
}