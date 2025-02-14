package dev.team08.movieverse.data.repository

import dev.team08.movieverse.domain.model.Genre

object GenreRepository {
    val genres = listOf(
        Genre(28, "Action"),
        Genre(18, "Drama"),
        Genre(35, "Comedy"),
        Genre(99, "Documentary"),
        Genre(27, "Horror"),
        Genre(878, "Science Fiction"),
        Genre(16, "Animation"),
        Genre(14, "Fantasy")
    )
}
