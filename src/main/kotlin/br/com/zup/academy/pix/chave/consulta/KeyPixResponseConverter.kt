package br.com.zup.academy.pix.chave.consulta

import br.com.zup.academy.KeyPixResponseConsulta
import br.com.zup.academy.TipoChave
import br.com.zup.academy.TipoConta
import com.google.protobuf.Timestamp
import java.time.ZoneId

class KeyPixResponseConverter {
    fun convert(keyPixRetorno: KeyPixRetornoConsulta): KeyPixResponseConsulta {
        return KeyPixResponseConsulta.newBuilder()
            .setClienteId(keyPixRetorno.clienteId?.toString() ?: "")
            .setPixId(keyPixRetorno.pixId?.toString() ?: "")
            .setChave(KeyPixResponseConsulta.ChavePix
                .newBuilder()
                .setTipo(TipoChave.valueOf(keyPixRetorno.tipoChave.name))
                .setChave(keyPixRetorno.valorChave)
                .setConta(KeyPixResponseConsulta.ChavePix.ContaInfo.newBuilder()
                    .setTipo(TipoConta.valueOf(keyPixRetorno.tipoConta.name))
                    .setInstituicao(keyPixRetorno.contaUsuario.instituicao)
                    .setNomeDoTitular(keyPixRetorno.contaUsuario.nomeTitular)
                    .setCpfDoTitular(keyPixRetorno.contaUsuario.cpfTitular)
                    .setAgencia(keyPixRetorno.contaUsuario.agencia)
                    .setNumeroDaConta(keyPixRetorno.contaUsuario.numeroConta)
                    .build()
                )
                .setCriadaEm(keyPixRetorno.momentoCadastro.let {
                    val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                    Timestamp.newBuilder()
                        .setSeconds(createdAt.epochSecond)
                        .setNanos(createdAt.nano)
                        .build()
                })
            )
            .build()
    }
}
