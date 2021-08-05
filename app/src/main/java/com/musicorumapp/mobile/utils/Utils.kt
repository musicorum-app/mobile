package com.musicorumapp.mobile.utils

import java.math.BigInteger
import java.security.MessageDigest


class Utils {
    companion object {
        fun md5Hash(string: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(string.toByteArray())).toString(16).padStart(32, '0')
        }

        fun anyToInt(x: Any): Int {
            return when (x) {
                is Int -> x
                is String -> x.toInt()
                is Double -> x.toInt()
                else -> 0
            }
        }
    }
}