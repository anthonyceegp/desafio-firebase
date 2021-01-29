package com.anthony.desafiofirebase.authentication.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.anthony.desafiofirebase.R
import com.anthony.desafiofirebase.utils.ValidationUtils
import com.google.firebase.auth.FirebaseAuth

class LogInFragment : Fragment() {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private lateinit var validationUtils: ValidationUtils

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var remember: CheckBox
    private lateinit var logIn: Button
    private lateinit var createAccount: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        validationUtils = ValidationUtils(view.context)

        email = view.findViewById(R.id.email)
        password = view.findViewById(R.id.password)
        remember = view.findViewById(R.id.remember)
        logIn = view.findViewById(R.id.log_in)
        createAccount = view.findViewById(R.id.create_account)

        logIn.setOnClickListener {
            if (validateFields()) {
                signInWithEmailAndPassword(email.text.toString().trim(), password.text.toString())
            }
        }

        createAccount.setOnClickListener {
            findNavController().navigate(R.id.createAccountFragment)
        }
    }

    private fun validateFields(): Boolean {
        var isValid = true

        isValid = validationUtils.validateEditText(email) && isValid
        isValid = validationUtils.validateEditText(password) && isValid

        return isValid
    }

    private fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    savePreferenceRemember()
                    findNavController().navigate(R.id.mainActivity)
                    requireActivity().finish()
                } else {
                    Toast.makeText(context, R.string.auth_failed, Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun savePreferenceRemember() {
        val sharedPref = activity?.getSharedPreferences(
            getString(R.string.preference_remember),
            Context.MODE_PRIVATE
        ) ?: return

        with(sharedPref.edit()) {
            putBoolean(getString(R.string.remember), remember.isChecked).apply()
        }
    }
}