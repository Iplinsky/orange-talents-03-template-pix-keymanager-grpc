package br.com.zup.academy.pix.extension.function

import br.com.zup.academy.KeyPixRequestConsulta
import br.com.zup.academy.KeyPixRequestConsulta.SearchTypeCase.*
import br.com.zup.academy.pix.chave.PixType
import javax.validation.ConstraintViolationException
import javax.validation.Validator

fun KeyPixRequestConsulta.toModel(validator: Validator): PixType {

    val filtro = when (searchTypeCase) {
        PIXID -> pixId.let { PixType.PixIdModel(clientId = it.clientId, pixId = it.pixId) }
        CHAVE -> PixType.PixKeyModel(chave)
        SEARCHTYPE_NOT_SET -> PixType.Invalid()
    }

    val violations = validator.validate(filtro)
    if (violations.isNotEmpty()) {
        throw ConstraintViolationException(violations)
    }

    return filtro
}