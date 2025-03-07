package org.bagirov.publicationservice.exception

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.reflect.InvocationTargetException
import java.nio.file.AccessDeniedException

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log = KotlinLogging.logger {}

    // Класс для стандартного ответа об ошибке
    data class ErrorResponse(
        val error: String,
        val message: String?,
        val status: Int
    )

    // Перехватываем AuthenticationException
    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthenticationException(ex: AuthenticationException): ResponseEntity<ErrorResponse> {
        log.error {"AuthenticationException:  ${ex.printStackTrace()}" }

        val errorResponse = ErrorResponse(
            error = "The user is not authorized",
            message = ex.message,
            status = HttpStatus.UNAUTHORIZED.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    // Перехватываем ExpiredJwtException (если токен истёк)
    @ExceptionHandler(ExpiredJwtException::class)
    fun handleExpiredJwtException(ex: ExpiredJwtException): ResponseEntity<ErrorResponse> {
        log.error {"ExpiredJwtException:  ${ex.printStackTrace()}" }

        val errorResponse = ErrorResponse(
            error = "Token Expired",
            message = ex.message,
            status = HttpStatus.UNAUTHORIZED.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    // Перехватываем JwtException
    @ExceptionHandler(JwtException::class)
    fun handleJwtException(ex: JwtException): ResponseEntity<ErrorResponse> {
        log.error {"JwtException:  ${ex.printStackTrace()}" }

        val errorResponse = ErrorResponse(
            error = "Error with the token",
            message = ex.message,
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }
    // Перехватываем JwtException
    @ExceptionHandler(AccessDeniedException::class)
    fun handleJwtException(ex: AccessDeniedException): ResponseEntity<ErrorResponse> {
        log.error {"JwtException:  ${ex.printStackTrace()}" }

        val errorResponse = ErrorResponse(
            error = "Access denied",
            message = ex.message,
            status = HttpStatus.FORBIDDEN.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    // Перехватываем InvocationTargetException
    @ExceptionHandler(InvocationTargetException::class)
    fun handleIInvocationTargetException(ex: InvocationTargetException): ResponseEntity<ErrorResponse> {
        val rootCause = ex.targetException // Достаем настоящее исключение
        log.error("ROOT CAUSE InvocationTargetException: ${rootCause.message}", rootCause)

        log.error {"InvocationTargetException:  ${ex.printStackTrace()}" }
        val errorResponse = ErrorResponse(
            error = "Error when calling the method through reflection",
            message = ex.message,
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    // Перехватываем IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        log.error {"IllegalArgumentException:  ${ex.printStackTrace()}" }

        val errorResponse = ErrorResponse(
            error = "Error from an invalid argument",
            message = ex.message,
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    // Перехватываем все исключения типа NoSuchElementException
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(ex: NoSuchElementException): ResponseEntity<ErrorResponse> {
        log.error {"NoSuchElementException:  ${ex.printStackTrace()}" }

        val errorResponse = ErrorResponse(
            error = "No such element",
            message = ex.message,
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    // Перехватываем все исключения типа RuntimeException
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(ex: RuntimeException): ResponseEntity<ErrorResponse> {
        log.error {"RuntimeException:  ${ex.printStackTrace()}" }

        val errorResponse = ErrorResponse(
            error = "Internal Server Error",
            message = ex.message,
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    // Перехватываем любые Exception (общий случай)
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error {"Exception:  ${ex.printStackTrace()}" }

        val errorResponse = ErrorResponse(
            error = "Unexpected error",
            message = ex.message,
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }


}