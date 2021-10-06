package team.sopo.common.exception

import team.sopo.common.exception.error.SopoError

class AlreadyRegisteredParcelException: SopoException(SopoError.ALREADY_REGISTERED_PARCEL, SopoError.ALREADY_REGISTERED_PARCEL.message)