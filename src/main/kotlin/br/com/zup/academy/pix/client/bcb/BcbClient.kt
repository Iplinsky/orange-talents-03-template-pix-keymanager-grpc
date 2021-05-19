package br.com.zup.academy.pix.client.bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import javax.validation.Valid

@Client(value = "\${bcb.client.url}")
interface BcbClient {

    @Post("/api/v1/pix/keys", consumes = [MediaType.APPLICATION_XML], produces = [MediaType.APPLICATION_XML])
    fun cadastrarChavePixNoBcbClient(@Valid @Body bcbPixRequest: BcbPixRequest): HttpResponse<BcbPixResponse>

    @Delete("/api/v1/pix/keys/{key}", consumes = [MediaType.APPLICATION_XML], produces = [MediaType.APPLICATION_XML])
    fun removerChavePixNoBcbClient(@PathVariable key: String, @Body bcbPixDeleteRequest: BcbPixDeleteRequest): HttpResponse<BcbPixDeleteResponse>

    @Get("/api/v1/pix/keys/{key}", consumes = [MediaType.APPLICATION_XML])
    fun buscarChavePixNoBcbClient(@PathVariable key: String): HttpResponse<BcbPixDetailsResponse>
}