package br.com.zup.academy.pix.chave.remocao

import br.com.zup.academy.pix.annotation.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
class KeyPixRemove(
    @field:NotBlank
    @ValidUUID(message = "Identificador com formato inválido")
    val pixId: String?,

    @field:NotBlank
    @ValidUUID(message = "Identificador com formato inválido")
    val clientId: String?
) {

}
