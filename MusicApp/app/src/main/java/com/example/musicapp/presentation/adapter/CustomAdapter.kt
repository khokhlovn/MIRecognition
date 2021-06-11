package com.example.musicapp.presentation.adapter

import android.view.LayoutInflater
import android.view.OrientationEventListener
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.musicapp.R
import com.google.firebase.storage.StorageReference

class CustomAdapter :
    RecyclerView.Adapter<CustomAdapter.CustomViewHolder>() {

    val names: MutableList<StorageReference> = mutableListOf()
    lateinit var listener: (StorageReference) -> Unit

    class CustomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var author: TextView? = null
        var song: TextView? = null

        init {
            author = itemView.findViewById(R.id.tv_author)
            song = itemView.findViewById(R.id.tv_song)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_item, parent, false)
        return CustomViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        holder.author?.text = names[position].name.removeSuffix(".wav")
        holder.song?.text = names[position].name.removeSuffix(".wav")
        holder.itemView.setOnClickListener {
            listener.invoke(names[position])
        }
    }

    override fun getItemCount() = names.size
}