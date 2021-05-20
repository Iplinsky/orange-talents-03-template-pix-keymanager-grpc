package br.com.zup.academy.pix.chave.consulta

import br.com.zup.academy.KeyPixRequestConsulta
import br.com.zup.academy.KeyPixResponseConsulta
import br.com.zup.academy.KeymanagerConsultarGrpcServiceGrpc
import br.com.zup.academy.pix.annotation.ErrorHandler
import br.com.zup.academy.pix.chave.ChavePixRepository
import br.com.zup.academy.pix.client.bcb.BcbClient
import br.com.zup.academy.pix.extension.function.toModel
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@ErrorHandler
@Singleton
class   ConsultaChaveGrpcEndpoint(
    @Inject private val repository: ChavePixRepository,
    @Inject private val bcbClient: BcbClient,
    @Inject private val validator: Validator,
) : KeymanagerConsultarGrpcServiceGrpc.KeymanagerConsultarGrpcServiceImplBase() {

    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    override fun consultarChavePix(request: KeyPixRequestConsulta, responseObserver: StreamObserver<KeyPixResponseConsulta>) {
        LOGGER.info("new request: ${request}")

        val filtro = request.toModel(validator)
        val keyPixRetorno: KeyPixRetornoConsulta = filtro.retrieve(chavePixRepository = repository, bcbClient = bcbClient)

        responseObserver.onNext(KeyPixResponseConverter().convert(keyPixRetorno))
        responseObserver.onCompleted()
    }
}