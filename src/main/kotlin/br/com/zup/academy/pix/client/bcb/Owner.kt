package br.com.zup.academy.pix.client.bcb

data class Owner(
    val type: TypePersonEnum,
    val name: String,
    val taxIdNumber: String,
) {
}