package br.com.zup.academy.pix.chave

import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChaveEnum {
    CPF {
        override fun validarFormatoDaChave(valorChave: String?): Boolean {
            if (valorChave.isNullOrBlank()) {
                return false
            }
            if (!valorChave.matches(regex = "^[0-9]{11}\$".toRegex())) {
                return false
            }
            return CPFValidator().run {
                initialize(null)
                isValid(valorChave, null)
            }
        }
    },
    TELEFONE {
        override fun validarFormatoDaChave(valorChave: String?): Boolean {
            if (valorChave.isNullOrBlank()) {
                return false
            }
            return valorChave.matches(regex = "^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL {
        override fun validarFormatoDaChave(valorChave: String?): Boolean {
            if (valorChave.isNullOrBlank()) {
                return false
            }
            return EmailValidator().run {
                initialize(null)
                isValid(valorChave, null)
            }
        }
    },
    ALEATORIA {
        override fun validarFormatoDaChave(valorChave: String?): Boolean {
            return valorChave.isNullOrBlank()
        }
    };

    abstract fun validarFormatoDaChave(valorChave: String?): Boolean
}
