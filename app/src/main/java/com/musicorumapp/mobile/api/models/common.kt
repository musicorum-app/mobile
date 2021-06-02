package com.musicorumapp.mobile.api.models

import com.musicorumapp.mobile.R

enum class LastfmEntity {
    TRACK, ARTIST, ALBUM, USER;

    fun asDrawableSource(): Int = when (this) {
        TRACK -> R.drawable.ic_placeholder_track
        ARTIST -> R.drawable.ic_placeholder_artist
        ALBUM -> R.drawable.ic_placeholder_album
        USER -> R.drawable.ic_placeholder_user
    }
}