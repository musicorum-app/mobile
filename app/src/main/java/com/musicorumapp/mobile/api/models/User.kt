package com.musicorumapp.mobile.api.models

import com.musicorumapp.mobile.utils.Utils

data class UserResponse(
    val playcount: Any,
    val name: String,
    val country: String?,
    val realname: String?,

    val image: List<ImageResourceSerializable>
) {

    fun toUser(): User {
        return User(
            name = realname,
            country = country,
            images = LastfmImages.fromSerializable(image, LastfmEntity.USER),
            playCount = Utils.anyToInt(playcount),
            userName = name
        )
    }
}

    data class User(
        val playCount: Int,
        val userName: String,
        val name: String? = null,
        val country: String?,
        val images: LastfmImages
    ) {
        val displayName: String
            get() = name ?: userName


        companion object {
            fun fromSample(): User {
                return User(
                    userName = "metye",
                    name = "metehus",
                    playCount = 26328,
                    country = "Brazil",
                    images = LastfmImages(
                        images = listOf(
                            "https://lastfm.freetls.fastly.net/i/u/300x300/d0d749fa16e88ce5c452283a37ec7516.png",
                            "https://lastfm.freetls.fastly.net/i/u/300x300/d0d749fa16e88ce5c452283a37ec7516.png",
                            "https://lastfm.freetls.fastly.net/i/u/300x300/d0d749fa16e88ce5c452283a37ec7516.png",
                            "https://lastfm.freetls.fastly.net/i/u/300x300/d0d749fa16e88ce5c452283a37ec7516.png",
                            "https://lastfm.freetls.fastly.net/i/u/300x300/d0d749fa16e88ce5c452283a37ec7516.png",
                            "https://lastfm.freetls.fastly.net/i/u/300x300/d0d749fa16e88ce5c452283a37ec7516.png"
                        ),
                        LastfmEntity.USER
                    )
                )
            }
        }
    }