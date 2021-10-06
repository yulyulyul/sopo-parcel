package team.sopo.common.exception

import team.sopo.common.exception.error.SopoError

class SystemException(message: String): SopoException(SopoError.SYSTEM_ERROR, message)