package chung.me.livechatapi.controller

import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ExceptionHandler : ResponseEntityExceptionHandler() {

  @ExceptionHandler(value = [ResponseStatusException::class])
  fun handleResponseStatusException(e: ResponseStatusException): ResponseEntity<ResponseErrorEntity> {
    if (logger.isInfoEnabled) {
      logger.info("", e)
    }

    val errorResponse = ResponseErrorEntity(e.statusCode.toString(), e.message)

    return ResponseEntity
      .status(e.statusCode)
      .body(errorResponse)
  }

  @ExceptionHandler(value = [Exception::class])
  fun handleException(e: RuntimeException, request: WebRequest): ResponseEntity<ResponseErrorEntity> {
    if (logger.isInfoEnabled) {
      logger.info("", e)
    }

    val rootCause = ExceptionUtils.getRootCause(e)

    val errorResponse = ResponseErrorEntity(HttpStatus.INTERNAL_SERVER_ERROR.name, rootCause.message ?: "")

    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(errorResponse)
  }
}

data class ResponseErrorEntity(
  val code: String,
  val message: String?,
)
