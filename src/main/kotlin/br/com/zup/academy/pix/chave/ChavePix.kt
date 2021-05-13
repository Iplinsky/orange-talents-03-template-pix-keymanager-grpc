package br.com.zup.academy.pix.chave

import br.com.zup.academy.pix.client.ContaUsuarioItau
import br.com.zup.academy.pix.enumerator.TipoChaveEnum
import br.com.zup.academy.pix.enumerator.TipoContaEnum
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChavePix(
    @field:NotBlank
    @Column(nullable = false)
    val clientId: UUID,

    @field:NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoChavePix: TipoChaveEnum,

    @field:NotBlank
    @Column(nullable = false, unique = true)
    val valorChave: String,


    @field:NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val tipoConta: TipoContaEnum,

    @Embedded
    @field:Valid
    val contaUsuarioItau: ContaUsuarioItau
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
}