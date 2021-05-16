package br.com.zup.academy.pix.chave.remocao

import br.com.zup.academy.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.academy.KeyPixRequestRemove
import br.com.zup.academy.KeyPixResponseRemove
import br.com.zup.academy.pix.annotation.ErrorHandler
import br.com.zup.academy.pix.extension.function.toModel
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class RemoveChaveGrpcEndpoint(@Inject private val pixService: RemoveChaveGrpcService) :
    KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceImplBase() {

    override fun removerChavePix(request: KeyPixRequestRemove, responseObserver: StreamObserver<KeyPixResponseRemove>) {
        pixService.remover(request.toModel())

        responseObserver.onNext(KeyPixResponseRemove.newBuilder().setPixId(request.pixId).build())
        responseObserver.onCompleted()
    }
}