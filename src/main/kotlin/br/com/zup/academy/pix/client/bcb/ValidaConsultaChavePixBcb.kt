package br.com.zup.academy.pix.client.bcb

import br.com.zup.academy.pix.chave.consulta.KeyPixRetornoConsulta
import br.com.zup.academy.pix.exception.handler.ChavePixInvalidaException
import br.com.zup.academy.pix.exception.handler.ChavePixNaoEncontradaException
import io.micronaut.http.HttpStatus.*
import io.micronaut.http.client.exceptions.HttpClientResponseException
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidaConsultaChavePixBcb(@Inject val bcbClient: BcbClient) {
    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    fun consultarChavePixNoBcb(pixId: String): KeyPixRetornoConsulta? {
        LOGGER.info("Consultando a chave Pix '$pixId' no sistema do Banco Central do Brasil (BCB).")

        val response = try {
            bcbClient.buscarChavePixNoBcbClient(pixId)
        } catch (ex: HttpClientResponseException) {
            throw ex
        }

        return when (response.status) {
            OK -> response.body()?.toModel()
            NOT_FOUND -> throw ChavePixNaoEncontradaException("A chave informada não foi localizada no sistema.")
            BAD_REQUEST -> throw ChavePixInvalidaException("A chave Pix informada é inválida.")
            else -> throw RuntimeException("Erro na conexão com a API externa do Banco Central do Brasil.")
        }
    }
}
