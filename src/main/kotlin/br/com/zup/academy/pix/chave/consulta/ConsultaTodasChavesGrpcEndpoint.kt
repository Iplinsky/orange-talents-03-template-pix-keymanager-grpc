package br.com.zup.academy.pix.chave.consulta

import br.com.zup.academy.*
import br.com.zup.academy.pix.annotation.ErrorHandler
import br.com.zup.academy.pix.chave.ChavePixRepository
import com.google.protobuf.Timestamp
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class ConsultaTodasChavesGrpcEndpoint(
    @Inject private val chavePixRepository: ChavePixRepository,
) :
    KeyManagerConsultarTodasGrpcServiceGrpc.KeyManagerConsultarTodasGrpcServiceImplBase() {

    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    override fun consultarTodasChavesPix(request: KeyPixRequestConsultaTodas,responseObserver: StreamObserver<KeyPixResponseConsultaTodas>) {

        LOGGER.info(request.toString())

        if (request.clientId.isNullOrBlank()) throw IllegalArgumentException("O parâmetro ClientId não pode ser nulo ou vazio.")

        val listaDeChaves =
            chavePixRepository.findAllByClientId(UUID.fromString(request.clientId)).map { it ->
                KeyPixResponseConsultaTodas
                    .ChavePix
                    .newBuilder()
                    .setPixId(it.id.toString())
                    .setTipoChave(TipoChave.valueOf(it.tipoChavePix.name))
                    .setValorChave(it.valorChave)
                    .setTipoConta(TipoConta.valueOf(it.tipoConta.name))
                    .setCriadaEm(it.momentoCadastro.let {
                        val instant = it.atZone(ZoneId.of("UTC")).toInstant()
                        Timestamp
                            .newBuilder()
                            .setSeconds(instant.epochSecond)
                            .setNanos(instant.nano)
                            .build()
                    })
                    .build()
            }.toMutableList()


        responseObserver.onNext(
            KeyPixResponseConsultaTodas
                .newBuilder()
                .setClientId(request.clientId)
                .addAllChavesPix(listaDeChaves)
                .build())
        responseObserver.onCompleted()
    }

}