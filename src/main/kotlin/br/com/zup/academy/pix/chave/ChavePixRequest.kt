package br.com.zup.academy.pix.chave

import br.com.zup.academy.pix.annotation.PixKey
import br.com.zup.academy.pix.annotation.ValidUUID
import br.com.zup.academy.pix.client.ContaUsuarioItau
import br.com.zup.academy.pix.enumerator.TipoChaveEnum
import br.com.zup.academy.pix.enumerator.TipoContaEnum
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@PixKey
@Introspected
data class ChavePixRequest(
    @ValidUUID
    @field:NotBlank
    val clientId: String,

    @field:NotNull
    val tipoChavePix: TipoChaveEnum,

    @field:NotBlank
    @field:Size(max = 77)
    val valorChave: String,

    @field:NotNull
    val tipoConta: TipoContaEnum
) {
    fun toModel(conta: ContaUsuarioItau): ChavePix {
        return ChavePix(
            clientId = UUID.fromString(clientId),
            tipoChavePix = TipoChaveEnum.valueOf(this.tipoChavePix.name),
            valorChave = if(this.tipoChavePix == TipoChaveEnum.CHAVE_ALEATORIA) UUID.randomUUID().toString() else this.valorChave,
            tipoConta = TipoContaEnum.valueOf(this.tipoConta.name),
            contaUsuarioItau = conta
        )
    }
}