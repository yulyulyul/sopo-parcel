package team.sopo.common.config.feign

import feign.Response
import feign.codec.ErrorDecoder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.springframework.http.HttpStatus
import team.sopo.common.exception.FailToSearchParcelException
import team.sopo.common.exception.ParcelNotFoundException

class DeliveryErrorDecoder: ErrorDecoder {

    private val logger: Logger = LogManager.getLogger(DeliveryErrorDecoder::class.java)

    override fun decode(methodKey: String, response: Response): Exception {
        logger.info("$methodKey 요청이 성공하지 못 했습니다. status : ${response.status()}, body : ${response.body()} ")

        when (response.status()) {

            HttpStatus.NOT_FOUND.value() -> throw ParcelNotFoundException("송장번호에 부합하는 택배를 찾지 못하였습니다.")

            else -> {
                logger.error("택배 조회 실패 : ${response.reason()}")
                throw FailToSearchParcelException(response.reason())
            }
        }
    }
}