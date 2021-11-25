package team.sopo.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import team.sopo.common.exception.error.SopoError

@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
class UnauthorizedException(message: String): SopoException(SopoError.AUTHORIZE_FAIL, message)