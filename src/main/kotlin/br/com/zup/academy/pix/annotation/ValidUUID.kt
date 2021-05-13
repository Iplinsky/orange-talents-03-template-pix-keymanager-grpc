package br.com.zup.academy.pix.annotation

import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.reflect.KClass

/*
 * UUID can be capitalized, so the CASE_INSENSITIVE pattern has been set.
 */
@Pattern(
//    regexp = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}",
    regexp = "^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$",
    flags = [Pattern.Flag.CASE_INSENSITIVE]
)
@MustBeDocumented
@ReportAsSingleViolation
@Constraint(validatedBy = [])
@Retention(RUNTIME)
@Target(FIELD)
annotation class ValidUUID(
    val message: String = "Invalid UUID format",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
) {

}
