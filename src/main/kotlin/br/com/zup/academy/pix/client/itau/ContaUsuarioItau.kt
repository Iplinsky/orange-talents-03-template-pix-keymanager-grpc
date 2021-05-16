package br.com.zup.academy.pix.client.itau

import io.micronaut.core.annotation.Introspected
import io.micronaut.data.annotation.Embeddable
import javax.persistence.Column
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Embeddable
@Introspected
class ContaUsuarioItau(
    @field:NotBlank
    val tipo: String = "",

    @field:NotBlank
    val instituicao: String = "",

    @field:NotBlank
    val nomeTitular: String = "",

    @field:NotBlank
    @field:Size(max = 11)
    @Column(length = 11, nullable = false)
    val cpfTitular: String = "",

    @field:NotBlank
    @field:Size(max = 4)
    @Column(length = 4, nullable = false)
    val agencia: String = "",

    @field:NotBlank
    @field:Size(max = 6)
    @Column(length = 6, nullable = false)
    val numeroConta: String = "",
) {
    companion object {
        public val ITAU_UNIBANCO_ISPB: String = "60701190"
    }
}