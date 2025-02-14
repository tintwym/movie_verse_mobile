package dev.team08.movieverse.domain.model

import java.util.UUID

data class FeedbackRequest(
    val userId: UUID,
    val projectId: Long,
    val rating: Int,
    val reviewContent: String
)
