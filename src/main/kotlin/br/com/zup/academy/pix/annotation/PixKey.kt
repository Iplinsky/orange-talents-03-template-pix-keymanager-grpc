package br.com.zup.academy.pix.annotation

import br.com.zup.academy.pix.chave.ChavePixRequest
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.TYPE
import kotlin.reflect.KClass

@MustBeDocumented
@Constraint(validatedBy = [PixKeyValidator::class])
@Retention(RUNTIME)
@Target(CLASS, TYPE)
annotation class PixKey(
    val message: String = "Invalid key Pix",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class PixKeyValidator : ConstraintValidator<PixKey, ChavePixRequest> {
    override fun isValid(
        value: ChavePixRequest?, annotationMetadata: AnnotationValue<PixKey>, context: ConstraintValidatorContext): Boolean {
        if (value?.tipoChavePix == null) {
            return false
        }
        return value.tipoChavePix.validarFormatoDaChave(value.valorChave)
    }

}


