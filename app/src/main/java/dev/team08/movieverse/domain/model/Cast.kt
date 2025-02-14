package dev.team08.movieverse.domain.model

import com.google.gson.annotations.SerializedName

data class Cast(
    val id: Int,
    val name: String,
    val character: String,
    val profile_path: String?
)
