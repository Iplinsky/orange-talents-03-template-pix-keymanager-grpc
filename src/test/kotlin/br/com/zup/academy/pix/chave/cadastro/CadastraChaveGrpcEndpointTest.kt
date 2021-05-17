package br.com.zup.academy.pix.chave.cadastro

import br.com.zup.academy.KeyManagerCadastrarGrpcServiceGrpc
import br.com.zup.academy.KeyPixRequestCadastro
import br.com.zup.academy.pix.chave.*
import br.com.zup.academy.pix.client.bcb.*
import br.com.zup.academy.pix.client.itau.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.Status.*
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import org.mockito.Mockito
import org.mockito.Mockito.*
import java.time.LocalDateTime
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
    lateinit var bcbClient: BcbClient

    @field:Inject
    lateinit var keyManagerServiceGrpc: KeyManagerCadastrarGrpcServiceGrpc.KeyManagerCadastrarGrpcServiceBlockingStub

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()
    }

    companion object {
        val RANDOM_CLIENT_ID_PIX = UUID.randomUUID().toString()
    }

    @Test
    @Order(1)
    fun `deve cadastrar uma nova chave pix do tipo cpf`() {
        `when`(itauClient.consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX,
            tipo = KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE.name))
            .thenReturn(HttpResponse.ok(filledPixDtoResponse()))

        `when`(bcbClient.cadastrarChavePixNoBcbClient(pixRequestCpf())).thenReturn(HttpResponse.created(pixResponseCpf()))

        val responseKeyPix = keyManagerServiceGrpc.cadastrarChavePix(
            KeyPixRequestCadastro
                .newBuilder()
                .setClientId(RANDOM_CLIENT_ID_PIX)
                .setTipoChavePix(KeyPixRequestCadastro.TipoChave.CPF)
                .setValorChave("01234567890")
                .setTipoConta(KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE)
                .build()

        )
        with(responseKeyPix) {
            assertTrue(chavePixRepository.count() == 1L)
            assertNotNull(pixId)
            assertEquals(RANDOM_CLIENT_ID_PIX, clientId)
            verify(itauClient, atMost(1)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX,
                tipo = KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE.name)
            verify(bcbClient, times(1)).cadastrarChavePixNoBcbClient(pixRequestCpf())
        }
    }

    @Test
    @Order(2)
    fun `deve cadastrar uma nova chave pix do tipo email`() {
        `when`(itauClient.consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX,
            tipo = KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE.name))
            .thenReturn(HttpResponse.ok(filledPixDtoResponse()))

        `when`(bcbClient.cadastrarChavePixNoBcbClient(pixRequestEmail())).thenReturn(HttpResponse.created(
            pixResponseEmail()))

        val responseKeyPix = keyManagerServiceGrpc.cadastrarChavePix(
            KeyPixRequestCadastro
                .newBuilder()
                .setClientId(RANDOM_CLIENT_ID_PIX)
                .setTipoChavePix(KeyPixRequestCadastro.TipoChave.EMAIL)
                .setValorChave("thiago.iplinsky@zup.com.br")
                .setTipoConta(KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE)
                .build()

        )
        with(responseKeyPix) {
            assertTrue(chavePixRepository.count() == 1L)
            assertNotNull(pixId)
            assertEquals(RANDOM_CLIENT_ID_PIX, clientId)
            verify(itauClient, atMost(1)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX,
                tipo = KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE.name)
            verify(bcbClient, times(1)).cadastrarChavePixNoBcbClient(pixRequestEmail())
        }
    }

    @Test
    @Order(3)
    fun `deve cadastrar uma nova chave pix do tipo telefone celular`() {
        `when`(itauClient.consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX,
            tipo = KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE.name))
            .thenReturn(HttpResponse.ok(filledPixDtoResponse()))

        `when`(bcbClient.cadastrarChavePixNoBcbClient(pixRequestTelefone())).thenReturn(HttpResponse.created(
            pixResponseTelefone()))

        val responseKeyPix = keyManagerServiceGrpc.cadastrarChavePix(
            KeyPixRequestCadastro
                .newBuilder()
                .setClientId(RANDOM_CLIENT_ID_PIX)
                .setTipoChavePix(KeyPixRequestCadastro.TipoChave.TELEFONE)
                .setValorChave("+5585988714077")
                .setTipoConta(KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE)
                .build()

        )
        with(responseKeyPix) {
            assertTrue(chavePixRepository.count() == 1L)
            assertNotNull(pixId)
            assertEquals(RANDOM_CLIENT_ID_PIX, clientId)
            verify(itauClient, atMost(1)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX,
                tipo = KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE.name)
            verify(bcbClient, times(1)).cadastrarChavePixNoBcbClient(pixRequestTelefone())
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
                KeyPixRequestCadastro
                    .newBuilder()
                    .setClientId(RANDOM_CLIENT_ID_PIX)
                    .setTipoChavePix(KeyPixRequestCadastro.TipoChave.CPF)
                    .setValorChave("01234567890")
                    .setTipoConta(KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }
        with(exceptionResponse) {
            assertEquals(ALREADY_EXISTS.code, status.code)
            assertEquals("A chave '01234567890' já existe.", status.description)
            verify(itauClient, times(0)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX,
                tipo = KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE.name)
        }
    }

    @Test
    @Order(5)
    fun `nao deve registrar uma chave pix para um usuario nao encontrado`() {
        `when`(itauClient.consultarContaDoClienteItau(RANDOM_CLIENT_ID_PIX,
            KeyPixRequestCadastro.TipoConta.CONTA_POUPANCA.name))
            .thenReturn(HttpResponse.notFound())

        val exceptionResponse = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.cadastrarChavePix(
                KeyPixRequestCadastro
                    .newBuilder()
                    .setClientId(RANDOM_CLIENT_ID_PIX)
                    .setTipoChavePix(KeyPixRequestCadastro.TipoChave.CPF)
                    .setValorChave("98765432100")
                    .setTipoConta(KeyPixRequestCadastro.TipoConta.CONTA_POUPANCA)
                    .build()
            )
        }
        with(exceptionResponse) {
            assertEquals(NOT_FOUND.code, status.code)
            assertEquals("Cliente não localizado no banco Itau.", status.description)
            verify(itauClient, times(1)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX,
                tipo = KeyPixRequestCadastro.TipoConta.CONTA_POUPANCA.name)
        }
    }

    @Test
    @Order(6)
    fun `nao deve cadastrar uma chave pix com valores invalidos`() {
        val exceptionResponse = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.cadastrarChavePix(KeyPixRequestCadastro.newBuilder().build())
        }
        with(exceptionResponse) {
            assertEquals(INVALID_ARGUMENT.code, status.code)
            verify(itauClient, never()).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX,
                tipo = KeyPixRequestCadastro.TipoConta.CONTA_POUPANCA.name)
        }
    }

    @Test
    @Order(7)
    fun `nao deve cadastrar uma chave pix caso ocorra algum erro na comunicacao com o sistema do BCB`() {
        `when`(itauClient.consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX,
            tipo = KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE.name))
            .thenReturn(HttpResponse.ok(filledPixDtoResponse()))

        `when`(bcbClient.cadastrarChavePixNoBcbClient(pixRequestCpf())).thenReturn(HttpResponse.badRequest())

        val exceptionResponse = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.cadastrarChavePix(
                KeyPixRequestCadastro
                    .newBuilder()
                    .setClientId(RANDOM_CLIENT_ID_PIX)
                    .setTipoChavePix(KeyPixRequestCadastro.TipoChave.CPF)
                    .setValorChave("01234567890")
                    .setTipoConta(KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE)
                    .build()
            )
        }
        with(exceptionResponse) {
            assertEquals("Erro ao registrar a chave PIX no Banco Central do Brasil (BCB)", status.description)
            assertEquals(Status.FAILED_PRECONDITION.code, status.code)
            verify(itauClient, times(1)).consultarContaDoClienteItau(clientId = RANDOM_CLIENT_ID_PIX, tipo = KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE.name)
            verify(bcbClient, times(1)).cadastrarChavePixNoBcbClient(pixRequestCpf())
        }
    }

    @Factory
    fun gRpcServerBlockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerCadastrarGrpcServiceGrpc.KeyManagerCadastrarGrpcServiceBlockingStub? {
        return KeyManagerCadastrarGrpcServiceGrpc.newBlockingStub(channel)
    }

    @MockBean(ItauClient::class)
    fun mockandoItauClient(): ItauClient {
        return Mockito.mock(ItauClient::class.java)
    }

    @MockBean(BcbClient::class)
    fun mockandoBcbClient(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }

    /**
     * Returns the filled entities.
     */

    fun pixRequestCpf(): BcbPixRequest {
        return BcbPixRequest(
            keyType = PixKeyTypeEnum.CPF,
            key = "01234567890",
            bankAccount = BankAccount(
                participant = ContaUsuarioItau.ITAU_UNIBANCO_ISPB,
                branch = "7777",
                accountNumber = "291900",
                accountType = AccountType.by(TipoContaEnum.CONTA_CORRENTE)
            ),
            owner = Owner(
                type = Type.NATURAL_PERSON,
                name = "Thiago Iplinsky",
                taxIdNumber = "01234567890"
            )
        )
    }

    fun pixResponseCpf(): BcbPixResponse {
        return BcbPixResponse(
            keyType = PixKeyTypeEnum.CPF,
            key = "01234567890",
            bankAccount = BankAccount(
                participant = ContaUsuarioItau.ITAU_UNIBANCO_ISPB,
                branch = "7777",
                accountNumber = "291900",
                accountType = AccountType.by(TipoContaEnum.CONTA_CORRENTE)
            ),
            owner = Owner(
                type = Type.NATURAL_PERSON,
                name = "Thiago Iplinsky",
                taxIdNumber = "01234567890"
            ),
            LocalDateTime.now()
        )
    }

    fun pixRequestEmail(): BcbPixRequest {
        return BcbPixRequest(
            keyType = PixKeyTypeEnum.EMAIL,
            key = "thiago.iplinsky@zup.com.br",
            bankAccount = BankAccount(
                participant = ContaUsuarioItau.ITAU_UNIBANCO_ISPB,
                branch = "7777",
                accountNumber = "291900",
                accountType = AccountType.by(TipoContaEnum.CONTA_CORRENTE)
            ),
            owner = Owner(
                type = Type.NATURAL_PERSON,
                name = "Thiago Iplinsky",
                taxIdNumber = "01234567890"
            )
        )
    }

    fun pixResponseEmail(): BcbPixResponse {
        return BcbPixResponse(
            keyType = PixKeyTypeEnum.EMAIL,
            key = "thiago.iplinsky@zup.com.br",
            bankAccount = BankAccount(
                participant = ContaUsuarioItau.ITAU_UNIBANCO_ISPB,
                branch = "7777",
                accountNumber = "291900",
                accountType = AccountType.by(TipoContaEnum.CONTA_CORRENTE)
            ),
            owner = Owner(
                type = Type.NATURAL_PERSON,
                name = "Thiago Iplinsky",
                taxIdNumber = "01234567890"
            ),
            LocalDateTime.now()
        )
    }

    fun pixRequestTelefone(): BcbPixRequest {
        return BcbPixRequest(
            keyType = PixKeyTypeEnum.PHONE,
            key = "+5585988714077",
            bankAccount = BankAccount(
                participant = ContaUsuarioItau.ITAU_UNIBANCO_ISPB,
                branch = "7777",
                accountNumber = "291900",
                accountType = AccountType.by(TipoContaEnum.CONTA_CORRENTE)
            ),
            owner = Owner(
                type = Type.NATURAL_PERSON,
                name = "Thiago Iplinsky",
                taxIdNumber = "01234567890"
            )
        )
    }

    fun pixResponseTelefone(): BcbPixResponse {
        return BcbPixResponse(
            keyType = PixKeyTypeEnum.PHONE,
            key = "+5585988714077",
            bankAccount = BankAccount(
                participant = ContaUsuarioItau.ITAU_UNIBANCO_ISPB,
                branch = "7777",
                accountNumber = "291900",
                accountType = AccountType.by(TipoContaEnum.CONTA_CORRENTE)
            ),
            owner = Owner(
                type = Type.NATURAL_PERSON,
                name = "Thiago Iplinsky",
                taxIdNumber = "01234567890"
            ),
            LocalDateTime.now()
        )
    }

    fun filledPixDtoResponse(): PixDtoResponse {
        return PixDtoResponse(
            tipo = KeyPixRequestCadastro.TipoConta.CONTA_CORRENTE_VALUE.toString(),
            instituicao = InstituicaoResponse("UNIBANCO ITAU SA", "ITAU_UNIBANCO_ISPB"),
            agencia = "7777",
            numero = "291900",
            titular = TitularResponse("Thiago Iplinsky", "01234567890")
        )
    }

}