package br.com.zup.academy.pix.chave.consulta

import br.com.zup.academy.KeyPixRequestConsulta.SearchByPixAndClientId
import br.com.zup.academy.KeyPixRequestConsulta.newBuilder
import br.com.zup.academy.KeyPixResponseConsulta
import br.com.zup.academy.KeymanagerConsultarGrpcServiceGrpc
import br.com.zup.academy.pix.chave.*
import br.com.zup.academy.pix.client.bcb.*
import br.com.zup.academy.pix.client.itau.ContaUsuarioItau
import io.grpc.ManagedChannel
import io.grpc.Status.INVALID_ARGUMENT
import io.grpc.Status.NOT_FOUND
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.mockito.Mockito.*
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@TestMethodOrder(OrderAnnotation::class)
@MicronautTest(transactional = false)
internal class ConsultaChaveGrpcEndpointTest {

    @field:Inject
    lateinit var keyManagerServiceGrpc: KeymanagerConsultarGrpcServiceGrpc.KeymanagerConsultarGrpcServiceBlockingStub

    @field:Inject
    lateinit var chavePixRepository: ChavePixRepository

    @field:Inject
    lateinit var bcbClient: BcbClient

    companion object {
        val RANDOM_CLIENT_ID = UUID.randomUUID()
    }

    lateinit var chaveAleatoria: ChavePix
    lateinit var chaveEmail: ChavePix
    lateinit var chaveTelefone: ChavePix
    lateinit var chaveCpf: ChavePix

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()

        // CHAVE ALEATÓRIA
        chaveAleatoria = chavePixRepository.save(ChavePix(clientId = RANDOM_CLIENT_ID,
            tipoChavePix = TipoChaveEnum.ALEATORIA,
            valorChave = "KEY-VALUE-7",
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            contaUsuarioItau = ContaUsuarioItau()))

        // EMAIL
        chaveEmail = chavePixRepository.save(ChavePix(clientId = RANDOM_CLIENT_ID,
            tipoChavePix = TipoChaveEnum.EMAIL,
            valorChave = "thiago.iplinsky@zup.com.br",
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            contaUsuarioItau = ContaUsuarioItau()))

        // TELEFONE
        chaveTelefone = chavePixRepository.save(ChavePix(clientId = RANDOM_CLIENT_ID,
            tipoChavePix = TipoChaveEnum.TELEFONE,
            valorChave = "+5534999887765",
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            contaUsuarioItau = ContaUsuarioItau()))

