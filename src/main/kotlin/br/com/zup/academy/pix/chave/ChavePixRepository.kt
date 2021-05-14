package br.com.zup.academy.pix.chave

import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, UUID> {

    fun existsByValorChave(valorChave: String): Boolean
    fun existsByIdAndClientId(id: UUID, clientId: UUID): Boolean
}
