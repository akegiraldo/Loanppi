package com.android.loanppi

import android.content.Context
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Function for validate the length, type and content of the fields
fun fieldsValidator(context: Context?, field: EditText, typeValidation: String, lengthMin: Int, lengthMax: Int, required: Boolean): Boolean {
    val onlyNumbers = Regex("[0-9]+")
    val onlyLetters = Regex("[a-zA-ZáéíóúÁÉÍÓÚ]+")
    val homeAddress = Regex("([\\sa-zA-Z0-9#-.]+)")
    val sqlQuery = Regex("DROP|DELETE|UPDATE|SELECT", RegexOption.IGNORE_CASE)
    val textToValidate = field.text.toString()

    if (textToValidate.length < lengthMin && required) {
        Toast.makeText(context, "El campo no puede contener " +
                    "menos de " + lengthMin.toString() + " caracteres.", Toast.LENGTH_LONG).show()
        field.requestFocus()
        return false
    } else if (textToValidate.length > 0 && textToValidate.length < lengthMin && !required) {
        Toast.makeText(context, "El campo no puede contener " +
                    "menos de " + lengthMin.toString() + " caracteres.", Toast.LENGTH_LONG).show()
        field.requestFocus()
        return false
    } else if (textToValidate.length > lengthMax) {
        Toast.makeText(context, "El campo no puede contener " +
                    "más de " + lengthMax.toString() + " caracteres.", Toast.LENGTH_LONG).show()
        field.requestFocus()
        return false
    } else if (sqlQuery.containsMatchIn(textToValidate)) {
        Toast.makeText(context, "No puedes incluir querys,", Toast.LENGTH_LONG).show()
        field.requestFocus()
        return false
    } else {
        when (typeValidation) {
            "onlyNumbers" ->
                if (!onlyNumbers.matches(textToValidate) && textToValidate.length > 0) {
                    Toast.makeText(
                        context, "El campo no puede contener " +
                                "caracteres diferentes a números.", Toast.LENGTH_LONG
                    ).show()
                    field.requestFocus()
                    return false
                }
            "onlyLetters" ->
                if (!onlyLetters.matches(textToValidate) && textToValidate.length > 0) {
                    Toast.makeText(
                        context, "El campo no puede contener " +
                                "caracteres diferentes a letras.", Toast.LENGTH_LONG
                    ).show()
                    field.requestFocus()
                    return false
                }
            "homeAddress" ->
                if (!homeAddress.matches(textToValidate) && textToValidate.length > 0) {
                    Toast.makeText(
                        context, "El campo no puede contener caracteres especiales " +
                                "diferentes a '#' y '-' y punto.", Toast.LENGTH_LONG
                    ).show()
                    field.requestFocus()
                    return false
                }
            else -> {
            }
        }
    }
    return true
}

fun replaceFragment(fragment: Fragment, parentFragmentManager: FragmentManager) {
    val fragmentTransaction = parentFragmentManager.beginTransaction()
    fragmentTransaction.replace(R.id.dashboard_container, fragment)
    fragmentTransaction.addToBackStack(null)
    fragmentTransaction.commit()
}

fun String.toDate(dateFormat: String = "yyyy-MM-dd HH:mm:ss", timeZone: TimeZone = TimeZone.getTimeZone("UTC")): Date {
    val parser = SimpleDateFormat(dateFormat, Locale.getDefault())
    parser.timeZone = timeZone
    return parser.parse(this)
}

fun Date.formatTo(dateFormat: String, timeZone: TimeZone = TimeZone.getDefault()): String {
    val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
    formatter.timeZone = timeZone
    return formatter.format(this)
}

val copFormat: NumberFormat = NumberFormat.getCurrencyInstance()