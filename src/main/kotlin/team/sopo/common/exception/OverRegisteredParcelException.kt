package team.sopo.common.exception

import team.sopo.common.exception.error.SopoError

class OverRegisteredParcelException: SopoException(SopoError.OVER_REGISTERED_PARCEL, SopoError.OVER_REGISTERED_PARCEL.message)