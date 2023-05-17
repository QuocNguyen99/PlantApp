package com.example.plantapp.utils

import android.text.TextUtils
import android.util.Patterns
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.regex.Matcher
import java.util.regex.Pattern


fun String.isValidEmail(): Boolean {
    return if (TextUtils.isEmpty(this)) {
        false
    } else {
        Patterns.EMAIL_ADDRESS.matcher(this).matches()
    }
}

fun String.isValidPassword(): Boolean {
    val passWordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?!.*[&%\$]).{11,}\$"
    val pattern = Pattern.compile(passWordPattern)
    val matcher: Matcher = pattern.matcher(this)
    return matcher.matches()
}

fun Timestamp.format(): String {
    val simpleDateFormat = SimpleDateFormat("yyyy . MM . dd")
    return simpleDateFormat.format(toDate())
}