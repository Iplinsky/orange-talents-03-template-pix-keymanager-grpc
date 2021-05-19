package br.com.zup.academy.pix.chave.remocao

import br.com.zup.academy.pix.chave.ChavePixRepository
import br.com.zup.academy.pix.client.bcb.ValidaRemocaoChavePixBcb
import br.com.zup.academy.pix.exception.handler.ChavePixNaoEncontradaException
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RemoveChaveGrpcService(
    @Inject val chavePixRepository: ChavePixRepository,
    @Inject val validaRemocaoChavePixBcb: ValidaRemocaoChavePixBcb,
) {

    @Transactional
    fun remover(@Valid keyPixRemove: KeyPixRemove) {
        val pixId = UUID.fromString(keyPixRemove.pixId!!)
        val exist = chavePixRepository.existsByIdAndClientId(pixId, UUID.fromString(keyPixRemove.clientId!!))

        if (!exist) throw ChavePixNaoEncontradaException("A chave '$pixId' não foi localizada ou não pertence ao cliente informado.")

        chavePixRepository.deleteById(pixId)
        validaRemocaoChavePixBcb.removerChavePixDoBcb(pixId.toString())
    }
}