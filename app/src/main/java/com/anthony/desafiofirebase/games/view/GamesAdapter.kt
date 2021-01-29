package com.anthony.desafiofirebase.games.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.anthony.desafiofirebase.R
import com.anthony.desafiofirebase.games.model.Game
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class GamesAdapter(
    private val games: List<Game>,
    private val onGameClickListener: OnGameClickListener
) : RecyclerView.Adapter<GamesAdapter.GameViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.games_item, parent, false)

        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game, onGameClickListener)
    }

    override fun getItemCount() = games.size

    class GameViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val image: ImageView = view.findViewById(R.id.image)
        private val name: TextView = view.findViewById(R.id.name)
        private val createdAt: TextView = view.findViewById(R.id.created_at)

        fun bind(game: Game, onGameClickListener: OnGameClickListener) {

            FirebaseStorage.getInstance().getReference("uploads")
                .child(game.image).downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(image)
                }
            name.text = game.name
            createdAt.text = game.createdAt

            itemView.setOnClickListener {
                onGameClickListener.onGameClick(adapterPosition)
            }
        }
    }
}