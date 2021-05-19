package br.com.zup.academy.pix.client.bcb

import br.com.zup.academy.pix.chave.PixKeyTypeEnum
import br.com.zup.academy.pix.chave.TipoContaEnum
import br.com.zup.academy.pix.chave.consulta.KeyPixRetornoConsulta
import br.com.zup.academy.pix.client.itau.ContaUsuarioItau
import java.time.LocalDateTime

data class BcbPixDetailsResponse(
    val keyType: PixKeyTypeEnum,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime,
) {

    fun toModel(): KeyPixRetornoConsulta {
        return KeyPixRetornoConsulta(
            tipoChave = keyType.domainType!!,
            valorChave = this.key,
            tipoConta = when (this.bankAccount.accountType) {
                AccountType.CACC -> TipoContaEnum.CONTA_CORRENTE
                AccountType.SVGS -> TipoContaEnum.CONTA_POUPANCA
            },
            contaUsuario = ContaUsuarioItau(
                instituicao = bankAccount.participant,
                nomeTitular = owner.name,
                cpfTitular = owner.taxIdNumber,
                agencia = bankAccount.branch,
                numeroConta = bankAccount.accountNumber
            )
        )
    }
}
