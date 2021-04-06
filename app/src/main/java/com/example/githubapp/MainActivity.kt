package com.example.githubapp

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkInfo
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import arrow.core.Either
import com.example.githubapp.adapters.GithubUsersAdapter
import com.example.githubapp.databinding.ActivityMainBinding
import com.example.githubapp.models.GithubSearchResults
import com.example.githubapp.viewmodels.MainActivityViewModel

class MainActivity : AppCompatActivity(), GithubUsersAdapter.GithubUserClickedListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: GithubUsersAdapter
    private lateinit var githubUsersViewModel : MainActivityViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var userNameField : EditText
    private lateinit var noResultsText : TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var searchButton: Button
    private var isToastCompleted: Boolean = true

    private val tag = "**MainActivity**"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        githubUsersViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        initViews()

        searchButton.setOnClickListener {
            closeKeyboard(this.currentFocus)
            showNoInternetConnectivity(false)
            if (isToastCompleted) {
                if (isConnectedToInternet()) {
                    performSearch()
                } else {
                    showNoInternetConnectivity(true)
                }
            }
        }

        githubUsersViewModel.showNoConnectivity.observe(this, Observer {
            when(it){
                true -> {
                    showProgressbar(false)
                    showNoInternetConnectivity(true)
                }
                else  ->  Unit
            }

        })
        githubUsersViewModel.githubUserSearchResult.observe(this, Observer { result ->
            showProgressbar(false)
            if(result.users.isEmpty()){
                showUserNotFound(result.userName)
            } else {
                recyclerView.visibility = View.VISIBLE
                (recyclerView.adapter as GithubUsersAdapter).updateList(result.users)
            }

        })
    }

    private fun performSearch(){
        recyclerView.visibility = View.INVISIBLE
        val searchCriteria = userNameField.text.toString()
        if(searchCriteria.isEmpty()){
            showEmptySearchToast()
        } else {
            showProgressbar(true)
            githubUsersViewModel.getUsersByUsername(searchCriteria)
        }
    }

    private fun closeKeyboard(view : View?) : Unit {
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view?.let{v ->
            imm.hideSoftInputFromWindow(v.windowToken, 0)
        }

    }

    fun isConnectedToInternet() : Boolean {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            capabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true

        } else {
            val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
            activeNetwork?.isConnectedOrConnecting == true
        }
    }

    private fun showUserNotFound(userName: String) {
        searchButton.isEnabled = false
        val message  = "User $userName not found"
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
        searchButton.isEnabled = true
        isToastCompleted = false
        Handler().postDelayed(run { { isToastCompleted = true}  }, 2000)


    }

    private fun showNoInternetConnectivity(show : Boolean){
        val (networkInfoVisibility, recyclerviewVisibility) = if(show) Pair(View.VISIBLE, View.INVISIBLE) else Pair(View.INVISIBLE,View.VISIBLE)
        binding.noConnectivityImage.visibility = networkInfoVisibility
        binding.noConnectivityText.visibility = networkInfoVisibility
        binding.placesRecyclerView.visibility = recyclerviewVisibility
    }
    private fun showEmptySearchToast() {
        searchButton.isEnabled = false
        val message : String = "Please Enter a github username"
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
        searchButton.isEnabled = true
    }

    private fun initViews() {
        recyclerView = binding.placesRecyclerView
        userNameField = binding.usernameEditText
        progressBar = binding.progressBar
        searchButton = binding.searchButton
        adapter = GithubUsersAdapter(this, githubUsersViewModel.githubUserSearchResult.value?.users ?: listOf(),this)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
    }

    private fun showProgressbar(show : Boolean) {
        val visibility =  if(show) View.VISIBLE else View.INVISIBLE
        binding.progressBar.visibility = visibility
    }

    override fun onGithubUserClicked(user: GithubSearchResults.GithubUser) {
        Toast.makeText(this,user.username, Toast.LENGTH_SHORT).show()
    }
}