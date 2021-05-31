package com.musicorumapp.mobile.api.interceptors


/**
 * Last.fm signed request.
 * See https://www.last.fm/api/authspec#_8-signing-calls
 */
@Target(AnnotationTarget.FUNCTION)
annotation class SignedRequest()
