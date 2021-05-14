package br.com.zup.academy.pix.chave

import br.com.zup.academy.KeyPixRequest

fun KeyPixRequest.toModel(): ChavePixRequest {
    return ChavePixRequest(
        clientId = clientId,
        tipoChavePix = TipoChaveEnum.valueOf(tipoChavePix.toString()),
        valorChave = valorChave,
        tipoConta = TipoContaEnum.valueOf(tipoConta.toString())
    )
}