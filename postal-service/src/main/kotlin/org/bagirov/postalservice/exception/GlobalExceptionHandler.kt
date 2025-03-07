package org.bagirov.postalservice.exception

import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
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
            error = "Unexpected Error",
            message = ex.message,
            status = HttpStatus.BAD_REQUEST.value()
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

}