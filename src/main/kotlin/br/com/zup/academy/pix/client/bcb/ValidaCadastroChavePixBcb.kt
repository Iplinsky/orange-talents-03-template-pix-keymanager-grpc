package br.com.zup.academy.pix.client.bcb

import br.com.zup.academy.pix.chave.ChavePix
import br.com.zup.academy.pix.exception.handler.ChavePixExistenteException
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidaCadastroChavePixBcb(@Inject val bcbClient: BcbClient) {

    fun cadastrarChavePixNoBcb(chavePix: ChavePix) {
        val response: HttpResponse<BcbPixResponse>
        try {
            response = bcbClient.cadastrarChavePixNoBcbClient(BcbPixRequest.fill(chavePix))
        } catch (ex: HttpClientResponseException) {
            if (HttpStatus.UNPROCESSABLE_ENTITY.code == ex.status.code) {
                throw ChavePixExistenteException(ex.message.toString())
            }
            throw ex
        }

        with(response) {
            if (status != HttpStatus.CREATED)
                throw IllegalStateException("Erro ao registrar a chave PIX no Banco Central do Brasil (BCB)")
        }.apply { chavePix.valorChave = response.body().key }
    }

}
