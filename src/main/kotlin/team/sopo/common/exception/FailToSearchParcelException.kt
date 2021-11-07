package team.sopo.common.exception

import team.sopo.common.exception.error.SopoError

/**
 * 외부 API 호출시에 미리 정의된 에러가 이닌 이유로 실패한 경우.
 */
class FailToSearchParcelException: SopoException{

    constructor(carrier: String,
                waybillNum: String,
                exception: Exception): super( SopoError.FAIL_TO_SEARCH_PARCEL, "배송사 : $carrier, 송장번호 : $waybillNum, exception : ${exception.javaClass.simpleName}, error msg : ${exception.message}")

    constructor(message: String): super( SopoError.FAIL_TO_SEARCH_PARCEL, message)
}