package br.com.zup.academy.pix.chave

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, UUID> {

    fun existsByValorChave(valorChave: String): Boolean
    fun findByValorChave(valorChave: String): Optional<ChavePix>
    fun findAllByClientId(clientId: UUID): MutableList<ChavePix>
    fun findByIdAndClientId(pixId: UUID?, fromString: UUID?): Optional<ChavePix>

}
