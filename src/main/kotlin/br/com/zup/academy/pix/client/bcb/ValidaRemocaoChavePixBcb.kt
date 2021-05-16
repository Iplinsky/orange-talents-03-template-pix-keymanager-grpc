package br.com.zup.academy.pix.client.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus.OK
import io.micronaut.http.client.exceptions.HttpClientResponseException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidaRemocaoChavePixBcb(@Inject val bcbClient: BcbClient) {

    fun removerChavePixDoBcb(pixId: String) {
        val response: HttpResponse<BcbPixDeleteResponse>

        try {
            response = bcbClient.removerChavePixNoBcbClient(pixId, BcbPixDeleteRequest(pixId))
        } catch (ex: HttpClientResponseException) {
            throw  ex
        }
        with(response) {
            (status != OK)
                ?: throw IllegalStateException("Erro ao remover a chave PIX no sistema do Banco Central do Brasil (BCB)")
        }
    }
}