package br.com.zup.academy.pix.chave

import br.com.zup.academy.KeyManagerServiceGrpc
import br.com.zup.academy.KeyPixRequest
import br.com.zup.academy.pix.client.*
import io.grpc.ManagedChannel
import io.grpc.Status.*
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.util.*
import javax.inject.Inject

/**
 * Controle de transação desabilitado pois o servidor gRPC é executado em uma thread separada,
 * dessa forma não haverá problemas dentro do escopo @Test.
 **/

@TestMethodOrder(OrderAnnotation::class)
@MicronautTest(transactional = false)
class CadastraChaveGrpcEndpointTest {

    @field:Inject
    lateinit var chavePixRepository: ChavePixRepository

    @field:Inject
    lateinit var itauClient: ItauClient

    @field:Inject
    lateinit var keyManagerServiceGrpc: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub

    lateinit var pixDtoResponse: PixDtoResponse

    @BeforeEach
    internal fun setUp() {
        // Clean database
        chavePixRepository.deleteAll()

        // Build PIX Response
        pixDtoResponse = PixDtoResponse(
            tipo = KeyPixRequest.TipoConta.CONTA_CORRENTE_VALUE.toString(),
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "ITAU_UNIBANCO_ISPB"),
            agencia = "7777",
            numero = "291900",
            titular = TitularResponse("Thiago Iplinsky", "01234567890")
        )
    }

    companion object {
        val RANDOM_CLIENT_ID_PIX = UUID.randomUUID().toString()
    }

    @Test
    @Order(1)
    fun `deve cadastrar uma nova chave pix do tipo cpf`() {
        `when`(itauClient.consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX, tipo = KeyPixRequest.TipoConta.CONTA_CORRENTE.name))
            .thenReturn(HttpResponse.ok(pixDtoResponse))

        val responseKeyPix = keyManagerServiceGrpc.cadastrarChavePix(
            KeyPixRequest
                .newBuilder()
                .setClientId(RANDOM_CLIENT_ID_PIX)
                .setTipoChavePix(KeyPixRequest.TipoChave.CPF)
                .setValorChave("01234567890")
                .setTipoConta(KeyPixRequest.TipoConta.CONTA_CORRENTE)
                .build()

        )
        with(responseKeyPix) {
            assertNotNull(pixId)
            assertEquals(RANDOM_CLIENT_ID_PIX, clientId)
            verify(itauClient, atMost(1)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX, tipo = KeyPixRequest.TipoConta.CONTA_CORRENTE.name)
        }
    }

    @Test
    @Order(2)
    fun `deve cadastrar uma nova chave pix do tipo email`() {
        `when`(itauClient.consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX, tipo = KeyPixRequest.TipoConta.CONTA_CORRENTE.name))
            .thenReturn(HttpResponse.ok(pixDtoResponse))

        val responseKeyPix = keyManagerServiceGrpc.cadastrarChavePix(
            KeyPixRequest
                .newBuilder()
                .setClientId(RANDOM_CLIENT_ID_PIX)
                .setTipoChavePix(KeyPixRequest.TipoChave.EMAIL)
                .setValorChave("thiago.iplinsky@zup.com.br")
                .setTipoConta(KeyPixRequest.TipoConta.CONTA_CORRENTE)
                .build()

        )
        with(responseKeyPix) {
            assertNotNull(pixId)
            assertEquals(RANDOM_CLIENT_ID_PIX, clientId)
            verify(itauClient, atMost(1)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX, tipo = KeyPixRequest.TipoConta.CONTA_CORRENTE.name)
        }
    }

    @Test
    @Order(3)
    fun `deve cadastrar uma nova chave pix do tipo telefone celular`() {
        `when`(itauClient.consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX, tipo = KeyPixRequest.TipoConta.CONTA_CORRENTE.name))
            .thenReturn(HttpResponse.ok(pixDtoResponse))

        val responseKeyPix = keyManagerServiceGrpc.cadastrarChavePix(
            KeyPixRequest
                .newBuilder()
                .setClientId(RANDOM_CLIENT_ID_PIX)
                .setTipoChavePix(KeyPixRequest.TipoChave.TELEFONE_CELULAR)
                .setValorChave("+5585988714077")
                .setTipoConta(KeyPixRequest.TipoConta.CONTA_CORRENTE)
                .build()

        )
        with(responseKeyPix) {
            assertNotNull(pixId)
            assertEquals(RANDOM_CLIENT_ID_PIX, clientId)
            verify(itauClient, atMost(1)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX, tipo = KeyPixRequest.TipoConta.CONTA_CORRENTE.name)
        }
    }

    @Test
    @Order(4)
    fun `nao deve registrar uma chave pix caso ja houver um tipo existente`() {
        chavePixRepository.save(
            ChavePix(
                clientId = UUID.fromString(RANDOM_CLIENT_ID_PIX),
                tipoChavePix = TipoChaveEnum.CPF,
                valorChave = "01234567890",
                tipoConta = TipoContaEnum.CONTA_CORRENTE,
                contaUsuarioItau = ContaUsuarioItau()
            )
        )

        val exceptionResponse = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.cadastrarChavePix(
                KeyPixRequest
                    .newBuilder()
                    .setClientId(RANDOM_CLIENT_ID_PIX)
                    .setTipoChavePix(KeyPixRequest.TipoChave.CPF)
                    .setValorChave("01234567890")
                    .setTipoConta(KeyPixRequest.TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }
        with(exceptionResponse) {
            assertEquals(ALREADY_EXISTS.code, status.code)
            assertEquals("A chave '01234567890' já existe.", status.description)
            verify(itauClient, times(0)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX, tipo = KeyPixRequest.TipoConta.CONTA_CORRENTE.name)
        }
    }

    @Test
    @Order(5)
    fun `nao deve registrar uma chave pix para um usuario nao encontrado`() {
        `when`(itauClient.consultarContaDoClienteItau(RANDOM_CLIENT_ID_PIX, KeyPixRequest.TipoConta.CONTA_POUPANCA.name))
            .thenReturn(HttpResponse.notFound())

        val exceptionResponse = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.cadastrarChavePix(
                KeyPixRequest
                    .newBuilder()
                    .setClientId(RANDOM_CLIENT_ID_PIX)
                    .setTipoChavePix(KeyPixRequest.TipoChave.CPF)
                    .setValorChave("98765432100")
                    .setTipoConta(KeyPixRequest.TipoConta.CONTA_POUPANCA)
                    .build()
            )
        }
        with(exceptionResponse) {
            assertEquals(NOT_FOUND.code, status.code)
            assertEquals("Cliente não localizado no banco Itau.", status.description)
            verify(itauClient, times(1)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX, tipo = KeyPixRequest.TipoConta.CONTA_POUPANCA.name)
        }
    }

    @Test
    @Order(6)
    fun `nao deve cadastrar uma chave pix com valores invalidos`() {
        val exceptionResponse = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.cadastrarChavePix(KeyPixRequest.newBuilder().build())
        }
        with(exceptionResponse) {
            assertEquals(INVALID_ARGUMENT.code, status.code)
            verify(itauClient, never()).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX, tipo = KeyPixRequest.TipoConta.CONTA_POUPANCA.name)
        }
    }

    @Factory
    fun gRpcServerBlockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerServiceGrpc.KeyManagerServiceBlockingStub? {
        return KeyManagerServiceGrpc.newBlockingStub(channel)
    }

    @MockBean(ItauClient::class)
    fun mockandoItauClient(): ItauClient {
        return Mockito.mock(ItauClient::class.java)
    }

}