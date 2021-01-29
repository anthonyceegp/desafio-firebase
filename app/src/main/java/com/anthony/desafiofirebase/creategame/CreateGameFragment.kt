package com.anthony.desafiofirebase.creategame

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
import com.anthony.desafiofirebase.games.viewmodel.GamesViewModel
import com.anthony.desafiofirebase.utils.Constant
import com.anthony.desafiofirebase.utils.ValidationUtils
import com.mikhaellopez.circularimageview.CircularImageView
import java.text.SimpleDateFormat
import java.util.*


class CreateGameFragment : Fragment() {

    private lateinit var validationUtils: ValidationUtils

    private lateinit var name: EditText
    private lateinit var createdAt: EditText
    private lateinit var description: EditText
    private lateinit var uploadButton: CircularImageView
    private lateinit var saveButton: Button
    private var imageURI: Uri? = null

    private val gamesViewModel: GamesViewModel by navGraphViewModels(R.id.main_nav)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_game, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validationUtils = ValidationUtils(view.context)

        name = view.findViewById(R.id.name)
        createdAt = view.findViewById(R.id.created_at)
        description = view.findViewById(R.id.description)
        uploadButton = view.findViewById(R.id.upload_button)
        saveButton = view.findViewById(R.id.save_button)

        uploadButton.setOnClickListener {
            searchFile()
        }

        saveButton.setOnClickListener {
            if (validateFields()) {
                createGame()
            }
        }

    }

    private fun validateFields(): Boolean {
        var isValid = true

        isValid = validationUtils.validateEditText(name) && isValid
        isValid = validationUtils.validateEditText(createdAt) && isValid
        isValid = validationUtils.validateEditText(description) && isValid

        if (imageURI == null) {
            Toast.makeText(context, getString(R.string.image_required), Toast.LENGTH_SHORT).show()
            isValid = false
        }

        return isValid
    }

    private fun searchFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, Constant.UPLOAD_IMAGE_CODE)
    }

    private fun createGame() {
        val dateTime = SimpleDateFormat(
            Constant.PATTERN_yyyyMMddHHmmssSSSS,
            Locale.getDefault()
        ).format(Date())
        val pathString = dateTime.toString()

        gamesViewModel.uploadSuccess.observe(viewLifecycleOwner, { uploadSuccess ->
            if (uploadSuccess) {
                gamesViewModel.createSuccess.observe(viewLifecycleOwner, { createSuccess ->
                    if (createSuccess) {
                        Toast.makeText(context, "Successfully created!", Toast.LENGTH_SHORT).show()
                        findNavController().popBackStack()
                    } else {
                        Toast.makeText(context, "Create failed!", Toast.LENGTH_SHORT).show()
                    }
                })

                gamesViewModel.createGame(
                    pathString,
                    name.text.toString(),
                    createdAt.text.toString(),
                    description.text.toString()
                )
            } else {
                Toast.makeText(context, "Image upload failed!", Toast.LENGTH_SHORT).show()
            }
        })

        gamesViewModel.uploadImage(imageURI!!, pathString)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constant.UPLOAD_IMAGE_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            imageURI = data?.data
            uploadButton.setImageURI(imageURI)
        }
    }
}