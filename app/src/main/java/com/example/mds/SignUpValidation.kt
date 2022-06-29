package com.example.mds


object SignUpValidation {
    private const val EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    /*
    return false if
    username is empty or
    email is empty or
    password is empty or
    password is not greater than 8 characters or
    password does not contain at least 1 upper Letter or
    password does not contain at least 3 digits or
    email is not valid
    */

    fun isValid(username:String, email: String, password: String): Boolean {
        if (username.isEmpty())
            return false
        if (email.isEmpty())
            return false
        if (password.isEmpty())
            return false
        return password.length >= 8 && password.count{it.isUpperCase()} >= 1 &&
                password.count{it.isDigit()} >= 3 && email.matches(EMAIL_PATTERN.toRegex())
    }
}
