package br.com.zup.academy.pix.chave.cadastro

import br.com.zup.academy.pix.chave.ChavePix
import br.com.zup.academy.pix.chave.ChavePixRepository
import br.com.zup.academy.pix.client.bcb.ValidaCadastroChavePixBcb
import br.com.zup.academy.pix.client.itau.ContaUsuarioItau
import br.com.zup.academy.pix.client.itau.ValidaComunicacaoErpItau
import br.com.zup.academy.pix.exception.handler.ChavePixExistenteException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotNull

@Validated
@Singleton
class CadastraChaveGrpcService(
    @Inject val pixRepository: ChavePixRepository,
    @Inject val validaComunicacaoErpItau: ValidaComunicacaoErpItau,
    @Inject val validaCadastroChavePixBcb: ValidaCadastroChavePixBcb,
) {
    @Transactional
    fun cadastrarChavePix(@Valid pixDtoCadastro: KeyPixCadastro): @Valid @NotNull ChavePix {

        if (pixRepository.existsByValorChave(pixDtoCadastro.valorChave)) {
            throw ChavePixExistenteException("A chave '${pixDtoCadastro.valorChave}' já existe.")
        }
        /**
         * Comunica com o sistema ERP do Itau retornando a conta do usuário ou então uma exceção caso o usuário não exista na base de dados
         **/
        var contaUsuario: ContaUsuarioItau = validaComunicacaoErpItau.comunicar(pixDtoCadastro)
        val chavePix: ChavePix = pixDtoCadastro.toModel(contaUsuario)

        pixRepository.save(chavePix)

        validaCadastroChavePixBcb.cadastrarChavePixNoBcb(chavePix).also {
            println("Realizando o registro global da chave PIX no sistema do Banco Central do Brasil (BCB).")
        }
        return chavePix
    }

}
