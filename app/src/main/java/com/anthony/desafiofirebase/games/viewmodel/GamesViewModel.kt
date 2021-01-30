package com.anthony.desafiofirebase.games.viewmodel

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.anthony.desafiofirebase.games.model.Game
import com.anthony.desafiofirebase.utils.Constant
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class GamesViewModel : ViewModel() {
    private var userId = FirebaseAuth.getInstance().currentUser!!.uid
    private var storageReference = FirebaseStorage.getInstance().getReference(Constant.LOCATION)
    private var databaseReference = FirebaseDatabase
        .getInstance()
        .getReference(userId)
        .child(Constant.GAMES)

    private val gamesAux = mutableListOf<Game>()
    private var filteredGames = listOf<Game>()

    var selectedGame: Game? = null
    var textFilter: String? = null

    val games = MutableLiveData<List<Game>>()
    val error = MutableLiveData<DatabaseError>()
    val uploadSuccess = MutableLiveData<Boolean>()
    val updateSuccess = MutableLiveData<Boolean>()
    val createSuccess = MutableLiveData<Boolean>()


    fun getGames() {
        if (gamesAux.isEmpty()) {
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    gamesAux.clear()
                    snapshot.children.forEach {
                        val game = it.getValue(Game::class.java) as Game
                        gamesAux.add(game)
                    }
                    games.value = gamesAux
                }

                override fun onCancelled(e: DatabaseError) {
                    error.value = e
                }
            })
        } else {
            games.value = if (textFilter.isNullOrBlank()) {
                gamesAux
            } else {
                filteredGames
            }
        }
    }

    fun createGame(imageUrl: String, name: String, createdAt: String, description: String) {
        val myRef = databaseReference.push()
        val game = Game(myRef.key!!, imageUrl, name, createdAt, description)
        myRef.setValue(game)
            .addOnSuccessListener {
                gamesAux.add(game)
                createSuccess.value = true
            }
            .addOnFailureListener {
                createSuccess.value = false
            }
    }

    fun uploadImage(uri: Uri, pathString: String) {
        storageReference.child(pathString).putFile(uri)
            .addOnSuccessListener {
                uploadSuccess.value = true
            }
            .addOnFailureListener {
                uploadSuccess.value = false
            }
    }

    fun updateGame(name: String, createdAt: String, description: String) {
        val game = selectedGame!!.copy(
            name = name,
            createdAt = createdAt,
            description = description
        )

        databaseReference.child(game.key).setValue(game)
            .addOnSuccessListener {
                selectedGame!!.name = name
                selectedGame!!.createdAt = createdAt
                selectedGame!!.description = description

                updateSuccess.value = true
            }
            .addOnFailureListener {
                updateSuccess.value = false
            }
    }

    fun applyFilter(text: String?) {
        if (textFilter != text) {
            textFilter = text
            filteredGames = if (textFilter.isNullOrBlank()) {
                gamesAux
            } else {
                gamesAux.filter { g -> g.name.startsWith(textFilter!!, true) }
            }

            games.value = filteredGames
        }
    }
}