package br.com.zup.academy.pix.client

data class PixDtoResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    fun toModel(): ContaUsuarioItau {
        return ContaUsuarioItau(
            tipo = tipo,
            instituicao = instituicao.nome,
            nomeTitular = titular.nome,
            cpfTitular = titular.cpf,
            agencia = agencia,
            numeroConta = numero
        )
    }
}

// Classe modelo DTO para Instituição
data class InstituicaoResponse(
    val nome: String,
    val ispb: String
) {}

// Classe modelo DTO para Titular
data class TitularResponse(
    val nome: String,
    val cpf: String
) {}
