package br.com.zup.academy.pix.chave

import br.com.zup.academy.KeyManagerServiceGrpc
import br.com.zup.academy.KeyPixRequest
import br.com.zup.academy.KeyPixResponse
import br.com.zup.academy.pix.annotation.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class CadastraChaveGrpcEndpoint(@Inject private val pixService: ChavePixService) : KeyManagerServiceGrpc.KeyManagerServiceImplBase() {

    override fun cadastrarChavePix(request: KeyPixRequest, responseObserver: StreamObserver<KeyPixResponse>) {
        val chave: ChavePixRequest = request.toModel()
        val chaveCadastrada: ChavePix = pixService.cadastrarChavePix(chave)

        responseObserver.onNext(
            KeyPixResponse.newBuilder()
                .setClientId(chave.clientId)
                .setPixId(chaveCadastrada.id.toString()).build()
        )
        responseObserver.onCompleted()
    }

}