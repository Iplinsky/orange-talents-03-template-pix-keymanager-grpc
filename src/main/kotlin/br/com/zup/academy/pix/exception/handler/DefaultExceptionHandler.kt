package br.com.zup.academy.pix.exception.handler

import br.com.zup.academy.pix.exception.ExceptionHandler
import br.com.zup.academy.pix.exception.ExceptionHandler.StatusWithDetails
import io.grpc.Status

/**
 * https://gist.github.com/rponte/949d947ac3c38aa7181929c41ee56c05#file-a04_defaultexceptionhandler-kt
 * By design, this class must NOT be managed by Micronaut
 */
class DefaultExceptionHandler : ExceptionHandler<Exception> {

    override fun handle(e: Exception): StatusWithDetails {
        val status = when (e) {
            is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message)
            else -> Status.UNKNOWN.withDescription(e.message)
        }
        return StatusWithDetails(status.withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return true
    }

}