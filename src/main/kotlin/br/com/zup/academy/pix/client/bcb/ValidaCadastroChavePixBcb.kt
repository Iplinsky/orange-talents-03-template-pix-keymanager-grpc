package br.com.zup.academy.pix.client.bcb

import br.com.zup.academy.pix.chave.ChavePix
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidaCadastroChavePixBcb(@Inject val bcbClient: BcbClient) {

    fun comunicar(chavePix: ChavePix) {
        val response: HttpResponse<BcbPixResponse>
        try {
            response = bcbClient.cadastrarChavePixNoBcbClient(BcbPixRequest.fill(chavePix))
        } catch (ex: HttpClientResponseException) {
            throw ex
        }

        with(response) {
            (status != HttpStatus.CREATED)
                ?: throw IllegalStateException("Erro ao registrar a chave PIX no Banco Central do Brasil (BCB)")
        }.apply { chavePix.valorChave = response.body().key }
    }

}
