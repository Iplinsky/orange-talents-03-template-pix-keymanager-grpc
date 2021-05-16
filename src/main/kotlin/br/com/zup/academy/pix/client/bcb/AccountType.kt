package br.com.zup.academy.pix.client.bcb

import br.com.zup.academy.pix.chave.TipoContaEnum
import br.com.zup.academy.pix.chave.TipoContaEnum.CONTA_CORRENTE
import br.com.zup.academy.pix.chave.TipoContaEnum.CONTA_POUPANCA

enum class AccountType {
    CACC,
    SVGS;

    companion object {
        fun by(tipoDeconta: TipoContaEnum): AccountType {
            return when (tipoDeconta) {
                CONTA_CORRENTE -> CACC
                CONTA_POUPANCA -> SVGS
            }
        }
    }
}
