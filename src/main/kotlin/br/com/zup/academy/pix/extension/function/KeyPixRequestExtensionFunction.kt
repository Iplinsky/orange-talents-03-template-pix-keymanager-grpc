package br.com.zup.academy.pix.extension.function

import br.com.zup.academy.KeyPixRequestCadastro
import br.com.zup.academy.KeyPixRequestRemove
import br.com.zup.academy.pix.chave.cadastro.KeyPixCadastro
import br.com.zup.academy.pix.chave.TipoChaveEnum
import br.com.zup.academy.pix.chave.TipoContaEnum
import br.com.zup.academy.pix.chave.remocao.KeyPixRemove

// CADASTRO TO MODEL
fun KeyPixRequestCadastro.toModel(): KeyPixCadastro {
    return KeyPixCadastro(
        clientId = clientId,
        tipoChavePix = TipoChaveEnum.valueOf(tipoChavePix.toString()),
        valorChave = valorChave,
        tipoConta = TipoContaEnum.valueOf(tipoConta.toString())
    )
}

// REMOVE TO MODEL
fun KeyPixRequestRemove.toModel(): KeyPixRemove {
    return KeyPixRemove(
        pixId = pixId,
        clientId = clientId
    )
}