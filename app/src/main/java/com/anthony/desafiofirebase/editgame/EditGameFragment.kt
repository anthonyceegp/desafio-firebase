package com.anthony.desafiofirebase.editgame

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.anthony.desafiofirebase.R
import com.anthony.desafiofirebase.games.model.Game
import com.anthony.desafiofirebase.games.viewmodel.GamesViewModel
import com.anthony.desafiofirebase.utils.Constant
import com.anthony.desafiofirebase.utils.ValidationUtils
import com.google.firebase.storage.FirebaseStorage
import com.mikhaellopez.circularimageview.CircularImageView
import com.squareup.picasso.Picasso

class EditGameFragment : Fragment() {

    private lateinit var validationUtils: ValidationUtils

    private lateinit var game: Game
    private lateinit var uploadButton: CircularImageView
    private lateinit var name: EditText
    private lateinit var createdAt: EditText
    private lateinit var description: EditText
    private lateinit var saveButton: Button
    private var imageURI: Uri? = null

    private val gamesViewModel: GamesViewModel by navGraphViewModels(R.id.main_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validationUtils = ValidationUtils(view.context)

        game = gamesViewModel.selectedGame!!

        uploadButton = view.findViewById(R.id.upload_button)
        name = view.findViewById(R.id.name)
        createdAt = view.findViewById(R.id.created_at)
        description = view.findViewById(R.id.description)
        saveButton = view.findViewById(R.id.save_button)

        FirebaseStorage.getInstance().getReference(Constant.LOCATION)
            .child(game.image).downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(uploadButton)
            }

        name.setText(game.name)
        createdAt.setText(game.createdAt)
        description.setText(game.description)

        uploadButton.setOnClickListener {
            searchFile()
        }

        saveButton.setOnClickListener {
            if (validateFields()) {
                updateGame()
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        isValid = validationUtils.validateEditText(name) && isValid
        isValid = validationUtils.validateEditText(createdAt) && isValid
        isValid = validationUtils.validateEditText(description) && isValid

        return isValid
    }

    private fun searchFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, Constant.UPLOAD_IMAGE_CODE)
    }

    private fun updateGame() {
        if (imageURI != null) {
            gamesViewModel.uploadSuccess.observe(viewLifecycleOwner, { uploadSuccess ->
                if (uploadSuccess) {
                    updateDatabase()
                } else {
                    Toast.makeText(context, "Image upload failed!", Toast.LENGTH_SHORT).show()
                }
            })

            gamesViewModel.uploadImage(imageURI!!, game.image)
        } else {
            updateDatabase()
        }
    }

    private fun updateDatabase() {
        gamesViewModel.updateSuccess.observe(viewLifecycleOwner, { updateSuccess ->
            if (updateSuccess) {
                Toast.makeText(context, "Successfully updated!", Toast.LENGTH_SHORT).show()
                findNavController().popBackStack()
            } else {
                Toast.makeText(context, "Update failed!", Toast.LENGTH_SHORT).show()
            }
        })

        gamesViewModel.updateGame(
            name.text.toString(),
            createdAt.text.toString(),
            description.text.toString()
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constant.UPLOAD_IMAGE_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            imageURI = data?.data
            uploadButton.setImageURI(imageURI)
        }
    }
}