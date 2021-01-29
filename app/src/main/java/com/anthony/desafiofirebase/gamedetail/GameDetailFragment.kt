package com.anthony.desafiofirebase.gamedetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.anthony.desafiofirebase.R
import com.anthony.desafiofirebase.games.model.Game
import com.anthony.desafiofirebase.games.viewmodel.GamesViewModel
import com.anthony.desafiofirebase.utils.Constant
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class GameDetailFragment : Fragment() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var toolbar: MaterialToolbar
    private lateinit var editButton: FloatingActionButton
    private lateinit var toolbarBackground: ImageView
    private lateinit var name: TextView
    private lateinit var createAt: TextView
    private lateinit var description: TextView
    private lateinit var game: Game

    private val gamesViewModel: GamesViewModel by navGraphViewModels(R.id.main_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        game = gamesViewModel.selectedGame!!

        toolbar = view.findViewById(R.id.toolbar)
        toolbarBackground = view.findViewById(R.id.toolbar_background)
        editButton = view.findViewById(R.id.edit_button)
        name = view.findViewById(R.id.name)
        createAt = view.findViewById(R.id.created_at)
        description = view.findViewById(R.id.description)

        FirebaseStorage.getInstance().getReference(Constant.LOCATION)
            .child(game.image).downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(toolbarBackground)
            }
        name.text = game.name
        createAt.text = getString(R.string.launch_year, game.createdAt)
        description.text = game.description

        initToolbar()

        editButton.setOnClickListener {
            findNavController().navigate(R.id.editGameFragment)
        }
    }

    private fun initToolbar() {
        val activity = activity as AppCompatActivity

        toolbar.title = game.name

        activity.setSupportActionBar(toolbar)

        activity.supportActionBar?.setDisplayShowTitleEnabled(true)

        navController = findNavController()
        appBarConfiguration = AppBarConfiguration(navController.graph)

        NavigationUI.setupActionBarWithNavController(activity, navController, appBarConfiguration)
    }
}