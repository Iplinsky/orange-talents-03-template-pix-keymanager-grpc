package br.com.zup.academy.pix.client

import io.micronaut.data.annotation.Embeddable

@Embeddable
class ContaUsuarioItau(
    val tipo: String = "",
    val instituicao: String = "",
    val nomeTitular: String = "",
    val cpfTitular: String = "",
    val agencia: String = "",
    val numeroConta: String = ""
) {
}