package br.com.zup.academy.pix.extension.function

import br.com.zup.academy.KeyPixRequestRemove
import br.com.zup.academy.pix.chave.remocao.KeyPixRemove

fun KeyPixRequestRemove.toModel(): KeyPixRemove {
    return KeyPixRemove(
        pixId = pixId,
        clientId = clientId
    )
}