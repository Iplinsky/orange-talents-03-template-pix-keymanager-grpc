package br.com.zup.academy.pix.chave.remocao

import br.com.zup.academy.KeyManagerRemoveGrpcServiceGrpc
import br.com.zup.academy.KeyPixRequestRemove
import br.com.zup.academy.KeyPixResponseRemove
import br.com.zup.academy.pix.chave.ChavePix
import br.com.zup.academy.pix.chave.ChavePixRepository
import br.com.zup.academy.pix.chave.TipoChaveEnum
import br.com.zup.academy.pix.chave.TipoContaEnum.CONTA_CORRENTE
import br.com.zup.academy.pix.client.itau.ContaUsuarioItau
import io.grpc.ManagedChannel
import io.grpc.Status.NOT_FOUND
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import java.util.*
import javax.inject.Inject

/**
 * Controle de transação desabilitado pois o servidor gRPC é executado em uma thread separada,
 * dessa forma não haverá problemas dentro do escopo @Test.
 **/

@TestMethodOrder(OrderAnnotation::class)
@MicronautTest(transactional = false)
internal class RemoveChaveGrpcEndpointTest {

    @field:Inject
    lateinit var chavePixRepository: ChavePixRepository

    @field:Inject
    lateinit var keyManagerServiceGrpc: KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub

    lateinit var chavePix: ChavePix

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()

        chavePix = chavePixRepository.save(
            ChavePix(
                clientId = UUID.randomUUID(),
                tipoChavePix = TipoChaveEnum.CPF,
                valorChave = "02467781054",
                tipoConta = CONTA_CORRENTE,
                contaUsuarioItau = ContaUsuarioItau()
            )
        )
    }

    @Test
    @Order(1)
    fun `deve remover a chave PIX cadastrada no sistema`() {
        val response: KeyPixResponseRemove = keyManagerServiceGrpc.removerChavePix(
            KeyPixRequestRemove
                .newBuilder()
                .setClientId(chavePix.clientId.toString())
                .setPixId(chavePix.id.toString())
                .build()
        )
        with(response) {
            assertEquals(response.pixId, chavePix.id.toString())
        }
    }

    @Test
    @Order(2)
    fun `nao deve remover a chave PIX caso a chave informada nao exista no sistema`() {
        val newRandomPixId = UUID.randomUUID().toString()
        val exception = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.removerChavePix(
                KeyPixRequestRemove
                    .newBuilder()
                    .setClientId(chavePix.clientId.toString())
                    .setPixId(newRandomPixId)
                    .build()
            )
        }

        with(exception) {
            assertEquals(exception.status.code, NOT_FOUND.code)
            assertEquals(
                exception.status.description,
                "A chave '$newRandomPixId' não foi localizada ou não pertence ao cliente informado."
            )
        }
    }

    @Test
    @Order(3)
    fun `nao deve remover a chave PIX caso ela pertenca a outro cliente`() {
        val newRandomClientId = UUID.randomUUID().toString()

        val response = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.removerChavePix(
                KeyPixRequestRemove
                    .newBuilder()
                    .setPixId(chavePix.id.toString())
                    .setClientId(newRandomClientId)
                    .build()
            )
        }

        with(response) {
            assertEquals(response.status.code, NOT_FOUND.code)
            assertEquals(
                response.status.description,
                "A chave '${chavePix.id.toString()}' não foi localizada ou não pertence ao cliente informado."
            )
        }
    }

    @Factory
    fun gRpcServerBlockingStub(@GrpcChannel(value = GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerRemoveGrpcServiceGrpc.KeyManagerRemoveGrpcServiceBlockingStub? {
        return KeyManagerRemoveGrpcServiceGrpc.newBlockingStub(channel)
    }
}