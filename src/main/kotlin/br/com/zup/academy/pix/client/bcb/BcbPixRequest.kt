package br.com.zup.academy.pix.client.bcb

import br.com.zup.academy.pix.chave.ChavePix
import br.com.zup.academy.pix.chave.PixKeyTypeEnum
import br.com.zup.academy.pix.client.itau.ContaUsuarioItau
import io.micronaut.core.annotation.Introspected
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Introspected
data class BcbPixRequest(
    @field:NotNull
    @Enumerated(EnumType.STRING)
    val keyType: PixKeyTypeEnum,

    @field:NotBlank
    val key: String,

    @field:NotNull
    val bankAccount: BankAccount,

    @field:NotNull
    val owner: Owner,
) {
    companion object {
        fun fill(chavePix: ChavePix): BcbPixRequest {
            return BcbPixRequest(
                keyType = PixKeyTypeEnum.by(chavePix.tipoChavePix),
                key = chavePix.valorChave,
                bankAccount = BankAccount(
                    participant = ContaUsuarioItau.ITAU_UNIBANCO_ISPB,
                    branch = chavePix.contaUsuarioItau.agencia,
                    accountNumber = chavePix.contaUsuarioItau.numeroConta,
                    accountType = AccountType.by(chavePix.tipoConta)
                ),
                owner = Owner(
                    type = Type.NATURAL_PERSON,
                    name = chavePix.contaUsuarioItau.nomeTitular,
                    taxIdNumber = chavePix.contaUsuarioItau.cpfTitular
                )
            )
        }
    }
}