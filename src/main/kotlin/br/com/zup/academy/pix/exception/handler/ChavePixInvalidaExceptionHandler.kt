package br.com.zup.academy.pix.exception.handler

import br.com.zup.academy.pix.exception.ExceptionHandler
import br.com.zup.academy.pix.exception.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixInvalidaExceptionHandler : ExceptionHandler<ChavePixInvalidaException> {
    override fun handle(e: ChavePixInvalidaException): StatusWithDetails {
        return StatusWithDetails(
            Status.INVALID_ARGUMENT
                .withDescription(e.message)
                .withCause(e)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixInvalidaException
    }

}