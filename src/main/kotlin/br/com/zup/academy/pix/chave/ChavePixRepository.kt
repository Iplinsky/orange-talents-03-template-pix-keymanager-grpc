package br.com.zup.academy.pix.chave

import br.com.zup.academy.pix.chave.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository

@Repository
interface ChavePixRepository : JpaRepository<ChavePix, Long> {
    fun existsByValorChave(valorChave: String): Boolean
}
