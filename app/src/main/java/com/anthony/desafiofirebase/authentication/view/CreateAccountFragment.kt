package com.anthony.desafiofirebase.authentication.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anthony.desafiofirebase.R
import com.anthony.desafiofirebase.utils.ValidationUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class CreateAccountFragment : Fragment() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var validationUtils: ValidationUtils

    private lateinit var name: EditText
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var repeatPassword: EditText
    private lateinit var createAccount: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validationUtils = ValidationUtils(view.context)

        name = view.findViewById(R.id.name)
        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        repeatPassword = view.findViewById(R.id.repeat_password)
        createAccount = view.findViewById(R.id.create_account)

        createAccount.setOnClickListener {
            if (validateFields()) {
                createUserWithEmailAndPassword(
                    email.text.toString().trim(),
                    password.text.toString()
                )
            }
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        isValid = validationUtils.validateEditText(name, true) && isValid
        isValid = validationUtils.validateEditText(email, true) && isValid
        isValid = validationUtils.validateEditText(password) && isValid
        isValid = validationUtils.validateEditText(repeatPassword) && isValid

        val passwordText = password.text.toString()
        val repeatPasswordText = repeatPassword.text.toString()

        if (passwordText.isNotEmpty() && repeatPasswordText.isNotBlank()) {
            isValid = validationUtils.validateRepeatPassword(
                repeatPassword,
                passwordText,
                repeatPasswordText
            ) && isValid
        }

        if (passwordText.isNotEmpty()) {
            isValid =
                validationUtils.validatePasswordComplexity(
                    password,
                    passwordText
                ) && isValid
        }

        return isValid
    }

    private fun createUserWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser

                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name.text.toString().trim()).build()
                    user!!.updateProfile(profileUpdates)

                    findNavController().navigate(R.id.mainActivity)
                    requireActivity().finish()
                } else {
                    Toast.makeText(context, getString(R.string.auth_failed), Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}