        // CPF
        chaveCpf = chavePixRepository.save(ChavePix(clientId = RANDOM_CLIENT_ID,
            tipoChavePix = TipoChaveEnum.CPF,
            valorChave = "76526572022",
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            contaUsuarioItau = ContaUsuarioItau()))
    }

    @Test
    @Order(1)
    fun `deve consultar uma chave PIX com sucesso através dos codigos pixId e clientId`() {
        val response: KeyPixResponseConsulta = keyManagerServiceGrpc.consultarChavePix(
            newBuilder()
                .setPixId(
                    SearchByPixAndClientId
                        .newBuilder()
                        .setClientId(chaveCpf.clientId.toString())
                        .setPixId(chaveCpf.id.toString())
                        .build())
                .build())
        with(response) {
            assertEquals(chaveCpf.id.toString(), pixId.toString())
            assertEquals(chaveCpf.clientId.toString(), clienteId.toString())
            assertEquals(chaveCpf.tipoChavePix.name, response.chave.tipo.name)
            assertEquals(chaveCpf.valorChave, response.chave.chave)
        }
    }

    @Test
    @Order(2)
    fun `nao deve consultar uma chave PIX caso os valores de entrada estiverem invalidos através dos codigos pixId e clientId`() {
        val responseException = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.consultarChavePix(
                newBuilder()
                    .setPixId(
                        SearchByPixAndClientId
                            .newBuilder()
                            .setClientId("")
                            .setPixId("")
                            .build())
                    .build())
        }

        with(responseException.status) {
            assertEquals(code, INVALID_ARGUMENT.code)
            assertEquals(description, "Invalid UUID string: ")
        }
    }

    @Test
    @Order(3)
    fun `nao deve recuperar uma chave PIX através dos codigos pixId e clientId caso a mesma nao existir na base de dados `() {
        val responseException = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.consultarChavePix(
                newBuilder()
                    .setPixId(
                        SearchByPixAndClientId
                            .newBuilder()
                            .setClientId(UUID.randomUUID().toString())
                            .setPixId(UUID.randomUUID().toString())
                            .build())
                    .build())
        }

        with(responseException.status) {
            assertEquals(code, NOT_FOUND.code)
            assertEquals(description, "A chave Pix informada não foi localizada no sistema.")
        }
    }

    @Test
    @Order(4)
    fun `deve recuperar a chave PIX atraves do valor da chave recebido em um cenario local`() {
        val response: KeyPixResponseConsulta = keyManagerServiceGrpc.consultarChavePix(
            newBuilder()
                .setChave(chaveAleatoria.valorChave)
                .build()
        )
        with(response) {
            assertEquals(pixId, chaveAleatoria.id.toString())
            assertEquals(clienteId, chaveAleatoria.clientId.toString())
            assertEquals(chave.chave, chaveAleatoria.valorChave)
            assertEquals(chave.tipo.name, chaveAleatoria.tipoChavePix.name)
        }
    }

    @Test
    @Order(5)
    fun `deve recuperar a chave PIX utilizando o sistema do BCB com base no valor da chave recebido`() {
        `when`(bcbClient.buscarChavePixNoBcbClient("+5534999999999"))
            .thenReturn(HttpResponse.ok(mockBcbPixDetailsResponse()))

        val response = keyManagerServiceGrpc.consultarChavePix(
            newBuilder()
                .setChave("+5534999999999")
                .build())

        with(response) {
            verify(bcbClient, times(1)).buscarChavePixNoBcbClient("+5534999999999")
            assertEquals("", pixId)
            assertEquals("", clienteId)
            assertEquals(response.chave.tipo.name, mockBcbPixDetailsResponse().keyType.name)
            assertEquals(response.chave.chave, mockBcbPixDetailsResponse().key)
        }
    }

    @Test
    @Order(6)
    fun `nao deve recuperar a chave PIX caso o registro nao existir no sistemas local e BCB com base no valor da chave recebido como parametro`() {
        val chaveInexistente = UUID.randomUUID().toString()

        `when`(bcbClient.buscarChavePixNoBcbClient(chaveInexistente))
            .thenReturn(HttpResponse.notFound())

        val responseException = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.consultarChavePix(
                newBuilder()
                    .setChave(chaveInexistente)
                    .build()
            )
        }

        with(responseException.status) {
            verify(bcbClient, times(1)).buscarChavePixNoBcbClient(chaveInexistente)
            assertEquals(code, NOT_FOUND.code)
            assertEquals(description, "A chave informada não foi localizada no sistema.")
        }
    }

    @Test
    @Order(7)
    fun `nao deve recuperar uma chave PIX caso o valor da chave esteja invalido`() {
        `when`(bcbClient.buscarChavePixNoBcbClient(""))
            .thenReturn(HttpResponse.badRequest())

        val responseException = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.consultarChavePix(
                newBuilder()
                    .setChave("")
                    .build()
            )
        }

        with(responseException.status) {
            verify(bcbClient, times(1)).buscarChavePixNoBcbClient("")
            assertEquals(code, INVALID_ARGUMENT.code)
            assertEquals(description, "A chave Pix informada é inválida.")
        }
    }

    @Test
    @Order(8)
    fun `nao deve recuperar uma chave PIX caso nenhum valor de entrada seja informado para a busca`() {

        val responseException = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.consultarChavePix(newBuilder().build())
        }

        with(responseException.status) {
            assertEquals(code, INVALID_ARGUMENT.code)
            assertEquals(description, "A chave Pix informada é inválida!")
        }
    }

    /**
     * Função para popular o objeto BcbPixDetailsResponse mockado
     **/

    fun mockBcbPixDetailsResponse(): BcbPixDetailsResponse {
        return BcbPixDetailsResponse(
            keyType = PixKeyTypeEnum.TELEFONE,
            key = "+5534999999999",
            bankAccount = BankAccount(
                participant = ContaUsuarioItau.ITAU_UNIBANCO_ISPB,
                branch = "0001",
                accountNumber = "135791",
                accountType = AccountType.CACC
            ),
            owner = Owner(
                type = TypePersonEnum.NATURAL_PERSON,
                name = "Zion Iplinsky",
                taxIdNumber = "44177063010"
            ),
            createdAt = LocalDateTime.now()
        )
    }


    /**
     * Mockando BCB client
     **/

    @MockBean(BcbClient::class)
    fun mockBcbClient(): BcbClient {
        return mock(BcbClient::class.java)
    }

    /**
     * gRPC - Embedded server
     **/
    @Factory
    fun gRpcServerBlockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeymanagerConsultarGrpcServiceGrpc.KeymanagerConsultarGrpcServiceBlockingStub? {
        return KeymanagerConsultarGrpcServiceGrpc.newBlockingStub(channel)
    }

}