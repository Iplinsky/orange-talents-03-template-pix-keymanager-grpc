package br.com.zup.academy.pix.extension.function

import br.com.zup.academy.KeyPixRequestCadastro
import br.com.zup.academy.pix.chave.TipoChaveEnum
import br.com.zup.academy.pix.chave.TipoContaEnum
import br.com.zup.academy.pix.chave.cadastro.KeyPixCadastro

fun KeyPixRequestCadastro.toModel(): KeyPixCadastro {
    return KeyPixCadastro(
        clientId = clientId,
        tipoChavePix = TipoChaveEnum.valueOf(tipoChavePix.toString()),
        valorChave = valorChave,
        tipoConta = TipoContaEnum.valueOf(tipoConta.toString())
    )
}