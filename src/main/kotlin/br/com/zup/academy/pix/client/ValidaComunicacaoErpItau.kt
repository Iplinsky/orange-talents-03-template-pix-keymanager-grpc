package br.com.zup.academy.pix.client

import br.com.zup.academy.pix.chave.ChavePixRequest
import br.com.zup.academy.pix.exception.handler.ClientNotFoundException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidaComunicacaoErpItau(@Inject val itauClient: ItauClient) {

    fun comunicar(pixDtoRequest: ChavePixRequest): ContaUsuarioItau {
        val consultarContaDoClienteItau = itauClient.consultarContaDoClienteItau(pixDtoRequest.clientId!!, pixDtoRequest.tipoConta!!.name)

        return consultarContaDoClienteItau?.body()?.toModel()
            ?: throw ClientNotFoundException("Cliente não localizado no banco Itau.")
    }
}
