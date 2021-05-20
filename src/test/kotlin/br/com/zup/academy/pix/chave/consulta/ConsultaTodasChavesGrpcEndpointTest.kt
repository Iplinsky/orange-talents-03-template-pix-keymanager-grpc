package br.com.zup.academy.pix.chave.consulta

import br.com.zup.academy.KeyManagerConsultarTodasGrpcServiceGrpc
import br.com.zup.academy.KeyPixRequestConsultaTodas
import br.com.zup.academy.KeyPixResponseConsultaTodas
import br.com.zup.academy.TipoChave
import br.com.zup.academy.pix.chave.ChavePix
import br.com.zup.academy.pix.chave.ChavePixRepository
import br.com.zup.academy.pix.chave.TipoChaveEnum
import br.com.zup.academy.pix.chave.TipoContaEnum
import br.com.zup.academy.pix.client.itau.ContaUsuarioItau
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation
import java.util.*
import javax.inject.Inject

@TestMethodOrder(OrderAnnotation::class)
@MicronautTest(transactional = false)
internal class ConsultaTodasChavesGrpcEndpointTest {

    @field:Inject
    lateinit var chavePixRepository: ChavePixRepository

    @field:Inject
    lateinit var keyManagerServiceGrpc: KeyManagerConsultarTodasGrpcServiceGrpc.KeyManagerConsultarTodasGrpcServiceBlockingStub

    companion object {
        val RANDOM_CLIENT_ID = UUID.randomUUID()
        val RANDOM_KEY = UUID.randomUUID().toString()
    }

    @BeforeEach
    internal fun setUp() {
        chavePixRepository.deleteAll()

        val chavePixCpf = ChavePix(
            clientId = RANDOM_CLIENT_ID,
            tipoChavePix = TipoChaveEnum.CPF,
            valorChave = "02467781054",
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            contaUsuarioItau = ContaUsuarioItau()
        )

        val chavePixEmail = ChavePix(
            clientId = RANDOM_CLIENT_ID,
            tipoChavePix = TipoChaveEnum.EMAIL,
            valorChave = "random@email.com",
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            contaUsuarioItau = ContaUsuarioItau()
        )
        val chavePixAleatoria = ChavePix(
            clientId = RANDOM_CLIENT_ID,
            tipoChavePix = TipoChaveEnum.ALEATORIA,
            valorChave = RANDOM_KEY,
            tipoConta = TipoContaEnum.CONTA_CORRENTE,
            contaUsuarioItau = ContaUsuarioItau()
        )

        chavePixRepository.save(chavePixCpf)
        chavePixRepository.save(chavePixEmail)
        chavePixRepository.save(chavePixAleatoria)

    }

    @Test
    @Order(1)
    fun `deve consultar todas as chaves a partir de um ClientId valido`() {
        val response: KeyPixResponseConsultaTodas = keyManagerServiceGrpc.consultarTodasChavesPix(
            KeyPixRequestConsultaTodas
                .newBuilder()
                .setClientId(RANDOM_CLIENT_ID.toString())
                .build()
        )

        with(response) {
            assertEquals(clientId, RANDOM_CLIENT_ID.toString())
            assertThat(chavesPixList, hasSize(3))
            // Verifica se a coleção está armazenando os registros na ordem correta de acordo com o processo de cadastro
            assertThat(
                chavesPixList.map { Pair(it.tipoChave, it.valorChave) }.toList(),
                contains(
                    Pair(TipoChave.CPF, "02467781054"),
                    Pair(TipoChave.EMAIL, "random@email.com"),
                    Pair(TipoChave.ALEATORIA, RANDOM_KEY)
                )
            )
            // Verifica o armazenando dos registros dentro da coleção independente da ordem do cadastro
            assertThat(
                chavesPixList.map { Pair(it.tipoChave, it.valorChave) }.toList(),
                containsInAnyOrder(
                    Pair(TipoChave.ALEATORIA, RANDOM_KEY),
                    Pair(TipoChave.EMAIL, "random@email.com"),
                    Pair(TipoChave.CPF, "02467781054"))
            )
        }
    }

    @Test
    @Order(2)
    fun `nao deve apresentar nenhuma chave PIX caso o cliente ainda não possua`() {
        val response = keyManagerServiceGrpc.consultarTodasChavesPix(
            KeyPixRequestConsultaTodas
                .newBuilder()
                .setClientId(UUID.randomUUID().toString())
                .build())

        with(response) {
            /**
             * Some ways to check the size of the list.
             * */
            assertThat(chavesPixList, hasSize(0))
            assertEquals(0, response.chavesPixCount)
        }
    }

    @Test
    @Order(3)
    fun `nao deve apresentar nenhuma chave PIX caso o clientId for invalido`() {
        val responseException = assertThrows<StatusRuntimeException> {
            keyManagerServiceGrpc.consultarTodasChavesPix(
                KeyPixRequestConsultaTodas
                    .newBuilder()
                    .setClientId("")
                    .build()
            )
        }

        with(responseException.status) {
            assertEquals(code, Status.INVALID_ARGUMENT.code)
            assertEquals(description, "O parâmetro ClientId não pode ser nulo ou vazio.")
        }
    }

    @Factory
    fun gRpcServerBlockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerConsultarTodasGrpcServiceGrpc.KeyManagerConsultarTodasGrpcServiceBlockingStub? {
        return KeyManagerConsultarTodasGrpcServiceGrpc.newBlockingStub(channel)
    }
}