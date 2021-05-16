package br.com.zup.academy.pix.client.bcb

import br.com.zup.academy.pix.chave.PixKeyTypeEnum
import java.time.LocalDateTime

data class BcbPixResponse(
    val keyType: PixKeyTypeEnum,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime,
) {
}