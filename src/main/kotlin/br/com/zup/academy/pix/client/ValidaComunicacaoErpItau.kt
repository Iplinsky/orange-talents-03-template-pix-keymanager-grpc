package br.com.zup.academy.pix.client

import br.com.zup.academy.pix.chave.cadastro.KeyPixCadastro
import br.com.zup.academy.pix.exception.handler.ClientNotFoundException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ValidaComunicacaoErpItau(@Inject val itauClient: ItauClient) {

    fun comunicar(pixDtoCadastro: KeyPixCadastro): ContaUsuarioItau {
        val consultarContaDoClienteItau = itauClient.consultarContaDoClienteItau(pixDtoCadastro.clientId!!, pixDtoCadastro.tipoConta!!.name)

        return consultarContaDoClienteItau?.body()?.toModel()
            ?: throw ClientNotFoundException("Cliente não localizado no banco Itau.")
    }
}
