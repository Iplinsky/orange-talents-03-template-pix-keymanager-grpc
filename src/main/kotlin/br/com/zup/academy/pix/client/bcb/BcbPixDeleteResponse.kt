package br.com.zup.academy.pix.client.bcb

import java.time.LocalDateTime

data class BcbPixDeleteResponse(
    val key: String,
    val participant: String,
    val deletedAt: LocalDateTime
)