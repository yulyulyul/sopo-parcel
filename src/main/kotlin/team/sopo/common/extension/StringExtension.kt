package team.sopo.common.extension

import java.security.MessageDigest

val String.asSHA256 get() = SHA256(this)

operator fun String.get(range: IntRange) : String
{
    return this.substring(range)
}

fun SHA256(string: String) : String
{
    val bytes = string.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)

    return digest.asHex
}



fun String.isBirthDayFormat(): Boolean
{
    val birthDayRegex = "^(19[0-9][0-9]|20\\d{2})(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])\$".toRegex()
    val matchResult: MatchResult? = birthDayRegex.find(this)
    return matchResult?.value != null
}