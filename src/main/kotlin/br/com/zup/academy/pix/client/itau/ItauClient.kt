package br.com.zup.academy.pix.client.itau

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client


@Client(value = "\${itau.client.url}")
interface ItauClient {

    @Get("/api/v1/clientes/{clientId}/contas{?tipo}")
    fun consultarContaDoClienteItau(@PathVariable clientId: String, @QueryValue tipo: String): HttpResponse<PixDtoResponse>?
}
