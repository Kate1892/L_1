package com.example.kotlinexemple.extentions

import androidx.annotation.VisibleForTesting
import java.util.*

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String, email: String, password: String
    ): User {
        return when {
            !map.containsKey(email.lowercase(Locale.getDefault())) -> User.makeUser(
                fullName, email = email, password = password
            ).also { user -> map[user.login] = user }
            else -> throw IllegalArgumentException("A user with this email already exists")
        }
    }

    fun registerUserByPhone(fullName: String, rawPhone: String): User {
        return when {
            !isPhoneValid(rawPhone) -> throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
            !map.containsKey(rawPhone.replace("""[^+\d]""".toRegex(), "")) -> User.makeUser(
                fullName, phone = rawPhone
            ).also { user -> map[user.login] = user }
            else -> throw IllegalArgumentException("A user with this phone already exists")
        }
    }

    private fun isPhoneValid(rawPhone: String): Boolean =
        (rawPhone.replace(""""\s""".toRegex(), "").first()
            .toString() == "+" && rawPhone.replace("""[+\s]""".toRegex(), "")
            .all { it.isDigit() || !it.isLetter() } && rawPhone.replace(
            """\D""".toRegex(), ""
        ).length == 11)

    fun loginUser(login: String, password: String): String? {
        val formattedLogin: String =
            if (isPhoneValid(login)) login.replace("""[^+\d]""".toRegex(), "") else login.trim()
        return map[formattedLogin]?.let {
            if (it.checkPassword(password)) it.userInfo
            else null
        }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() {
        map.clear()
    }

    fun requestAccessCode(login: String): Unit {
        val formattedLogin: String =
            if (isPhoneValid(login)) login.replace("""[^+\d]""".toRegex(), "") else login.trim()
        map[formattedLogin]?.let {
            it.updateUsersAccessCode(it);
        }
    }
}