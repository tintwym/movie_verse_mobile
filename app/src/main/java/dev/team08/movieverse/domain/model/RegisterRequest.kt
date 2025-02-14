package dev.team08.movieverse.domain.model

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val favoriteGenres: List<String>
)
