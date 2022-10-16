package io.musicorum.mobile.utils

import java.math.BigInteger
import java.security.MessageDigest

fun md5Hash(string: String): String {
    val md = MessageDigest.getInstance("MD5")
    return BigInteger(1, md.digest(string.toByteArray())).toString(16).padStart(32, '0')
}