package br.com.zup.academy.pix.client.bcb

import br.com.zup.academy.pix.client.itau.ContaUsuarioItau

data class BcbPixDeleteRequest(
    val key: String,
    val participant: String = ContaUsuarioItau.ITAU_UNIBANCO_ISPB,
) {
}
