package com.anthony.desafiofirebase.games.view

import android.app.Activity.RESULT_OK
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognizerIntent
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anthony.desafiofirebase.R
import com.anthony.desafiofirebase.games.model.Game
import com.anthony.desafiofirebase.games.viewmodel.GamesViewModel
import com.anthony.desafiofirebase.utils.Constant
import com.anthony.desafiofirebase.utils.GridLayoutDecoration
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class GamesFragment : Fragment(), OnGameClickListener {
    private lateinit var micButton: ImageButton
    private lateinit var searchInput: EditText

    private lateinit var recyclerView: RecyclerView
    private lateinit var gamesAdapter: GamesAdapter
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var createButton: FloatingActionButton
    private lateinit var signOutButton: FloatingActionButton

    private lateinit var handler: Handler

    private val gamesViewModel: GamesViewModel by navGraphViewModels(R.id.main_nav)
    private var games = mutableListOf<Game>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_games, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        micButton = view.findViewById(R.id.mic_button)
        micButton.setOnClickListener {
            openMic()
        }

        initSearchInput(view)
        initRecyclerView(view)

        gamesViewModel.getGames()

        createButton = view.findViewById(R.id.create_button)
        createButton.setOnClickListener {
            findNavController().navigate(R.id.createGameFragment)
        }

        signOutButton = view.findViewById((R.id.sign_out_button))
        signOutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            findNavController().navigate(R.id.authenticationActivity)
            requireActivity().finish()
        }
    }

    private fun initSearchInput(view: View) {
        searchInput = view.findViewById(R.id.search_input)
        searchInput.setText(gamesViewModel.textFilter)

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeMessages(Constant.HANDLER_MESSAGE)
                handler.sendEmptyMessageDelayed(
                    Constant.HANDLER_MESSAGE,
                    Constant.HANDLER_DELAY
                )
            }

            override fun afterTextChanged(s: Editable?) {
            }

        })

        handler = Handler(Looper.myLooper()!!) { msg ->
            if (msg.what == Constant.HANDLER_MESSAGE) {
                gamesViewModel.applyFilter(searchInput.text.toString())
            }
            false
        }
    }

    private fun initRecyclerView(view: View) {
        recyclerView = view.findViewById(R.id.games_recycler_view)

        gamesViewModel.games.observe(viewLifecycleOwner, {
            games.clear()
            games.addAll(it)
            gamesAdapter.notifyDataSetChanged()
        })

        gridLayoutManager = GridLayoutManager(view.context, 2)
        gamesAdapter = GamesAdapter(games, this)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = gamesAdapter
        recyclerView.addItemDecoration(GridLayoutDecoration(2, 30, true))
    }

    override fun onGameClick(position: Int) {
        gamesViewModel.selectedGame = games[position]
        findNavController().navigate(R.id.gameDetailFragment)
    }

    private fun openMic() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speak_now))

        try {
            startActivityForResult(intent, Constant.REQ_CODE_SPEECH_OUTPUT)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, getString(R.string.speech_not_available), Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constant.REQ_CODE_SPEECH_OUTPUT && resultCode == RESULT_OK && data != null) {
            val voiceInText = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            val text = voiceInText?.get(0)
            searchInput.setText(text)
        }
    }
}