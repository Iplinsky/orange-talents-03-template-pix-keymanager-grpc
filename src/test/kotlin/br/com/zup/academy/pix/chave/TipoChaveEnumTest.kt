package br.com.zup.academy.pix.chave

import br.com.zup.academy.pix.chave.TipoChaveEnum.*
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class TipoChaveEnumTest {

    @Nested
    inner class CPF {

        @Test
        fun `deve reconhecer a chave CPF como valida caso o formato do numero de entrada esteja correto`() {
            with(CPF) {
                assertTrue(validarFormatoDaChave(valorChave = "27939394045"))
            }
        }

        @Test
        fun `deve invalidar a chave CPF caso o formato do numero de entrada esteja incorreto`() {
            with(CPF) {
                assertFalse(validarFormatoDaChave(valorChave = "111111111112"))
            }
        }

        @Test
        fun `deve invalidar a chave CPF caso houver letras no numero de entrada`() {
            with(CPF) {
                assertFalse(validarFormatoDaChave(valorChave = "A2793939404Z"))
            }
        }

        @Test
        fun `deve invalidar a chave CPF caso vier nula ou em branco`() {
            with(CPF) {
                assertFalse(validarFormatoDaChave(valorChave = ""))
                assertFalse(validarFormatoDaChave(valorChave = null))
            }
        }
    }

    @Nested
    inner class TELEFONE {

        @Test
        fun `deve reconhecer a chave TELEFONE como valida caso possuir o formato adequado`() {
            with(TELEFONE) {
                assertTrue(validarFormatoDaChave(valorChave = "+5534999998877"))
            }
        }

        @Test
        fun `deve invalidar a chave TELEFONE caso vier nula ou em branco`() {
            with(TELEFONE) {
                assertFalse(validarFormatoDaChave(valorChave = ""))
                assertFalse(validarFormatoDaChave(valorChave = null))
            }
        }

        @Test
        fun `deve invalidar a chave TELEFONE caso vier em um formato inadequado`() {
            with(TELEFONE) {
                assertFalse(validarFormatoDaChave(valorChave = "+55349999988776655"))
                assertFalse(validarFormatoDaChave(valorChave = "+55349999988776?"))
                assertFalse(validarFormatoDaChave(valorChave = "ABC55349999988776"))
                assertFalse(validarFormatoDaChave(valorChave = "TELEFONE"))
            }
        }
    }

    @Nested
    inner class EMAIL {

        @Test
        fun `deve reconhecer a chave EMAIL como valida caso possuir o formato adequado`() {
            with(EMAIL) {
                assertTrue(validarFormatoDaChave(valorChave = "thiago.iplinsky@gmail.com"))
            }
        }

        @Test
        fun `deve invalidar a chave EMAIL caso possuir um formato inadequado`() {
            with(EMAIL) {
                assertFalse(validarFormatoDaChave(valorChave = "thiago.iplinskygmail.com"))
                assertFalse(validarFormatoDaChave(valorChave = "thiago.iplinsky@gmail."))
                assertFalse(validarFormatoDaChave(valorChave = "thiago.iplinsky@"))
                assertFalse(validarFormatoDaChave(valorChave = "@"))
            }
        }

        @Test
        fun `deve invalidar a chave EMAIL caso vier nula ou em branco`() {
            with(EMAIL) {
                assertFalse(validarFormatoDaChave(valorChave = ""))
                assertFalse(validarFormatoDaChave(valorChave = null))
            }
        }

    }

    @Nested
    inner class ALEATORIA {

        @Test
        fun `deve reconhecer a chave ALEATORIA como valida caso vier nula ou em branco`() {
            with(ALEATORIA) {
                assertTrue(validarFormatoDaChave(valorChave = ""))
                assertTrue(validarFormatoDaChave(valorChave = null))
            }
        }

        @Test
        fun `deve invalidar a chave ALEATORIA caso possuir valor inserido`() {
            with(ALEATORIA) {
                assertFalse(validarFormatoDaChave(valorChave = "Valor inserido"))
            }
        }

    }

}