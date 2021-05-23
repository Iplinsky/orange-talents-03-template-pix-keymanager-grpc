package br.com.zup.academy.pix.client.itau

import io.micronaut.data.annotation.Embeddable

@Embeddable
class ContaUsuarioItau(
    val tipo: String = "",
    val instituicao: String = "",
    val nomeTitular: String = "",
    val cpfTitular: String = "",
    val agencia: String = "",
    val numeroConta: String = "",
) {
    companion object {
        val ITAU_UNIBANCO_ISPB: String = "60701190"
    }
}