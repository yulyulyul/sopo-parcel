package team.sopo.common.extension

import java.security.MessageDigest

operator fun String.get(range: IntRange) : String
{
    return this.substring(range)
}

fun sha256(string: String) : String
{
    val bytes = string.toByteArray()
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(bytes)

    return digest.asHex
}
