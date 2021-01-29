package com.anthony.desafiofirebase.games.model

import java.io.Serializable

data class Game(
    val key: String = "",
    val image: String = "",
    var name: String = "",
    var createdAt: String = "",
    var description: String = ""
) : Serializable
