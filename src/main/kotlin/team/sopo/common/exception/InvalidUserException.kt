package team.sopo.common.exception

import team.sopo.common.exception.error.SopoError

class InvalidUserException(message: String): SopoException(SopoError.INVALID_USER, message)