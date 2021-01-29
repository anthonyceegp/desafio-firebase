package com.anthony.desafiofirebase.utils

import android.content.Context
import android.widget.EditText
import com.anthony.desafiofirebase.R

class ValidationUtils(private val context: Context) {

    fun validateEditText(editText: EditText, trim: Boolean = false): Boolean {
        var isValid = true
        val text = if(trim) editText.text.toString().trim() else editText.text.toString()


        if (text.isEmpty()) {
            editText.error = context.getString(R.string.required_field)
            isValid = false
        }

        return isValid
    }

    fun validateRepeatPassword(
        editText: EditText,
        password: String,
        repeatPassword: String
    ): Boolean {
        var isValid = true
        editText.error = null

        if (password != repeatPassword) {
            editText.error = context.getString(R.string.password_match)
            isValid = false
        }

        return isValid
    }

    fun validatePasswordComplexity(editText: EditText, password: String): Boolean {
        var isValid = true
        val pattern = PASSWORD_PATTERN.toRegex()
        editText.error = null

        if (!pattern.matches(password)) {
            editText.error = context.getString(R.string.password_pattern_error)
            isValid = false
        }

        return isValid
    }

    companion object {
        const val PASSWORD_PATTERN =
            """^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$"""
    }
}