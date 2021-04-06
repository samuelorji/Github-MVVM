package com.example.githubapp.api

import com.example.githubapp.models.GithubSearchResults.GithubUsersSearchResult
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubApi {

    @GET("users")
    suspend fun getUsersByUsername(@Query("q") username : String) : GithubUsersSearchResult
}