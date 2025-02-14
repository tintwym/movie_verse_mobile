package dev.team08.movieverse.domain.model

import com.google.gson.annotations.SerializedName

data class CastResponse(
    val id: Int,
    val cast: List<Cast>
)
