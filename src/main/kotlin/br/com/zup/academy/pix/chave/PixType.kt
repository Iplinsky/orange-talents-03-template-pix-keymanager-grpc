package br.com.zup.academy.pix.chave

import br.com.zup.academy.pix.annotation.ValidUUID
import br.com.zup.academy.pix.chave.consulta.KeyPixRetornoConsulta
import br.com.zup.academy.pix.client.bcb.BcbClient
import br.com.zup.academy.pix.client.bcb.ValidaConsultaChavePixBcb
import br.com.zup.academy.pix.exception.handler.ChavePixNaoEncontradaException
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class PixType {

    abstract fun retrieve(chavePixRepository: ChavePixRepository, bcbClient: BcbClient): KeyPixRetornoConsulta

    @Introspected
    data class PixIdModel(
        @field:[NotBlank ValidUUID] val clientId: String,
        @field:[NotBlank ValidUUID] val pixId: String,
    ) : PixType() {

        override fun retrieve(chavePixRepository: ChavePixRepository, bcbClient: BcbClient): KeyPixRetornoConsulta {
            return chavePixRepository
                .findById(UUID.fromString(pixId))
                .filter { it.pertenceAoCliente(UUID.fromString(clientId)) }
                .map(KeyPixRetornoConsulta::fill)
                .orElseThrow { ChavePixNaoEncontradaException("A chave Pix informada não foi localizada no sistema.") }
        }
    }

    @Introspected
    data class PixKeyModel(@field:NotBlank @Size(max = 77) val chave: String) : PixType() {
        override fun retrieve(chavePixRepository: ChavePixRepository, bcbClient: BcbClient): KeyPixRetornoConsulta {
            return chavePixRepository.findByValorChave(chave)
                .map(KeyPixRetornoConsulta::fill)
                .orElseGet {
                    ValidaConsultaChavePixBcb(bcbClient).consultarChavePixNoBcb(chave)
                }
        }
    }

    @Introspected
    class Invalid() : PixType() {
        override fun retrieve(chavePixRepository: ChavePixRepository, bcbClient: BcbClient): KeyPixRetornoConsulta {
            throw IllegalArgumentException("A chave Pix informada é inválida!")
        }
    }
}