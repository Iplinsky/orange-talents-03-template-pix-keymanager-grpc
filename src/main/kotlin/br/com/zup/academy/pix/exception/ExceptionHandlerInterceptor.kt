package br.com.zup.academy.pix.exception

import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * https://gist.github.com/rponte/8f23557d7e3079c9e644e620df293420
 * Class responsible for intercepting gRPC Endpoints and handling any exception thrown by their methods
 */
@Singleton
class ExceptionHandlerInterceptor(@Inject private val resolver: ExceptionHandlerResolver) : MethodInterceptor<BindableService, Any?> {
    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {
        return try {
            context.proceed()
        } catch (e: Exception) {
            /* Returns the appropriate exception handler */
            val handler = resolver.resolve(e)
            val status = handler.handle(e)

            GrpcEndpointArguments(context).response().onError(status.asRuntimeException())
            null
        }
    }

    /**
     * Represents the endpoint method arguments
     */
    private class GrpcEndpointArguments(val context: MethodInvocationContext<BindableService, Any?>) {
        fun response(): StreamObserver<*> {
            return context.parameterValues[1] as StreamObserver<*>
        }
    }
}