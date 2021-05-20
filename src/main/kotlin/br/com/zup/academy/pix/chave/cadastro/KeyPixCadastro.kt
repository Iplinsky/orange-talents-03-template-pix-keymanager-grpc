package br.com.zup.academy.pix.chave.cadastro

import br.com.zup.academy.pix.annotation.PixKey
import br.com.zup.academy.pix.annotation.ValidUUID
import br.com.zup.academy.pix.chave.ChavePix
import br.com.zup.academy.pix.chave.TipoChaveEnum
import br.com.zup.academy.pix.chave.TipoContaEnum
import br.com.zup.academy.pix.client.itau.ContaUsuarioItau
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@PixKey
@Introspected
class KeyPixCadastro(
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
            valorChave = if (this.tipoChavePix == TipoChaveEnum.ALEATORIA) UUID.randomUUID().toString() else this.valorChave,
            tipoConta = TipoContaEnum.valueOf(this.tipoConta.name),
            contaUsuarioItau = conta
        )
    }
}