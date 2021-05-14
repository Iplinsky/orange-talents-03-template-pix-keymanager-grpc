package br.com.zup.academy.pix.chave.cadastro

import br.com.zup.academy.KeyManagerCadastrarGrpcServiceGrpc
import br.com.zup.academy.KeyPixRequestCadastro
import br.com.zup.academy.KeyPixResponseCadastro
import br.com.zup.academy.pix.annotation.ErrorHandler
import br.com.zup.academy.pix.chave.ChavePix
import br.com.zup.academy.pix.chave.ChavePixRequest
import br.com.zup.academy.pix.extension.function.toModel
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class CadastraChaveGrpcEndpoint(@Inject private val pixService: CadastraChaveGrpcService) :
    KeyManagerCadastrarGrpcServiceGrpc.KeyManagerCadastrarGrpcServiceImplBase() {

    override fun cadastrarChavePix(request: KeyPixRequestCadastro, responseObserver: StreamObserver<KeyPixResponseCadastro>) {
        val chave: ChavePixRequest = request.toModel()
        val chaveCadastrada: ChavePix = pixService.cadastrarChavePix(chave)

        responseObserver.onNext(
            KeyPixResponseCadastro.newBuilder()
                .setClientId(chave.clientId)
                .setPixId(chaveCadastrada.id.toString()).build()
        )
        responseObserver.onCompleted()
    }

}