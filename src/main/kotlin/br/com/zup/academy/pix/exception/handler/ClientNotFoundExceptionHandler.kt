package br.com.zup.academy.pix.exception.handler

import br.com.zup.academy.pix.exception.ExceptionHandler
import br.com.zup.academy.pix.exception.ExceptionHandler.StatusWithDetails
import io.grpc.Status
import javax.inject.Singleton

@Singleton
class ClientNotFoundExceptionHandler : ExceptionHandler<ClientNotFoundException> {
    override fun handle(ex: ClientNotFoundException): StatusWithDetails {
        return StatusWithDetails(
            Status.NOT_FOUND
                .withDescription(ex.message)
                .withCause(ex)
        )
    }

    override fun supports(e: Exception): Boolean {
        return e is ClientNotFoundException
    }

}
