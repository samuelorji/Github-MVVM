package com.example.githubapp.models

data class GithubSearchUserResponse (val userName : String , val users : List<GithubSearchResults.GithubUser>)