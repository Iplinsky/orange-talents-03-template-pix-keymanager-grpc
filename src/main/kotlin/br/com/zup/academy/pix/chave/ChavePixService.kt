package br.com.zup.academy.pix.chave

import br.com.zup.academy.pix.client.ContaUsuarioItau
import br.com.zup.academy.pix.client.ItauClient
import br.com.zup.academy.pix.client.ValidaComunicacaoErpItau
import br.com.zup.academy.pix.exception.handler.ChavePixExistenteException
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid
import javax.validation.constraints.NotNull

@Validated
@Singleton
class ChavePixService(
    @Inject val pixRepository: ChavePixRepository,
    @Inject val itauClient: ItauClient,
    @Inject val validaComunicacaoErpItau: ValidaComunicacaoErpItau
) {
    @Transactional
    fun cadastrarChavePix(@Valid pixDtoRequest: ChavePixRequest): @Valid @NotNull ChavePix {

        if (pixRepository.existsByValorChave(pixDtoRequest.valorChave)) {
            throw ChavePixExistenteException("A chave '${pixDtoRequest.valorChave}' já existe.")
        }

        /* Comunica com o sistema ERP do Itau */
        var contaUsuario: ContaUsuarioItau = validaComunicacaoErpItau.comunicar(pixDtoRequest)

        val chavePix: ChavePix = pixDtoRequest.toModel(contaUsuario)
        pixRepository.save(chavePix)

        return chavePix
    }

}
