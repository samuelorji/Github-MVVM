package com.example.githubapp.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import arrow.core.Either
import com.example.githubapp.models.GithubSearchUserResponse
import com.example.githubapp.repositories.GithubApiRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MainActivityViewModel : ViewModel() {


    private val tag : String = "MainActivityViewModel"
    private val isUpdating: MutableLiveData<Boolean> = MutableLiveData()
    val showNoConnectivity: MutableLiveData<Boolean> = MutableLiveData()
    val githubUserSearchResult: MutableLiveData<GithubSearchUserResponse> = MutableLiveData()

    fun getUsersByUsername(username : String) {
        isUpdating.postValue(true)
        CoroutineScope(Dispatchers.IO).launch {
            val result = GithubApiRepository.getUsersByUsername(username)
            when(result){
                is Either.Right -> {
                    showNoConnectivity.postValue(false)
                    githubUserSearchResult.postValue(result.b)

                }

                is Either.Left ->

                    when(result.a) {
                        is SocketTimeoutException ->
                            showNoConnectivity.postValue(true)

                        is UnknownHostException ->
                            showNoConnectivity.postValue(true)

                        else -> {
                            Log.i(tag, result.a.javaClass.kotlin.qualifiedName?: "unknown exception name")
                        }
                    }


            }
        }
    }


}