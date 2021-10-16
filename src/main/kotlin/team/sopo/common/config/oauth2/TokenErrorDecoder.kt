package team.sopo.common.config.oauth2

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import feign.Response
import feign.codec.ErrorDecoder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import team.sopo.common.exception.SopoOauthException

class TokenErrorDecoder: ErrorDecoder {

    private val logger: Logger = LogManager.getLogger(TokenErrorDecoder::class.java)

    override fun decode(methodKey: String, response: Response): Exception {
        logger.info("$methodKey 요청이 성공하지 못 했습니다. status : ${response.status()}, body : ${response.body()} ")

        val body = getErrorBody(response)
        logger.info("errorResponse : $body")

        throw SopoOauthException(body.error, body.error_description, body.sopoErrorCode.toInt())
    }

    private fun getErrorBody(response: Response): SopoExceptionDTO {
        return jacksonObjectMapper().readValue(response.body().asInputStream(), SopoExceptionDTO::class.java)
    }

}