package com.musicorumapp.mobile.utils

import java.math.BigInteger
import java.security.MessageDigest
import kotlin.reflect.typeOf


class Utils {
    companion object {
        fun md5Hash(string: String): String {
            val md = MessageDigest.getInstance("MD5")
            return BigInteger(1, md.digest(string.toByteArray())).toString(16).padStart(32, '0')
        }

        fun anyToInt(x: Any): Int {
            if (x is Int) {
                return x
            } else if (x is String) {
                return x.toInt()
            }
            return 0
        }
    }
}