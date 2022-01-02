package team.sopo.common.exception

import team.sopo.common.exception.error.SopoError

class ParcelBadRequestException(message: String = "송장 번호를 확인해주세요."): SopoException(SopoError.PARCEL_BAD_REQUEST, message)