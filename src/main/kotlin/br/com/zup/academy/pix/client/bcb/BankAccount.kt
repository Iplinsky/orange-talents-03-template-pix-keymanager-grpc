package br.com.zup.academy.pix.client.bcb

data class BankAccount(
    /**
     * (participant) - ISPB (Identificador de Sistema de Pagamento Brasileiro) do ITAÚ UNIBANCO S.A --> [60701190].
     **/
    val participant: String,
    val branch: String,
    val accountNumber: String,
    val accountType: AccountType,
) {
}


