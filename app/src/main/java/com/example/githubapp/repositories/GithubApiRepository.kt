package com.example.githubapp.repositories

import arrow.core.Either
import com.example.githubapp.api.GithubApi
import com.example.githubapp.models.GithubSearchUserResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GithubApiRepository {

    private val BASEURL = "https://api.github.com/search/"
    private val githubApi: GithubApi = Retrofit.Builder()
        .baseUrl(BASEURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GithubApi::class.java)



    suspend fun getUsersByUsername(username : String): Either<Throwable, GithubSearchUserResponse> {
        return try {
            Either.right(GithubSearchUserResponse(username,githubApi.getUsersByUsername(username).users))
        } catch (e : Exception){
            Either.left(e)
        }
    }
}