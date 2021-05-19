package br.com.zup.academy.pix.chave.consulta

import br.com.zup.academy.pix.chave.ChavePix
import br.com.zup.academy.pix.chave.TipoChaveEnum
import br.com.zup.academy.pix.chave.TipoContaEnum
import br.com.zup.academy.pix.client.itau.ContaUsuarioItau
import java.time.LocalDateTime
import java.util.*

data class KeyPixRetornoConsulta(
    val pixId: UUID? = null,
    val clienteId: UUID? = null,
    val tipoChave: TipoChaveEnum,
    val valorChave: String,
    val tipoConta: TipoContaEnum,
    val contaUsuario: ContaUsuarioItau,
    val momentoCadastro: LocalDateTime = LocalDateTime.now(),
) {
    companion object {
        fun fill(chave: ChavePix): KeyPixRetornoConsulta {
            return KeyPixRetornoConsulta(
                pixId = chave.id,
                clienteId = chave.clientId,
                tipoChave = chave.tipoChavePix,
                valorChave = chave.valorChave,
                tipoConta = chave.tipoConta,
                contaUsuario = chave.contaUsuarioItau,
                momentoCadastro = chave.momentoCadastro
            )
        }
    }
}