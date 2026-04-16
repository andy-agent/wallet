package com.app.data.remote.dto

import com.app.data.model.LegalDocument

data class LegalDocumentDto(
    val id: String,
    val title: String,
    val summary: String,
    val content: String,
)

fun LegalDocumentDto.toModel() = LegalDocument(id, title, summary, content)
