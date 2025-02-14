package dev.team08.movieverse.domain.model

data class Review(
    val id: String = "",
    val originalReviewText: String,
    val createdAt: String,
    val updatedAt: String,
    val user: User,
    val tmdbMovieId: Int,
    val editedReviewText: String? = null,
    val reviewSentiment: String? = null,
    val edited: Boolean = false
)
