package com.example.githubapp.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.githubapp.R
import com.example.githubapp.models.GithubSearchResults.GithubUser
import de.hdodenhof.circleimageview.CircleImageView

class GithubUsersAdapter(val context: Context, var users : List<GithubUser>, val githubUserClickedListener: GithubUserClickedListener): RecyclerView.Adapter<GithubUserHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GithubUserHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_layout,parent,false)
        return GithubUserHolder(view)

    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun updateList(updatedPlaces : List<GithubUser>){
        users = updatedPlaces
        this.notifyDataSetChanged()

    }

    override fun onBindViewHolder(holder: GithubUserHolder, position: Int) {

        holder.itemView.setOnClickListener { v: View ->
            githubUserClickedListener.onGithubUserClicked(users.get(position))
        }

        val user = users.get(position)

        holder.placeName.text = user.username
        val defaultOptions: RequestOptions = RequestOptions()
            .error(R.drawable.ic_launcher_background)

        Glide.with(context)
            .setDefaultRequestOptions(defaultOptions)
            .load(user.avatar_url)
            .into(holder.placeImage)

    }

    interface GithubUserClickedListener {
        fun onGithubUserClicked(user : GithubUser) : Unit

    }
}

class GithubUserHolder(view : View) : RecyclerView.ViewHolder(view){
    val placeImage = view.findViewById<CircleImageView>(R.id.place_image)
    val placeName = view.findViewById<TextView>(R.id.place_name)

}